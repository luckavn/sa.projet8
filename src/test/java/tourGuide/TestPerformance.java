package tourGuide;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import rewardCentral.RewardCentral;
import tourGuide.domain.User;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class TestPerformance {

    @Test
    public void highVolumeTrackLocation() throws InterruptedException {
        //Initialize instances and thread pools
        Locale.setDefault(Locale.US);
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        UserService userService = new UserService(gpsUtil, rewardsService);
        ExecutorService executorService = Executors.newFixedThreadPool(1000);

        // Users incremented up to 100,000
        InternalTestHelper.setInternalUserNumber(100000);

        //Obtain all users
        List<User> allUsers = new ArrayList<>();
        allUsers = userService.getAllUsers();

        //Testing with asynchrone thread pools
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (User user : allUsers) {
            executorService.execute(new Runnable() {
                User user;
                UserService userService;

                public void run() {
                    this.userService.trackUserLocation(this.user);
                    System.out.println("ok passed");
                }

                public Runnable init(UserService userService, User user) {
                    this.userService = userService;
                    this.user = user;
                    return this;
                }
            }.init(userService, user));
        }
        executorService.shutdown();
        executorService.awaitTermination(15, TimeUnit.MINUTES);
        stopWatch.stop();
        userService.tracker.stopTracking();

        //Assert
        System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

    @Test
    public void highVolumeGetRewards() throws InterruptedException {
        //Initialize instances and thread pools
        GpsUtil gpsUtil = new GpsUtil();
        RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
        UserService userService = new UserService(gpsUtil, rewardsService);
        ExecutorService executorService = Executors.newFixedThreadPool(1000);

        // Users incremented up to 100,000
        InternalTestHelper.setInternalUserNumber(100000);

        //Testing with asynchrone thread pools
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Attraction attraction = gpsUtil.getAttractions().get(0);
        List<User> allUsers = new ArrayList<>();
        allUsers = userService.getAllUsers();

        allUsers.forEach(u -> {
            executorService.execute(new Runnable() {
                RewardsService rewardsService;
                User user;

                public void run() {
                    user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
                    this.rewardsService.calculateRewards(this.user);
                    assertTrue(user.getUserRewards().size() > 0);
                    System.out.println("ok passed");
                }

                public Runnable init(RewardsService rewardsService, User user) {
                    this.rewardsService = rewardsService;
                    this.user = user;
                    return this;
                }
            }.init(rewardsService, u));
        });

        executorService.shutdown();
        executorService.awaitTermination(20, TimeUnit.MINUTES);
        stopWatch.stop();
        userService.tracker.stopTracking();

        //Assert
        System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

}
