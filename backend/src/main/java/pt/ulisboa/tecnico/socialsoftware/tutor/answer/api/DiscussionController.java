package pt.ulisboa.tecnico.socialsoftware.tutor.answer.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Message;
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


    @PostMapping("/quizAnswer/createDisscussion/{courseId}/{userName}/{quizAnswerId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_DEMO_STUDENT')")
    public DiscussionDto createDiscussion(@PathVariable Integer quizAnswerId, @PathVariable Integer courseId, @PathVariable String userName) {
        return answerService.createDiscussion(courseId, quizAnswerId, userName);
    }

    @GetMapping("/discussion/{courseId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_DEMO_TEACHER')")
    public List<DiscussionDto> getDiscussion(@PathVariable Integer courseId){
        return answerService.findDiscussionById(courseId);
    }

    @PostMapping("/quizAnswer/{discussionId}/{userName}")
    @PostAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_DEMO_STUDENT') or hasRole('ROLE_TEACHER') or hasRole('ROLE_DEMO_TEACHER')")
    public MessageDto submitMessage(@PathVariable int discussionId, @PathVariable String userName, @RequestBody MessageDto messageDto){
        return answerService.submitMessage(messageDto, discussionId, userName);
    }

    @GetMapping("/quizAnswer/{discussionId}")
    @PostAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_DEMO_STUDENT') or hasRole('ROLE_TEACHER') or hasRole('ROLE_DEMO_TEACHER')")
    public List<MessageDto> seeMessages(@PathVariable int discussionId){
        return answerService.seeMessages(discussionId);
    }

    @GetMapping("/discussion/{courseId}/{userName}")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_DEMO_STUDENT')")
    public List<DiscussionDto> getDiscussionStudent(@PathVariable Integer courseId, @PathVariable String userName){
        return answerService.findByCourseIdandStudent(courseId, userName);
    }

    @PostMapping("/discussion/{discussionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_DEMO_TEACHER')")
    public DiscussionDto makePublicDiscussion(@PathVariable Integer discussionId){
        return answerService.makePublicDiscussion(discussionId);
    }

    @GetMapping("/discussion/public/{courseId}/{quizAnswerId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_DEMO_STUDENT')")
    public List<DiscussionDto> getQuizAnswerPublicDiscussions(@PathVariable Integer courseId, @PathVariable Integer quizAnswerId){
        return answerService.findQuizAnswerPublicDiscussions(courseId, quizAnswerId);
    }

    @GetMapping("/discussion/public/{courseId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_DEMO_STUDENT')")
    public List<DiscussionDto> getPublicDiscussions(@PathVariable Integer courseId){
        return answerService.findPublicDiscussions(courseId);
    }

}

