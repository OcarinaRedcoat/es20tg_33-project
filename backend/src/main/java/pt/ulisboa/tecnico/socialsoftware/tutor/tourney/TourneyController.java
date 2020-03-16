package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TourneyController {

    @Autowired
    private TourneyService tourneyService;

    @PostMapping("/tourneys/{userId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TourneyDto createTourney(@RequestBody TourneyDto tourneyDto, @PathVariable int userId){
        return tourneyService.createTourney(tourneyDto, userId);
    }


}
