package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "discussion")
public class Discussion {

    public enum PublicStatus {PUBLIC, PRIVATE}
    public enum SolvedStatus {SOLVED, UNSOLVED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User creatorStudent;

    @ManyToOne
    @JoinColumn(name = "quiz_answer_id")
    private QuizAnswer quizAnswer;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany
    private List<Message> discussionListMessages = new ArrayList<>();

    @Column(name = "status_id")
    private PublicStatus status;

    @Column(name = "solved_id")
    private SolvedStatus solved;

    public Discussion(){
    }

    public Discussion(Course course, QuizAnswer quizAnswer, User creatorStudent){
        this.creatorStudent = creatorStudent;
        this.course = course;
        this.quizAnswer = quizAnswer;
        this.status = PublicStatus.PRIVATE;
        this.solved = SolvedStatus.UNSOLVED;
        this.creatorStudent.addDiscussion(this);
        this.course.addDiscussion(this);
        this.quizAnswer.addDiscussion(this);
    }

    public void addDiscussionMessages(Message message){
        this.discussionListMessages.add(message);
    }

    public Integer getId() {
        return id;
    }

    public User getCreatorStudent() {
        return creatorStudent;
    }

    public QuizAnswer getQuizAnswer() {
        return quizAnswer;
    }

    public Course getCourse() {
        return course;
    }

    public List<Message> getDiscussionListMessages() {
        return discussionListMessages;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setCreatorStudent(User creatorStudent) {
        this.creatorStudent = creatorStudent;
    }

    public void setQuizAnswer(QuizAnswer quizAnswer) {
        this.quizAnswer = quizAnswer;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setDiscussionListMessages(List<Message> discussionListMessages) {
        this.discussionListMessages = discussionListMessages;
    }

    public PublicStatus getStatus() {
        return status;
    }

    public void setStatus(PublicStatus status) {
        this.status = status;
    }

    public void changeStatus(){
        if (status == PublicStatus.PRIVATE){
            status = PublicStatus.PUBLIC;
        } else{
            status = PublicStatus.PRIVATE;
        }
    }

    public void changeSolved(){
        if (solved == SolvedStatus.SOLVED){
            solved = SolvedStatus.UNSOLVED;
        } else{
            solved = SolvedStatus.SOLVED;
        }
    }

    public SolvedStatus getSolved() {
        return solved;
    }
}
