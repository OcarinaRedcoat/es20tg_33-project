package pt.ulisboa.tecnico.socialsoftware.tutor.question;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.LatexQuestionExportVisitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.QuestionsXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.XMLQuestionExportVisitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ImageRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_NOT_PENDING;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USERNAME_NOT_FOUND;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_MISSING_JUSTIFICATION;

@Service
public class QuestionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ImageRepository imageRepository;

    @PersistenceContext
    EntityManager entityManager;
    
    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionDto findQuestionById(Integer questionId) {
        return questionRepository.findById(questionId).map(QuestionDto::new)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findQuestionCourse(Integer questionId) {
        return questionRepository.findById(questionId)
                .map(Question::getCourse)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionDto findQuestionByKey(Integer key) {
        return questionRepository.findByKey(key).map(QuestionDto::new)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, key));
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuestionDto> findQuestions(int courseId) {
        return questionRepository.findQuestions(courseId).stream().map(QuestionDto::new).collect(Collectors.toList());
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuestionDto> findAvailableQuestions(int courseId) {
        return questionRepository.findAvailableQuestions(courseId).stream().map(QuestionDto::new).collect(Collectors.toList());
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionDto createQuestion(int courseId, QuestionDto questionDto) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));

        checkQuestionKey(questionDto);
        if (questionDto.getCreationDate() == null) {
            questionDto.setCreationDate(LocalDateTime.now().format(Course.formatter));
        }

        Question question = new Question(course, questionDto);
        question.setCreationDate(LocalDateTime.now());
        questionRepository.save(question);
        return new QuestionDto(question);
    }


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionDto updateQuestion(Integer questionId, QuestionDto questionDto) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
        question.update(questionDto);
        return new QuestionDto(question);
    }


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeQuestion(Integer questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
        question.remove();
        questionRepository.delete(question);
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void questionSetStatus(Integer questionId, Question.Status status) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
        question.setStatus(status);
    }


    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void uploadImage(Integer questionId, String type) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));

        Image image = question.getImage();

        if (image == null) {
            image = new Image();

            question.setImage(image);

            imageRepository.save(image);
        }

        question.getImage().setUrl(question.getCourse().getName().replaceAll("\\s", "") +
                question.getCourse().getType() +
                question.getKey() +
                "." + type);
    }

    @Retryable(
      value = { SQLException.class },
      backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateQuestionTopics(Integer questionId, TopicDto[] topics) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));

        question.updateTopics(Arrays.stream(topics).map(topicDto -> topicRepository.findTopicByName(question.getCourse().getId(), topicDto.getName())).collect(Collectors.toSet()));
    }

    public String exportQuestionsToXml() {
        XMLQuestionExportVisitor xmlExporter = new XMLQuestionExportVisitor();

        return xmlExporter.export(questionRepository.findAll());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void importQuestionsFromXml(String questionsXML) {
        QuestionsXmlImport xmlImporter = new QuestionsXmlImport();

        xmlImporter.importQuestions(questionsXML, this, courseRepository);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionDto submitQuestion(int courseId, QuestionDto questionDto, String username) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new TutorException(USERNAME_NOT_FOUND);

        checkQuestionKey(questionDto);

        Question question = new Question(course, questionDto);
        question.setCreationDate(LocalDateTime.now());
        checkIfPending(question);
        question.setSubmittingUser(user);
        user.addSubmittedQuestion(question);
        entityManager.persist(question);
        return new QuestionDto(question);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionDto approveQuestion(int questionId, String justification) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
        if(question.getStatus() == Question.Status.PENDING) {
            question.setStatus(Question.Status.AVAILABLE);
            question.setJustification(justification);
            entityManager.persist(question);
            return new QuestionDto(question);
        }
        else {
            throw new TutorException(QUESTION_NOT_PENDING);
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public QuestionDto rejectQuestion(int questionId, String justification) {
        if(justification == null || justification.trim().isEmpty()) {
            throw new TutorException(QUESTION_MISSING_JUSTIFICATION);
        }
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, questionId));
        if(question.getStatus() == Question.Status.PENDING) {
            question.setStatus(Question.Status.REJECTED);
            question.setJustification(justification);
            entityManager.persist(question);
            return new QuestionDto(question);
        }
        else {
            throw new TutorException(QUESTION_NOT_PENDING);
        }
    }

    private void checkIfPending(Question question) {
        if (question.getStatus() != Question.Status.PENDING)
            question.setStatus(Question.Status.PENDING);
    }

    private void checkQuestionKey(QuestionDto questionDto) {
        if (questionDto.getKey() == null) {
            int maxQuestionNumber = questionRepository.getMaxQuestionNumber() != null ?
                    questionRepository.getMaxQuestionNumber() : 0;
            questionDto.setKey(maxQuestionNumber + 1);
        }
    }
    
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String exportQuestionsToLatex() {
        LatexQuestionExportVisitor latexExporter = new LatexQuestionExportVisitor();

        return latexExporter.export(questionRepository.findAll());
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ByteArrayOutputStream exportCourseQuestions(int courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));

        course.getQuestions();

        String name = course.getName();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            List<Question> questions = new ArrayList<>(course.getQuestions());

            XMLQuestionExportVisitor xmlExport = new XMLQuestionExportVisitor();
            InputStream in = IOUtils.toInputStream(xmlExport.export(questions), StandardCharsets.UTF_8);
            zos.putNextEntry(new ZipEntry(name + ".xml"));
            copyToZipStream(zos, in);
            zos.closeEntry();

            LatexQuestionExportVisitor latexExport = new LatexQuestionExportVisitor();
            zos.putNextEntry(new ZipEntry(name + ".tex"));
            in = IOUtils.toInputStream(latexExport.export(questions), StandardCharsets.UTF_8);
            copyToZipStream(zos, in);
            zos.closeEntry();

            baos.flush();

            return baos;
        } catch (IOException ex) {
            throw new TutorException(ErrorMessage.CANNOT_OPEN_FILE);
        }
    }

    private void copyToZipStream(ZipOutputStream zos, InputStream in) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) > 0) {
            zos.write(buffer, 0, len);
        }
        in.close();
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deleteQuizQuestion(QuizQuestion quizQuestion) {
        Question question = quizQuestion.getQuestion();
        quizQuestion.remove();
        quizQuestionRepository.delete(quizQuestion);

        if (question.getQuizQuestions().isEmpty()) {
            this.deleteQuestion(question);
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deleteQuestion(Question question) {
        for (Option option : question.getOptions()) {
            option.remove();
            optionRepository.delete(option);
        }

        if (question.getImage() != null) {
            imageRepository.delete(question.getImage());
        }

        question.getTopics().forEach(topic -> topic.getQuestions().remove(question));
        question.getTopics().clear();

        questionRepository.delete(question);

    }
}

