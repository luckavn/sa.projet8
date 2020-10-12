package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
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

    @Autowired
    UserService userService;

    @Autowired
    RewardsService rewardsService;

    //User
    @RequestMapping("/user")
    public User getUserByUserName(@RequestParam String username) {
        return userService.getUser(username);
    }

    //User location
    @RequestMapping("/location")
    public String getLocation(@RequestParam String username) {
        VisitedLocation visitedLocation = userService.getUserLocation(getUser(username));
        return JsonStream.serialize(visitedLocation.location);
    }

    //All users locations
    @RequestMapping("/allCurrentLocations")
    public Map<String, Location> getAllCurrentLocations() {
        return userService.getLastLocationOfUsers();
    }

    //User preferences
    @PostMapping("/userPreferences")
    public User setUserPreferences(@RequestParam String username,
                                   @RequestBody UserPreferences userPreferences) {
        return userService.setUserPreferences(username, userPreferences);
    }

    //User's nearby attractions
    @RequestMapping("/nearbyAttractions")
    public NearbyAttractionsForUser getNearbyAttractions(@RequestParam String username) {
        return userService.getNearByAttractionsForUser(username);
    }

    //User trip deals
    @RequestMapping("/tripDeals")
    public String getTripDeals(@RequestParam String username) {
        List<Provider> providers = userService.getTripDeals(getUser(username));
        return JsonStream.serialize(providers);
    }

    private User getUser(String username) {
        return userService.getUser(username);
    }

}