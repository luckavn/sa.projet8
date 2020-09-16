package tourGuide.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    //Application
    @RequestMapping("/home")
    public String index() {
        return "Greetings from TourGuide!";
    }

}