package tourGuide;

import gpsUtil.GpsUtil;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rewardCentral.RewardCentral;
import tourGuide.domain.NearbyAttractionsForUser;
import tourGuide.domain.User;
import tourGuide.domain.UserPreferences;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tripPricer.Provider;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestTourGuideService {
    String userName = "internalUser4";

    @Autowired
    TourGuideService tourGuideService;

    @Test
    public void getUserLocation() {
        //Initialize required instances
        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        //Add user and track
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
        tourGuideService.tracker.stopTracking();

        //Assert
        assertEquals(visitedLocation.userId, user.getUserId());
    }

    @Test
    public void addUser() {
        //Initialize required instances
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        //Add users to service
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        //Transfer created users
        User retrivedUser = tourGuideService.getUser(user.getUserName());
        User retrivedUser2 = tourGuideService.getUser(user2.getUserName());
        tourGuideService.tracker.stopTracking();

        //Assert
        assertEquals(user, retrivedUser);
        assertEquals(user2, retrivedUser2);
    }

    @Test
    public void getAllUsers() {
        //Initialize required instances
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        //Add users to service
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        //Get all users
        List<User> allUsers = tourGuideService.getAllUsers();
        tourGuideService.tracker.stopTracking();

        //Assert
        assertTrue(allUsers.contains(user));
        assertTrue(allUsers.contains(user2));
    }

    @Test
    public void trackUser() {
        //Initialize required instances
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        //Add user and track location
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);
        tourGuideService.tracker.stopTracking();

        //Assert
        assertEquals(user.getUserId(), visitedLocation.userId);
    }

    @Test
    public void getNearbyAttractions() {
        //Initialize required instances
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(10);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        //Get 5 nearby attraction for application's user
        NearbyAttractionsForUser nearbyAttractionsForUser = tourGuideService.getNearByAttractionsForUser(userName);
        tourGuideService.tracker.stopTracking();

        //Assert
        assertEquals(5, nearbyAttractionsForUser.getNearbyAttractionList().size());
    }

    @Test
    public void getTripDeals() {
        //Initialize required instances
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(10);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        //Create user
        UserPreferences userPreferences = new UserPreferences(1190.90, 3, 2, 2);
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user.setUserPreferences(userPreferences);

        //Get trip deals
        List<Provider> providers = tourGuideService.getTripDeals(user);
        tourGuideService.tracker.stopTracking();

        //Assert
        assertTrue(1 <= providers.size() && providers.size() <= 26);
    }

    @Test
    public void getLastLocationOfUsers() {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(10);
        TourGuideService tourGuideService = new TourGuideService(gpsUtil, rewardsService);

        Map<String, Location> lastLocation = tourGuideService.getLastLocationOfUsers();

        assertEquals(10, lastLocation.size());
    }
}
