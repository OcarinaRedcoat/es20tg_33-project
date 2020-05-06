package pt.ulisboa.tecnico.socialsoftware.tutor.answer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import pt.ulisboa.tecnico.socialsoftware.tutor.TutorApplication;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Message;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.DiscussionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MessageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuizAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MessageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.DiscussionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.MessageRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorExceptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlExport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class AnswerService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private AnswersXmlImport xmlImporter;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private MessageRepository messageRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuizAnswerDto createQuizAnswer(Integer userId, Integer quizId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);
        quizAnswerRepository.save(quizAnswer);

        return new QuizAnswerDto(quizAnswer);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<CorrectAnswerDto> concludeQuiz(User user, Integer quizId) {
        QuizAnswer quizAnswer = user.getQuizAnswers().stream().filter(qa -> qa.getQuiz().getId().equals(quizId)).findFirst().orElseThrow(() ->
                new TutorException(QUIZ_NOT_FOUND, quizId));

        if (quizAnswer.getQuiz().getAvailableDate() != null && quizAnswer.getQuiz().getAvailableDate().isAfter(DateHandler.now())) {
            throw new TutorException(QUIZ_NOT_YET_AVAILABLE);
        }

        if (!quizAnswer.isCompleted()) {
            quizAnswer.setAnswerDate(DateHandler.now());
            quizAnswer.setCompleted(true);
        }

        // In class quiz when student submits before resultsDate
        if (quizAnswer.getQuiz().getResultsDate() != null &&
            quizAnswer.getQuiz().getType().equals(Quiz.QuizType.IN_CLASS) &&
            DateHandler.now().isBefore(quizAnswer.getQuiz().getResultsDate())) {

            return new ArrayList<>();
        }

        return quizAnswer.getQuestionAnswers().stream()
                .sorted(Comparator.comparing(QuestionAnswer::getSequence))
                .map(CorrectAnswerDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void submitAnswer(User user, Integer quizId, StatementAnswerDto answer) {
        QuizAnswer quizAnswer = user.getQuizAnswers().stream()
                .filter(qa -> qa.getQuiz().getId().equals(quizId))
                .findFirst()
                .orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        QuestionAnswer questionAnswer = quizAnswer.getQuestionAnswers().stream()
                .filter(qa -> qa.getSequence().equals(answer.getSequence()))
                .findFirst()
                .orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, answer.getSequence()));

        if (isNotAssignedStudent(user, quizAnswer)) {
            throw new TutorException(QUIZ_USER_MISMATCH, String.valueOf(quizAnswer.getQuiz().getId()), user.getUsername());
        }

        if (quizAnswer.getQuiz().getConclusionDate() != null && quizAnswer.getQuiz().getConclusionDate().isBefore(DateHandler.now())) {
            throw new TutorException(QUIZ_NO_LONGER_AVAILABLE);
        }

        if (quizAnswer.getQuiz().getAvailableDate() != null && quizAnswer.getQuiz().getAvailableDate().isAfter(DateHandler.now())) {
            throw new TutorException(QUIZ_NOT_YET_AVAILABLE);
        }

        if (!quizAnswer.isCompleted()) {

            Option option;
            if (answer.getOptionId() != null) {
                option = optionRepository.findById(answer.getOptionId())
                        .orElseThrow(() -> new TutorException(OPTION_NOT_FOUND, answer.getOptionId()));

                if (isNotQuestionOption(questionAnswer.getQuizQuestion(), option)) {
                    throw new TutorException(QUESTION_OPTION_MISMATCH, questionAnswer.getQuizQuestion().getQuestion().getId(), option.getId());
                }

                if (questionAnswer.getOption() != null) {
                    questionAnswer.getOption().getQuestionAnswers().remove(questionAnswer);
                }

                questionAnswer.setOption(option);
                questionAnswer.setTimeTaken(answer.getTimeTaken());
                quizAnswer.setAnswerDate(DateHandler.now());
            }
        }
    }

    private boolean isNotQuestionOption(QuizQuestion quizQuestion, Option option) {
        return quizQuestion.getQuestion().getOptions().stream().map(Option::getId).noneMatch(value -> value.equals(option.getId()));
    }

    private boolean isNotAssignedStudent(User user, QuizAnswer quizAnswer) {
        return !user.getId().equals(quizAnswer.getUser().getId());
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String exportAnswers() {
        AnswersXmlExport xmlExport = new AnswersXmlExport();

        return xmlExport.export(quizAnswerRepository.findAll());
    }


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void importAnswers(String answersXml) {
        xmlImporter.importAnswers(answersXml, this, questionRepository, quizRepository, quizAnswerRepository, userRepository);
    }


    public void deleteQuizAnswer(QuizAnswer quizAnswer) {
        List<QuestionAnswer> questionAnswers = new ArrayList<>(quizAnswer.getQuestionAnswers());
        questionAnswers.forEach(questionAnswer ->
        {
            questionAnswer.remove();
            questionAnswerRepository.delete(questionAnswer);
        });
        quizAnswer.remove();
        quizAnswerRepository.delete(quizAnswer);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<DiscussionDto> findDiscussionById(int courseId){
        return discussionRepository.findByCourseId(courseId).stream().map(DiscussionDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<DiscussionDto> findByCourseIdandStudent(int courseId, String userName){
        User user = userRepository.findByUsername(userName);
        if (user == null){
            throw new TutorException(USER_NOT_FOUND , userName);
        }
        return discussionRepository.findByCourseIdandStudent(courseId, user).stream().map(DiscussionDto::new).collect(Collectors.toList());
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public DiscussionDto createDiscussion(int courseId, int quizAnswerId, String userName){
        User user = userRepository.findByUsername(userName);
        if (user == null){
            throw new TutorException(USER_NOT_FOUND , userName);
        }
        QuizAnswer quizAnswer = quizAnswerRepository.findById(quizAnswerId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND , quizAnswerId));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND , courseId));
        Discussion discussion = new Discussion(course, quizAnswer, user);
        discussionRepository.save(discussion);
        return new DiscussionDto(discussion);
    }




    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public MessageDto submitMessage(MessageDto messageDto, int discussionId, String userName) {
        Discussion discussion = discussionRepository.findById(discussionId).orElseThrow(() -> new TutorException(ErrorMessage.DISCUSSION_NOT_FOUND));

        User user = userRepository.findByUsername(userName);
        if (user == null){
            throw new TutorException(USER_NOT_FOUND , userName);
        }

        if (user.getRole().equals(User.Role.TEACHER)){
            discussion.changeSolved();
        } else if (user.getRole().equals(User.Role.STUDENT)){
            discussion.changeSolved();
        }

        messageDto.setName(user.getName());
        messageDto.setUserName(user.getUsername());
        messageDto.setDiscussionId(discussionId);

        Message message  = new Message(discussion, messageDto);

        messageRepository.save(message);
        return new MessageDto(message);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<MessageDto> seeMessages(int discussionId){
        Discussion discussion = discussionRepository.findById(discussionId).orElseThrow(() -> new TutorException(DISCUSSION_NOT_FOUND));
        return discussion.getDiscussionListMessages().stream().map(MessageDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public DiscussionDto makePublicDiscussion(int discussionId) {
        Discussion discussion = discussionRepository.findById(discussionId).orElseThrow(() -> new TutorException(DISCUSSION_NOT_FOUND));
        discussion.changeStatus();
        return new DiscussionDto(discussion);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<DiscussionDto> findPublicDiscussions(int courseId){
        return discussionRepository.findPublicDiscussions(courseId, Discussion.PublicStatus.PUBLIC.ordinal()).stream().map(DiscussionDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<DiscussionDto> findQuizAnswerPublicDiscussions(int courseId, int quizAnswerId){
        return discussionRepository.findQuizAnswerPublicDiscussions(courseId, quizAnswerId, Discussion.PublicStatus.PUBLIC.ordinal()).stream().map(DiscussionDto::new).collect(Collectors.toList());
    }

}
