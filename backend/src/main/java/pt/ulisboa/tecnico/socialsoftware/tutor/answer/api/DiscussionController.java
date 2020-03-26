package pt.ulisboa.tecnico.socialsoftware.tutor.answer.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.DiscussionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MessageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.DiscussionRepository;

import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@RestController
public class DiscussionController {

    @Autowired
    private AnswerService answerService;



    @PostMapping("/discussion/{questionAnswer}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DiscussionDto createDiscussion(@RequestBody DiscussionDto discussionDto,@PathVariable Integer questionAnswer) {
        return answerService.createDiscussion(questionAnswer,discussionDto);
    }

    @PostMapping("/discussion/{questionAnswer}/{messageDto}/submit")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DiscussionDto submitMessage(@RequestBody DiscussionDto discussionDto, @PathVariable Integer questionAnswer, @PathVariable MessageDto messageDto,Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if(user==null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return answerService.submitMessage(questionAnswer,user.getId(),discussionDto,messageDto);

    }

    @GetMapping("/discussion/{discussionDto}/visualize")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<MessageDto> visualizeDiscussion(@PathVariable Integer discussionDtoId, Principal principal){
        User user = (User) ((Authentication) principal).getPrincipal();
        if (user == null){ throw new TutorException(AUTHENTICATION_ERROR); }
        return answerService.displayDiscussion(user.getId(), discussionDtoId);
    }

}

