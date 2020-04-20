package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.*;

@Entity
@Table(name = "tourneys")
public class Tourney {

    public enum Status {CLOSED, OPEN, CANCELED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Tourney.Status status;

    private Integer numberOfQuestions;

    private String availableDate, conclusionDate;

    private String title;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "topicTourneys", fetch=FetchType.LAZY)
    private List<Topic> topics = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "enrolledTourneys", fetch=FetchType.LAZY)
    private List<User> enrolledStudents = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    @OneToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User creator;

    public Tourney(){}

    public Tourney(String title, Integer numberOfQuestions, String availableDate, String conclusionDate, User creator){
        this.title = title;
        this.numberOfQuestions = numberOfQuestions;
        this.availableDate = availableDate;
        this.conclusionDate = conclusionDate;
        this.status = Tourney.Status.OPEN;
        this.creator = creator;
    }

    public Tourney(TourneyDto tourneyDto, User user){
        this.title = tourneyDto.getTourneyTitle();
        this.numberOfQuestions = tourneyDto.getTourneyNumberOfQuestions();
        this.id = tourneyDto.getTourneyId();
        this.availableDate = tourneyDto.getTourneyAvailableDate();
        this.conclusionDate = tourneyDto.getTourneyConclusionDate();
        this.status = tourneyDto.getTourneyStatus();
        this.creator = user;
    }

    public Integer getNumberOfQuestions(){
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions){
        this.numberOfQuestions = numberOfQuestions;
    }

    public Integer getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getAvailableDate(){ return availableDate; }

    public void setAvailableDate(String availableDate){
        this.availableDate = availableDate;
    }

    public String getConclusionDate(){
        return conclusionDate;
    }

    public void setConclusionDate(String conclusionDate){
        this.conclusionDate = conclusionDate;
    }

    public Tourney.Status getStatus(){
        return status;
    }

    public void setStatus(Tourney.Status status){
        this.status = status;
    }

    public void closeTourney() {
        this.status = Status.CLOSED;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public User getCreator() {
        return creator;
    }

    public List<User> getEnrolledStudents() {return this.enrolledStudents;}
    public void enrollStudent(User user) {
        this.enrolledStudents.add(user);
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Quiz getQuiz() {
        return this.quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
}
