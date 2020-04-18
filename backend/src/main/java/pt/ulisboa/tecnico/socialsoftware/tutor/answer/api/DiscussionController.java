package pt.ulisboa.tecnico.socialsoftware.tutor.answer.api;

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


    /*@PostMapping("/question_answers/{questionAnswerId}/discussion")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DiscussionDto createDiscussion(@PathVariable Integer questionAnswerId, @RequestBody DiscussionDto discussionDto) {
        return answerService.createDiscussion(questionAnswerId,discussionDto);
    }*/

    @PostMapping("/courses/{courseId}/questionAnswer/{questionAnswerId}/discussion/submit")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public DiscussionDto submitStudentMessage(@PathVariable Integer questionAnswerId,@PathVariable Integer courseId,@RequestBody MessageDto messageDto, Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if(user==null){ throw new TutorException(AUTHENTICATION_ERROR); }
        return answerService.submitStudentMessage(user.getId(),courseId,questionAnswerId,messageDto);
    }

    @PostMapping("/discussion/{discussionId}/submit")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public DiscussionDto submitTeacherMessage(@PathVariable Integer discussionId,@RequestBody MessageDto messageDto,Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if(user==null){ throw new TutorException(AUTHENTICATION_ERROR); }
        return answerService.submitTeacherMessage(user.getId(),discussionId,messageDto);
    }

    @GetMapping("/visualize/{discussionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<MessageDto> visualizeDiscussion(@PathVariable Integer discussionId, Principal principal){
        User user = (User) ((Authentication) principal).getPrincipal();
        if (user == null){ throw new TutorException(AUTHENTICATION_ERROR); }
        return answerService.displayDiscussion(user.getId(), discussionId);
    }

    @GetMapping("/visualize/teacher/{courseId}")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<DiscussionDto> teacherVisualizesAllDiscussion(@PathVariable Integer courseId, Principal principal){
        User user = (User) ((Authentication) principal).getPrincipal();
        if (user == null){ throw new TutorException(AUTHENTICATION_ERROR); }
        return answerService.teacherVisualizesAllDiscussion(courseId);
    }
}

