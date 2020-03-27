package pt.ulisboa.tecnico.socialsoftware.tutor.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.UsersXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.TourneyDto;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    public User findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    public User findByKey(Integer key) {
        return this.userRepository.findByKey(key);
    }

    public Integer getMaxUserNumber() {
        Integer result = userRepository.getMaxUserNumber();
        return result != null ? result : 0;
    }

    public User createUser(String name, String username, User.Role role) {

        if (findByUsername(username) != null) {
            throw new TutorException(DUPLICATE_USER, username);
        }

        User user = new User(name, username, getMaxUserNumber() + 1, role);
        userRepository.save(user);
        return user;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String getEnrolledCoursesAcronyms(String username) {
        User user =  this.userRepository.findByUsername(username);

        return user.getEnrolledCoursesAcronyms();
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<CourseDto> getCourseExecutions(String username) {
        User user =  this.userRepository.findByUsername(username);

        return user.getCourseExecutions().stream().map(CourseDto::new).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TourneyDto> getCreatedTourneys(String username) {
        User user =  this.userRepository.findByUsername(username);

        return user.getCreatedTourneys().stream().map(TourneyDto::new).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void addCourseExecution(String username, int executionId) {

        User user =  this.userRepository.findByUsername(username);

        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        user.addCourse(courseExecution);
        courseExecution.addUser(user);
    }

    public String exportUsers() {
        UsersXmlExport xmlExporter = new UsersXmlExport();

       return xmlExporter.export(userRepository.findAll());
    }


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void importUsers(String usersXML) {
        UsersXmlImport xmlImporter = new UsersXmlImport();

        xmlImporter.importUsers(usersXML, this);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User getDemoTeacher() {
        return this.userRepository.findByUsername("Demo-Teacher");
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User getDemoStudent() {
        return this.userRepository.findByUsername("Demo-Student");
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User createDemoStudent() {
        String birthDate = LocalDateTime.now().toString();
        User newDemoUser = createUser("Demo-Student-" + birthDate, "Demo-Student-" + birthDate, User.Role.STUDENT);

        User demoUser = this.userRepository.findByUsername("Demo-Student");

        CourseExecution courseExecution = demoUser.getCourseExecutions().stream().findAny().orElse(null);

        if (courseExecution != null) {
            courseExecution.addUser(newDemoUser);
            newDemoUser.addCourse(courseExecution);
        }

        return newDemoUser;
    }


    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public User getDemoAdmin() {
        return this.userRepository.findByUsername("Demo-Admin");
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuestionDto> getSubmittedQuestionsStats(String username) {
        User user = userRepository.findByUsername(username);
        checkUserFound(user);
        user.clearSubmittedQuestionsStatus();
        checkSubmittedQuestions(user);
        List<Question> questions = user.getSubmittedQuestions();
        countUserQuestions(user, questions);

        return questions.stream().map(QuestionDto::new).collect(Collectors.toList());
    }

    private void countUserQuestions(User user, List<Question> questions) {
        for (Question question : questions) {
            if (question.getStatus() == Question.Status.PENDING)
                user.increaseNumberOfSubmittedQuestions();
            else if (question.getStatus() == Question.Status.REJECTED) {
                user.increaseNumberOfSubmittedQuestions();
                user.increaseNumberOfRejectedQuestions();
            } else {
                user.increaseNumberOfSubmittedQuestions();
                user.increaseNumberOfApprovedQuestions();
            }
        }
    }

    private void checkSubmittedQuestions(User user) {
        if (user.getSubmittedQuestions().isEmpty())
            throw new TutorException(USER_WITHOUT_SUBMITTED_QUESTIONS);
    }

    private void checkUserFound(User user) {
        if (user == null)
            throw new TutorException(USERNAME_NOT_FOUND);
    }
}
