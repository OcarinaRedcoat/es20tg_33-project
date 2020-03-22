package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.security.Principal;
import java.util.List;

@RestController
public class TourneyController {

    @Autowired
    private TourneyService tourneyService;

    @PostMapping("/tourneys")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TourneyDto createTourney(@RequestBody TourneyDto tourneyDto, Principal principal){
        User user = (User) ((Authentication) principal).getPrincipal();
        if(user==null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        tourneyDto.setTourneyStatus(Tourney.Status.OPEN);
        return tourneyService.createTourney(tourneyDto, user.getId());
    }

    @GetMapping("/tourneys/open")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<TourneyDto> getOpenTourneys(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return tourneyService.getOpenTourneys(user.getId());
    }

    @PutMapping("/tourneys/{tourneyId}/enroll")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TourneyDto getOpenTourneys(@PathVariable Integer tourneyId, Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return tourneyService.enrollStudent(tourneyId,user.getId());
    }

    @GetMapping("/tourneys/{tourneyId}/cancel")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tourneyId, 'TOURNEY.CREATOR')")
    public TourneyDto getOpenTourneys(@PathVariable Integer tourneyId) {
        return tourneyService.cancelTournament(tourneyId);
    }

}
