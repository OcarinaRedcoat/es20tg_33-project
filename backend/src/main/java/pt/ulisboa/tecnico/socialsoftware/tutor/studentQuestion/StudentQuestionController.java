package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class StudentQuestionController {
    private static Logger logger = LoggerFactory.getLogger(StudentQuestionController.class);

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
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'STUDENT_QUESTION.ACCESS')")
    public StudentQuestionDto approveQuestion(@PathVariable Integer questionId, @RequestBody String justification) {

        return this.studentQuestionService.approveQuestion(questionId, justification);
    }

    @PutMapping("/studentQuestions/{questionId}/reject")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'STUDENT_QUESTION.ACCESS')")
    public StudentQuestionDto rejectQuestion(@PathVariable Integer questionId, @RequestBody String justification) {

        return this.studentQuestionService.rejectQuestion(questionId, justification);
    }

    @GetMapping("/courses/{courseId}/studentQuestions/status")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<StudentQuestionDto> getUserSubmittedQuestions(Principal principal, @PathVariable int courseId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if(user == null){
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        return this.studentQuestionService.getUserSubmittedQuestions(user.getUsername());
    }

    @PostMapping("/courses/{courseId}/questionsFromStudents")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public QuestionDto makeStudentQuestionAvailable(@PathVariable int courseId, @RequestBody int studentQuestionId) {

        return this.studentQuestionService.makeStudentQuestionAvailable(studentQuestionId);
    }

    @GetMapping("/courses/{courseId}/studentQuestions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<StudentQuestionDto> getPendingQuestions(@PathVariable int courseId) {

        return this.studentQuestionService.getSubmittedQuestions(courseId);
    }

    @DeleteMapping("/studentQuestions/{questionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionId, 'STUDENT_QUESTION.ACCESS')")
    public ResponseEntity deleteSubmittedQuestion(@PathVariable int questionId) {
        logger.debug("removeQuestion questionId: {}: ", questionId);

        studentQuestionService.removeStudentQuestion(questionId);

        return ResponseEntity.ok().build();
    }

    @PutMapping("studentQuestions/{questionId}/resubmit")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionId, 'STUDENT_QUESTION.ACCESS')")
    public StudentQuestionDto resubmitQuestion(@PathVariable int questionId, @Valid @RequestBody StudentQuestionDto question) {
        question.setStatus(StudentQuestion.Status.PENDING.name());
        return this.studentQuestionService.resubmitQuestion(questionId, question);
    }

    @PutMapping("studentQuestions/{questionId}/editApprovedQuestion")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'STUDENT_QUESTION.ACCESS')")
    public StudentQuestionDto editApprovedQuestion(@PathVariable int questionId, @Valid @RequestBody StudentQuestionDto questionChanges) {

        return this.studentQuestionService.editApprovedQuestion(questionId, questionChanges);
    }

    @GetMapping("/studentQuestions/dashboard")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public StudentQuestionStatsDto getStudentQuestionsDashboard(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return this.studentQuestionService.getStudentQuestionStats(user.getUsername());
    }

    @PutMapping("/student/studentQuestions/{privacy}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public boolean toggleStudentQuestionPrivacy(@PathVariable String privacy, Principal principal) {
        logger.debug("privacy: {}: ", privacy);

        User user = (User) ((Authentication) principal).getPrincipal();
        return this.studentQuestionService.toggleStudentQuestionPrivacy(privacy, user.getId());
    }
}
