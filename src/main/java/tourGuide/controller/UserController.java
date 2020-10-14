package tourGuide.controller;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tourGuide.domain.NearbyAttractionsForUser;
import tourGuide.domain.User;
import tourGuide.domain.UserPreferences;
import tourGuide.service.RewardsService;
import tourGuide.service.UserService;
import tripPricer.Provider;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    private Logger logger = LoggerFactory.getLogger(UserController.class);


    @Autowired
    UserService userService;

    @Autowired
    RewardsService rewardsService;

    /**
     * Returns user's information
     * This endpoint needs a url param that is the user's name
     * Will return a json object
     *
     * @param username is a string
     * @return the wanted user object
     */
    @RequestMapping("/user")
    public User getUserByUserName(@RequestParam String username) {
        logger.info("Call user endpoint (get user information) successfully");
        return userService.getUser(username);
    }

    /**
     * Returns the actual location of a single user
     * This endpoint needs a url param that is the user's name
     * Will return a json object
     *
     * @param username is a string
     * @return the wanted user's actual location (give longitude and latitude)
     */
    @RequestMapping("/location")
    public Location getLocation(@RequestParam String username) {
        VisitedLocation visitedLocation = userService.getUserLocation(getUser(username));
        logger.info("Call user endpoint (get user location) successfully");
        return visitedLocation.location;
    }

    /**
     * Returns a map of all application users with their actual locations
     * This endpoint doesn't need any parameter
     * Will return a map in json object with UserId and longitude and latitude per each
     *
     * @return all locations of users who are using TourGuide
     */
    @RequestMapping("/allCurrentLocations")
    public Map<String, Location> getAllCurrentLocations() {
        logger.info("Call user endpoint (get all users locations) successfully");
        return userService.getLastLocationOfUsers();
    }

    /**
     * Returns user's information
     *
     * @param username        is a string that has to be given in url parameters
     * @param userPreferences is json body that has to be informed into request body
     * @return the user object with his preferences updated
     */
    @PostMapping("/userPreferences")
    public User setUserPreferences(@RequestParam String username,
                                   @RequestBody UserPreferences userPreferences) {
        logger.info("Call user endpoint (set user's preferences) successfully");
        return userService.setUserPreferences(username, userPreferences);
    }

    /**
     * Returns a list of the 5 closest attractions for the given user
     *
     * @param username is a string
     * @return a json object containing userId, actual location and the 5 closest attractions
     */
    @RequestMapping("/nearbyAttractions")
    public NearbyAttractionsForUser getNearbyAttractions(@RequestParam String username) {
        logger.info("Call user endpoint (get user's nearby attractions) successfully");
        return userService.getNearByAttractionsForUser(username);
    }

    /**
     * Returns a list of providers that offers attractions tickets
     * Those proposals uses user's preferences to filter
     *
     * @param username is a string
     * @return a json object containing the name and informations about providers
     */
    @RequestMapping("/tripDeals")
    public List<Provider> getTripDeals(@RequestParam String username) {
        List<Provider> providers = userService.getTripDeals(getUser(username));
        logger.info("Call user endpoint (get user's personalized trip deals) successfully");
        return providers;
    }

    private User getUser(String username) {
        return userService.getUser(username);
    }

}