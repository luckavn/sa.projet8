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
import tourGuide.service.UserService;
import tripPricer.Provider;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUserService {
    String userName = "internalUser4";

    @Autowired
    UserService userService;

    @Test
    public void getUserLocation() {
        //Initialize required instances
        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        UserService userService = new UserService(gpsUtil, rewardsService);

        //Add user and track
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = userService.trackUserLocation(user);
        userService.tracker.stopTracking();

        //Assert
        assertEquals(visitedLocation.userId, user.getUserId());
    }

    @Test
    public void addUser() {
        //Initialize required instances
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        UserService userService = new UserService(gpsUtil, rewardsService);

        //Add users to service
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);

        //Transfer created users
        User retrivedUser = userService.getUser(user.getUserName());
        User retrivedUser2 = userService.getUser(user2.getUserName());
        userService.tracker.stopTracking();

        //Assert
        assertEquals(user, retrivedUser);
        assertEquals(user2, retrivedUser2);
    }

    @Test
    public void getAllUsers() {
        //Initialize required instances
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        UserService userService = new UserService(gpsUtil, rewardsService);

        //Add users to service
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
        userService.addUser(user);
        userService.addUser(user2);

        //Get all users
        List<User> allUsers = userService.getAllUsers();
        userService.tracker.stopTracking();

        //Assert
        assertTrue(allUsers.contains(user));
        assertTrue(allUsers.contains(user2));
    }

    @Test
    public void trackUser() {
        //Initialize required instances
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        UserService userService = new UserService(gpsUtil, rewardsService);

        //Add user and track location
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = userService.trackUserLocation(user);
        userService.tracker.stopTracking();

        //Assert
        assertEquals(user.getUserId(), visitedLocation.userId);
    }

    @Test
    public void getNearbyAttractions() {
        //Initialize required instances
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(10);
        UserService userService = new UserService(gpsUtil, rewardsService);

        //Get 5 nearby attraction for application's user
        NearbyAttractionsForUser nearbyAttractionsForUser = userService.getNearByAttractionsForUser(userName);
        userService.tracker.stopTracking();

        //Assert
        assertEquals(5, nearbyAttractionsForUser.getNearbyAttractionList().size());
    }

    @Test
    public void getTripDeals() {
        //Initialize required instances
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(10);
        UserService userService = new UserService(gpsUtil, rewardsService);

        //Create user
        UserPreferences userPreferences = new UserPreferences(1190.90, 3, 2, 2);
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        user.setUserPreferences(userPreferences);

        //Get trip deals
        List<Provider> providers = userService.getTripDeals(user);
        userService.tracker.stopTracking();

        //Assert
        assertTrue(1 <= providers.size() && providers.size() <= 26);
    }

    @Test
    public void getLastLocationOfUsers() {
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        InternalTestHelper.setInternalUserNumber(10);
        UserService userService = new UserService(gpsUtil, rewardsService);

        Map<String, Location> lastLocation = userService.getLastLocationOfUsers();
        userService.tracker.stopTracking();

        assertEquals(10, lastLocation.size());
    }
}
