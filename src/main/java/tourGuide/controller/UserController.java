package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tourGuide.domain.NearbyAttractionsForUser;
import tourGuide.domain.User;
import tourGuide.domain.UserPreferences;
import tourGuide.service.UserService;
import tripPricer.Provider;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    //User
    @RequestMapping("/user")
    public User getUserByUserName(@RequestParam String userName) {
        return userService.getUser(userName);
    }

    //User location
    @RequestMapping("/location")
    public String getLocation(@RequestParam String userName) {
        VisitedLocation visitedLocation = userService.getUserLocation(getUser(userName));
        return JsonStream.serialize(visitedLocation.location);
    }

    //All users locations
    @RequestMapping("/allCurrentLocations")
    public Map<String, Location> getAllCurrentLocations() {
        return userService.getLastLocationOfUsers();
    }

    //User preferences
    @PostMapping("/userPreferences")
    public User setUserPreferences(@RequestParam String userName,
                                   @RequestBody UserPreferences userPreferences) {
        return userService.setUserPreferences(userName, userPreferences);
    }

    //User's nearby attractions
    @RequestMapping("/nearbyAttractions")
    public NearbyAttractionsForUser getNearbyAttractions(@RequestParam String userName) {
        return userService.getNearByAttractionsForUser(userName);
    }

    //User rewards
    @RequestMapping("/rewards")
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(userService.getUserRewards(getUser(userName)));
    }

    //User trip deals
    @RequestMapping("/tripDeals")
    public String getTripDeals(@RequestParam String userName) {
        List<Provider> providers = userService.getTripDeals(getUser(userName));
        return JsonStream.serialize(providers);
    }

    private User getUser(String userName) {
        return userService.getUser(userName);
    }

}