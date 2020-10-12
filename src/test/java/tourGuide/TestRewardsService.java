package tourGuide;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.junit.Test;
import rewardCentral.RewardCentral;
import tourGuide.domain.User;
import tourGuide.domain.UserReward;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.UserService;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestRewardsService {

    @Test
    public void userGetRewards() {
        //Initialize required instances
        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        UserService userService = new UserService(gpsUtil, rewardsService);

        //Create user and test getting rewards
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        Attraction attraction = gpsUtil.getAttractions().get(0);
        user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
        userService.trackUserLocation(user);
        List<UserReward> userRewards = user.getUserRewards();
        userService.tracker.stopTracking();

        //Assert
        assertTrue(userRewards.size() == 1);
    }

    @Test
    public void isWithinAttractionProximity() {
        //Initialize required instances
        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());

        //Get attraction proximity
        Attraction attraction = gpsUtil.getAttractions().get(0);

        //Assert
        assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
    }

    @Test
    public void nearAllAttractions() {
        //Initialize required instances
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        rewardsService.setProximityBuffer(Integer.MAX_VALUE);
        InternalTestHelper.setInternalUserNumber(1);
        UserService userService = new UserService(gpsUtil, rewardsService);
        userService.tracker.stopTracking();

        //Testing
        rewardsService.calculateRewards(userService.getAllUsers().get(0));
        List<UserReward> userRewards = userService.getUserRewards(userService.getAllUsers().get(0));

        //Assert
        assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
    }

}
