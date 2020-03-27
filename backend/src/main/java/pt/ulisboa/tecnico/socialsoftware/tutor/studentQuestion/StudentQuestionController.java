package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class StudentQuestionController {

    private StudentQuestionService studentQuestionService;

    StudentQuestionController(StudentQuestionService studentQuestionService) { this.studentQuestionService = studentQuestionService; }

    @PostMapping("/courses/{courseId}/studentQuestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public StudentQuestionDto submitQuestion(Principal principal, @PathVariable int courseId, @Valid @RequestBody StudentQuestionDto question) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        question.setStatus(StudentQuestion.Status.PENDING.name());
        return this.studentQuestionService.submitQuestion(courseId, question, user.getUsername());
    }

    @PutMapping("/studentQuestions/{questionId}/approve")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public StudentQuestionDto approveQuestion(@PathVariable Integer questionId, @RequestBody String justification) {

        return this.studentQuestionService.approveQuestion(questionId, justification);
    }

    @PutMapping("/studentQuestions/{questionId}/reject")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public StudentQuestionDto rejectQuestion(@PathVariable Integer questionId, @RequestBody String justification) {

        return this.studentQuestionService.rejectQuestion(questionId, justification);
    }

    @GetMapping("/courses/{courseId}/studentQuestions/status")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<StudentQuestionDto> getSubmittedQuestionsStats(Principal principal, @PathVariable int courseId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        return this.studentQuestionService.getSubmittedQuestionsStats(user.getUsername());
    }
}
