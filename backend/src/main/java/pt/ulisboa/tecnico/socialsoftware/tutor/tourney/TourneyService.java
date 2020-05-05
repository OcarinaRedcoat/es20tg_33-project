package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.Comparator.comparingInt;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND;

@Service
public class TourneyService {

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private TourneyRepository tourneyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TourneyDto> getOpenTourneys(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, userId));
        return tourneyRepository.findByStatus(Tourney.Status.OPEN.name()).stream()
                .filter(tourney -> (user.getCourseExecutions().stream()
                        .anyMatch(courseExec -> courseExec.getId().equals(tourney.getCourseExecution().getId()))))
                .map(TourneyDto::new).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TourneyDto createTourney(TourneyDto tourneyDto, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, userId));
        Tourney tourney = new Tourney(tourneyDto, user);

        if (tourney.getTitle() == null || tourney.getTitle().trim().isEmpty())
            throw new TutorException(ErrorMessage.TOURNEY_NOT_CONSISTENT, "title");
        if (tourney.getAvailableDate() == null)
            throw new TutorException(ErrorMessage.TOURNEY_NOT_CONSISTENT, "availableDate");
        if (tourney.getConclusionDate() == null)
            throw new TutorException(ErrorMessage.TOURNEY_NOT_CONSISTENT, "conclusionDate");
        try {
            if (DateHandler.toLocalDateTime(tourney.getConclusionDate())
                    .isBefore(DateHandler.toLocalDateTime(tourney.getAvailableDate())))
                throw new TutorException(ErrorMessage.TOURNEY_AVAILABLEDATE_BIGGER_THAN_CONCLUSIONDATE);
        } catch (NullPointerException e) {
            throw new TutorException(ErrorMessage.TOURNEY_DATE_WRONG_FORMAT);
        }
        if (tourney.getNumberOfQuestions() == null || tourney.getNumberOfQuestions() <= 0)
            throw new TutorException(ErrorMessage.TOURNEY_NOT_CONSISTENT, "numberOfQuestions");

        CourseDto courseExecutionDto = tourneyDto.getTourneyCourseExecution();
        if (courseExecutionDto == null)
            throw new TutorException(ErrorMessage.TOURNEY_NOT_CONSISTENT, "courseExecution");

        tourney.setCourseExecution(courseExecutionRepository.findById(courseExecutionDto.getCourseExecutionId())
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND,
                        courseExecutionDto.getCourseExecutionId())));

        tourney.setTopics(tourneyDto
                .getTourneyTopics().stream().map(topicDto -> topicRepository
                        .findTopicByName(tourney.getCourseExecution().getCourse().getId(), topicDto.getName()))
                .collect(Collectors.toList()));

        for (TopicDto topicDto : tourneyDto.getTourneyTopics()) {
            Topic topic = topicRepository.findTopicByName(tourney.getCourseExecution().getCourse().getId(),
                    topicDto.getName());
            topic.addTourney(tourney);
        }

        if (tourney.getTopics().size() == 0)
            throw new TutorException(ErrorMessage.TOURNEY_NOT_CONSISTENT, "topics");
        if (!user.getCourseExecutions().stream()
                .map((courseExecution) -> (courseExecution.getId().equals(tourney.getCourseExecution().getId())))
                .reduce(false, (acc, elem) -> acc || elem))
            throw new TutorException(ErrorMessage.STUDENT_CANT_ACCESS_COURSE_EXECUTION,
                    tourney.getCourseExecution().getAcronym());

        if (tourney.getTopics().stream().map(Objects::isNull).reduce(false, (acc, elem) -> acc || elem))
            throw new TutorException(ErrorMessage.TOPICS_NOT_FROM_SAME_COURSE);

        user.addCreatedTourneys(tourney);
        entityManager.persist(tourney);
        return new TourneyDto(tourney);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TourneyDto enrollStudent(Integer tourneyId, Integer studentId) {
        Tourney tourney = tourneyRepository.findById(tourneyId)
                .orElseThrow(() -> new TutorException(ErrorMessage.TOURNEY_NOT_FOUND));
        User user = userRepository.findById(studentId)
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, studentId));

        if (tourney.getStatus() == Tourney.Status.CLOSED) {
            throw new TutorException(ErrorMessage.TOURNEY_CLOSED);
        }

        if (user.getRole() != User.Role.STUDENT) {
            throw new TutorException(ErrorMessage.USER_NOT_STUDENT);
        }

        if (!user.getCourseExecutions().stream()
                .map((courseExecution) -> (courseExecution.getId().equals(tourney.getCourseExecution().getId())))
                .reduce(false, (acc, elem) -> acc || elem))
            throw new TutorException(ErrorMessage.STUDENT_CANT_ACCESS_COURSE_EXECUTION,
                    tourney.getCourseExecution().getAcronym());

        if (tourney.getEnrolledStudents().stream().anyMatch(elem -> user.getId() == elem.getId())) {
            throw new TutorException(ErrorMessage.STUDENT_ALREADY_ENROLLED);
        }

        if (tourney.getEnrolledStudents().size() == 1) {
            this.createTourneyQuiz(tourney);
        }

        tourney.enrollStudent(user);
        user.addEnrolledTourneys(tourney);

        return new TourneyDto(tourney);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TourneyDto createTourneyQuiz(Tourney tourney) {
        TourneyDto tourneyDto = new TourneyDto(tourney);
        QuizDto quizDto = new QuizDto();
        String creationDate = DateHandler.toISOString(DateHandler.now());
        String availableDate = tourney.getAvailableDate();
        String conclusionDate = tourney.getConclusionDate();
        quizDto.setTitle(tourney.getTitle() + " - Quiz");
        quizDto.setType(Quiz.QuizType.TOURNEY.name());
        quizDto.setScramble(true);
        quizDto.setOneWay(true);
        quizDto.setQrCodeOnly(true);
        quizDto.setCreationDate(creationDate);
        quizDto.setAvailableDate(availableDate);
        quizDto.setConclusionDate(conclusionDate);

        List<Question> questions = new ArrayList<>();
        List<Integer> selectedQuestions = new ArrayList<>();
        int numberOfQuestions = tourney.getNumberOfQuestions();
        List<Topic> topics = tourney.getTopics();
        for (Topic topic : topics) {
            questions.addAll(topic.getQuestions());
        }
        int i = 0;
        int maxQuestions = numberOfQuestions < questions.size() ? numberOfQuestions : questions.size();
        int nQuestions = questions.size();
        while (i < maxQuestions) {
            int index = (int) (Math.random() * nQuestions);
            if (!selectedQuestions.contains(index)) {
                selectedQuestions.add(index);
                i++;
            }
        }

        quizDto.setTourney(tourneyDto);

        quizDto = quizService.createQuiz(tourney.getCourseExecution().getId(), quizDto);
        Quiz quiz = quizRepository.findByKey(quizDto.getKey())
                .orElseThrow(() -> new TutorException(ErrorMessage.QUIZ_NOT_FOUND));

        for (int j = 0; j < selectedQuestions.size(); j++) {
            Question question = questions.get(selectedQuestions.get(j));
            QuizQuestion quizQuestion = new QuizQuestion(quiz, question, j);
            quizQuestionRepository.save(quizQuestion);
        }

        tourney.setQuiz(quiz);

        return new TourneyDto(tourney);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TourneyDto cancelTournament(Integer tourneyId) {
        Tourney tourney = tourneyRepository.findById(tourneyId)
                .orElseThrow(() -> new TutorException(ErrorMessage.TOURNEY_NOT_FOUND));

        tourney.setStatus(Tourney.Status.CANCELED);

        return new TourneyDto(tourney);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TourneyStatsDto> getTourneyDashboard(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, userId));

        List<QuizAnswer> tourneyAnswers = user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getType().equals(Quiz.QuizType.TOURNEY))
                .collect(Collectors.toList());

        List<TourneyStatsDto> tourneyStatsList = new ArrayList<>();
        for (QuizAnswer quizAnswer : tourneyAnswers) {
            TourneyStatsDto tourneyStats = new TourneyStatsDto();
            tourneyStats.setId(quizAnswer.getId());
            tourneyStats.setTitle(quizAnswer.getQuiz().getTourney().getTitle());
            tourneyStats.setCompletionDate(DateHandler.toISOString(quizAnswer.getAnswerDate()));
            int correct = (int) quizAnswer.getQuestionAnswers().stream().collect(collectingAndThen(
                    toCollection(() -> new TreeSet<>(
                            comparingInt(questionAnswer -> questionAnswer.getQuizQuestion().getQuestion().getId()))),
                    ArrayList::new)).stream().map(QuestionAnswer::getOption).filter(Objects::nonNull)
                    .filter(Option::getCorrect).count();
            tourneyStats.setScore(correct + "/" + quizAnswer.getQuestionAnswers().size());
            tourneyStatsList.add(tourneyStats);
        }

        return tourneyStatsList;
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StatementQuizDto getTourneyQuizAnswer(Integer tourneyId, Integer userId) {
        Tourney tourney = tourneyRepository.findById(tourneyId)
                .orElseThrow(() -> new TutorException(ErrorMessage.TOURNEY_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        Quiz quiz = tourney.getQuiz();
        LocalDateTime now = DateHandler.now();

        Integer executionId = tourney.getCourseExecution().getId();

        Set<Integer> studentQuizIds = user.getQuizAnswers().stream()
                .filter(quizAnswer -> quizAnswer.getQuiz().getCourseExecution().getId() == executionId)
                .map(QuizAnswer::getQuiz).map(Quiz::getId).collect(Collectors.toSet());

        QuizAnswer quizAnswer;

        // create QuizAnswer for quizzes
        if (quiz != null && !studentQuizIds.contains(quiz.getId()) && quiz.getConclusionDate().isAfter(now)) {
            quizAnswer = new QuizAnswer(user, quiz);
            quizAnswerRepository.save(quizAnswer);
            return new StatementQuizDto(quizAnswer);
        }

        return new StatementQuizDto();
    }

}
