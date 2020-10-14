package tourGuide.controller;

        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    private Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * Returns the home application endpoint
     *
     * @return string "Greetings from TourGuide!"
     */
    @RequestMapping("/home")
    public String index() {
        logger.info("Call home controller successfully");
        return "Greetings from TourGuide!";
    }

}