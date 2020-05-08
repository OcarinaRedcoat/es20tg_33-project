package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "student_questions")
public class StudentQuestion {

    public enum Status {PENDING, REJECTED, APPROVED, AVAILABLE}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable = false)
    private Integer key;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User submittingUser;

    @Column
    private String justification;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "studentQuestion", fetch = FetchType.EAGER, orphanRemoval=true)
    private List<Option> studentQuestionOptions = new ArrayList<>();

    public StudentQuestion() {
    }

    public StudentQuestion(Course course, StudentQuestionDto questionDto) {
        checkConsistentQuestion(questionDto);
        this.key = questionDto.getKey();
        this.title = questionDto.getTitle();
        this.content = questionDto.getContent();
        this.status = Status.valueOf(questionDto.getStatus());

        this.course = course;
        course.addStudentQuestion(this);

        int index = 0;
        for (OptionDto optionDto : questionDto.getOptions()) {
            optionDto.setSequence(index++);
            Option option = new Option(optionDto);
            this.addOption(option);
            option.setStudentQuestion(this);
        }
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getKey() { return key; }

    public void setKey(Integer key) { this.key = key; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }

    public List<Option> getOptions() { return studentQuestionOptions; }

    public void setOptions(List<Option> options) { this.studentQuestionOptions = options; }

    public Course getCourse() { return course; }

    public void setCourse(Course course) { this.course = course; }

    public User getSubmittingUser() { return submittingUser; }

    public void setSubmittingUser(User submittingUser) { this.submittingUser = submittingUser; }

    public String getJustification() { return justification; }

    public void setJustification(String justification) { this.justification = justification; }

    public void addOption(Option option) {
        this.studentQuestionOptions.add(option);
        option.setStudentQuestion(this);
    }

    public void removeOptions() {
        studentQuestionOptions = new ArrayList<>();
    }

    public void remove() {
        canRemove();
        getCourse().getSubmittedQuestions().remove(this);
        getSubmittingUser().getSubmittedQuestions().remove(this);
        course = null;
    }

    @Override
    public String toString() {
        return "StudentQuestion{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", options=" + studentQuestionOptions +
                ", course=" + course +
                ", submittingUser=" + submittingUser +
                ", justification='" + justification + '\'' +
                '}';
    }

    public void editStudentQuestion(StudentQuestionDto studentQuestionDto) {
        checkConsistentQuestion(studentQuestionDto);

        setTitle(studentQuestionDto.getTitle());
        setContent(studentQuestionDto.getContent());

        List<Option> options = getOptions();
        List<OptionDto> changedOptions = studentQuestionDto.getOptions();

        for (Option option : options) {
            for (OptionDto optionDto : changedOptions) {
                if (option.getSequence().equals(optionDto.getSequence())) {
                    option.setContent(optionDto.getContent());
                    option.setCorrect(optionDto.getCorrect());
                }
            }
        }
    }

    private void checkConsistentQuestion(StudentQuestionDto questionDto) {
        if (questionDto.getTitle() == null || questionDto.getTitle().trim().length() == 0 ||
                questionDto.getContent().trim().length() == 0 ||
                questionDto.getContent() == null) {
            throw new TutorException(QUESTION_MISSING_TITLE_OR_CONTENT);
        }

        if (questionDto.getOptions().size() != 4) {
            throw new TutorException(INVALID_NUMBER_OF_OPTIONS);
        }

        if (questionDto.getOptions().stream().anyMatch(optionDto -> optionDto.getContent().trim().length() == 0)) {
            throw new TutorException(QUESTION_MISSING_OPTIONS);
        }

        if (questionDto.getOptions().stream().filter(OptionDto::getCorrect).count() < 1) {
            throw new TutorException(NO_CORRECT_OPTION);
        }

        if (questionDto.getOptions().stream().filter(OptionDto::getCorrect).count() > 1) {
            throw new TutorException(QUESTION_MULTIPLE_CORRECT_OPTIONS);
        }
    }

    private void canRemove() {
        if (this.status == Status.APPROVED) {
            throw new TutorException(QUESTION_ALREADY_APPROVED);
        }
    }
}
