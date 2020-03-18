package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TourneyController {

    @Autowired
    private TourneyService tourneyService;

    @PostMapping("/tourneys/{userId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TourneyDto createTourney(@RequestBody TourneyDto tourneyDto, @PathVariable int userId){
        return tourneyService.createTourney(tourneyDto, userId);
    }

    @GetMapping("/tourneys/open")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<TourneyDto> getOpenTourneys() {
        // TODO: Student should only see tourneys from his course execution
        return tourneyService.getOpenTourneys();
    }

}
