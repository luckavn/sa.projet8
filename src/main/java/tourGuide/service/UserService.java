package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.domain.*;
import tourGuide.tracker.Tracker;
import tourGuide.utils.TourGuideUtils;
import tourGuide.utils.UserUtils;
import tripPricer.Provider;
import tripPricer.TripPricer;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final GpsUtil gpsUtil;
    private final RewardsService rewardsService;
    private TourGuideUtils tourGuideUtils = new TourGuideUtils();
    private UserUtils userUtils = new UserUtils();
    private final TripPricer tripPricer = new TripPricer();
    public final Tracker tracker;
    private boolean testMode = true;

    public UserService(GpsUtil gpsUtil, RewardsService rewardsService) {
        this.gpsUtil = gpsUtil;
        this.rewardsService = rewardsService;

        if (testMode) {
            Logger logger = LoggerFactory.getLogger(UserService.class);
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            userUtils.initializeInternalUsers();
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
        return userUtils.internalUserMap.get(userName);
    }

    public List<User> getAllUsers() {
        return userUtils.internalUserMap.values().stream().collect(Collectors.toList());
    }

    public List<String> getAllUsersID() {
        List<String> userId = new ArrayList<>();
        List<User> users = new ArrayList<>(userUtils.getInternalUserMap().values());
        for (User user : users) {
            userId.add(user.getUserId().toString());
        }
        return userId;
    }

    public void addUser(User user) {
        if (!userUtils.internalUserMap.containsKey(user.getUserName())) {
            userUtils.internalUserMap.put(user.getUserName(), user);
        }
    }

    public List<Provider> getTripDeals(User user) {
        int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
        List<Provider> providers = tripPricer.getPrice(userUtils.tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
        user.setTripDeals(providers);

        List<Provider> providersResult = new ArrayList<>();
        for (Provider provider : providers) {
            if (provider.price <= user.getUserPreferences().getHighPricePoint()) {
                providersResult.add(provider);
            }
        }
        user.setTripDeals(providersResult);
        return providersResult;
    }

    public VisitedLocation trackUserLocation(User user) {
        VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
        user.addToVisitedLocations(visitedLocation);
        rewardsService.calculateRewards(user);
        return visitedLocation;
    }

    public Map<String, Location> getLastLocationOfUsers() {
        Map<String, Location> lastLocation = new HashMap<>();
        userUtils.getInternalUserMap().values()
                .stream().forEach(
                user -> lastLocation.put(
                        user.getUserId().toString(),
                        user.getVisitedLocations().get(user.getVisitedLocations().size() - 1).location));
        return lastLocation;
    }

    public NearbyAttractionsForUser getNearByAttractionsForUser(String userName) {
        NearbyAttractionsForUser nearbyAttractionsForUser = new NearbyAttractionsForUser();
        List<Attractions> nearbyAttractionList = new ArrayList<>();
        VisitedLocation visitedLocation = getUserLocation(getUser(userName));

        nearbyAttractionsForUser.setUserLat(visitedLocation.location.latitude);
        nearbyAttractionsForUser.setUserLong(visitedLocation.location.longitude);

        //Calculate attraction and distances
        for (Attraction attraction : gpsUtil.getAttractions()) {
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

    public User setUserPreferences(String userName, UserPreferences preferences) {
        User user = getUser(userName);
        UserPreferences userPreferences = new UserPreferences(
                preferences.getAttractionProximity(),
                preferences.getCurrency(),
                preferences.getHighPricePoint(),
                preferences.getLowerPricePoint(),
                preferences.getNumberOfAdults(),
                preferences.getNumberOfChildren(),
                preferences.getTicketQuantity(),
                preferences.getTripDuration());

        user.setUserPreferences(userPreferences);
        userUtils.getInternalUserMap().put(String.valueOf(user.getUserId()), user);
        return user;
    }

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                tracker.stopTracking();
            }
        });
    }

}
