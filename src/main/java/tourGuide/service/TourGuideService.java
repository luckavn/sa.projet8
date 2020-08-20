package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.domain.*;
import tourGuide.helper.InternalTestHelper;
import tourGuide.tracker.Tracker;
import tourGuide.utils.TourGuideUtils;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
	private TourGuideUtils tourGuideUtils = new TourGuideUtils();
	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;
	boolean testMode = true;


	public TourGuideService(GpsUtil gpsUtil, RewardsService rewardsService) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		
		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}
	
	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}
	
	public VisitedLocation getUserLocation(User user) {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
			user.getLastVisitedLocation() :
			trackUserLocation(user);
		return visitedLocation;
	}
	
	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}
	
	public List<User> getAllUsers() {
		return internalUserMap.values().stream().collect(Collectors.toList());
	}
	
	public void addUser(User user) {
		if(!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}
	
	public List<Provider> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(), 
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}
	
	public VisitedLocation trackUserLocation(User user) {
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
		List<Attraction> nearbyAttractions = new ArrayList<>();
		for(Attraction attraction : gpsUtil.getAttractions()) {
			if(rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
				nearbyAttractions.add(attraction);
			}
		}
		return nearbyAttractions;
	}

	public NearbyAttractionsForUser getNearByAttractionsForUser(String userName) {
		NearbyAttractionsForUser nearbyAttractionsForUser = new NearbyAttractionsForUser();
		List<Attractions> nearbyAttractionList = new ArrayList<>();
		VisitedLocation visitedLocation = getUserLocation(getUser(userName));

		nearbyAttractionsForUser.setUserLat(visitedLocation.location.latitude);
		nearbyAttractionsForUser.setUserLong(visitedLocation.location.longitude);

		//Calculate attraction and distances
		for(Attraction attraction : gpsUtil.getAttractions()) {
			Location attractionLocation = new Location(attraction.longitude, attraction.latitude);
			Double distance = rewardsService.getDistance(attractionLocation, visitedLocation.location);
			Attractions nearbyAttraction = new Attractions(attraction, attraction.latitude, attraction.longitude, distance, 0);

			nearbyAttractionList.add(nearbyAttraction);
		}

		//Sort attraction by distance
		List<Attractions> sortedList = nearbyAttractionList.stream()
				.sorted(Comparator.comparingDouble(Attractions::getDistance))
				.collect(Collectors.toList());

		//Keep 5 first attractions
		List<Attractions> wipList = new ArrayList<>();
		wipList.add(sortedList.get(0));
		wipList.add(sortedList.get(1));
		wipList.add(sortedList.get(2));
		wipList.add(sortedList.get(3));
		wipList.add(sortedList.get(4));

		//Calculate rewards points
		wipList.get(0).setRewardsPoints(rewardsService.getRewardPoints(wipList.get(0).getAttraction(), getUser(userName)));
		wipList.get(1).setRewardsPoints(rewardsService.getRewardPoints(wipList.get(0).getAttraction(), getUser(userName)));
		wipList.get(2).setRewardsPoints(rewardsService.getRewardPoints(wipList.get(0).getAttraction(), getUser(userName)));
		wipList.get(3).setRewardsPoints(rewardsService.getRewardPoints(wipList.get(0).getAttraction(), getUser(userName)));
		wipList.get(4).setRewardsPoints(rewardsService.getRewardPoints(wipList.get(0).getAttraction(), getUser(userName)));


		//Create new object
		List<NearbyAttractions> finalList = tourGuideUtils.createFinalList(wipList);

		nearbyAttractionsForUser.setNearbyAttractionList(finalList);
		return nearbyAttractionsForUser;
	}
	
	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() { 
		      public void run() {
		        tracker.stopTracking();
		      } 
		    }); 
	}
	
	/**********************************************************************************
	 * 
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new HashMap<>();
	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);
			
			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}

	public Map<String, Location> getLastLocationOfUsers() {
		Map<String, Location> lastLocation = new HashMap<>();
		getInternalUserMap().values()
				.stream().forEach(
				user -> lastLocation.put(
						user.getUserId().toString(),
						user.getVisitedLocations().get(user.getVisitedLocations().size()-1).location));
		return lastLocation;
	}
	
	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i-> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}
	
	private double generateRandomLongitude() {
		double leftLimit = -180;
	    double rightLimit = 180;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
	    double rightLimit = 85.05112878;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
	    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

	public Map<String, User> getInternalUserMap() {
		return internalUserMap;
	}
}
