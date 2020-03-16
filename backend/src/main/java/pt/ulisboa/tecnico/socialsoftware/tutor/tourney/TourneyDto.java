package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TourneyDto implements Serializable {
    private Integer tourneyNumberOfQuestions;
    private Integer tourneyId;
    private String tourneyAvailableDate, tourneyConclusionDate;
    private Tourney.Status tourneyStatus;
    private StudentDto tourneyCreator;

    private List<TopicDto> topics = new ArrayList<>();
    private List<StudentDto> tourneyEnrolledStudents = new ArrayList<>();

    public TourneyDto(){}

    public TourneyDto(Tourney tourney){
        this.tourneyNumberOfQuestions = tourney.getNumberOfQuestions();
        this.tourneyId = tourney.getId();
        this.tourneyAvailableDate = tourney.getAvailableDate();
        this.tourneyConclusionDate = tourney.getConclusionDate();
        this.tourneyStatus = tourney.getStatus();
        this.tourneyCreator = new StudentDto(tourney.getCreator());
        this.topics = tourney.getTopics().stream().map(TopicDto::new).collect(Collectors.toList());;
    }

    public Integer getTourneyNumberOfQuestions(){
        return tourneyNumberOfQuestions;
    }

    public void setTourneyNumberOfQuestions(int tourneyNumberOfQuestions){
        this.tourneyNumberOfQuestions = tourneyNumberOfQuestions;
    }

    public Integer getTourneyId(){
        return tourneyId;
    }

    public void setTourneyId(int tourneyId){
        this.tourneyId = tourneyId;
    }

    public String getTourneyAvailableDate(){
        return tourneyAvailableDate;
    }

    public void setTourneyAvailableDate(String tourneyAvailableDate){
        this.tourneyAvailableDate = tourneyAvailableDate;
    }

    public String getTourneyConclusionDate(){
        return tourneyConclusionDate;
    }

    public void setTourneyConclusionDate(String tourneyConclusionDate){
        this.tourneyConclusionDate = tourneyConclusionDate;
    }

    public Tourney.Status getTourneyStatus(){
        return tourneyStatus;
    }

    public void setTourneyStatus(Tourney.Status tourneyStatus){
        this.tourneyStatus = tourneyStatus;
    }

    public List<TopicDto> getTourneyTopics() {
        return topics;
    }

    public void setTourneyTopics(List<TopicDto> topics) {
        this.topics = topics;
    }

    public StudentDto getTourneyCreator() {
        return tourneyCreator;
    }

    public void setTourneyCreator(StudentDto tourneyCreator) {
        this.tourneyCreator = tourneyCreator;
    }

    public List<StudentDto> getTourneyEnrolledStudents() {
        return tourneyEnrolledStudents;
    }

    public void setTourneyEnrolledStudents(List<StudentDto> tourneyEnrolledStudents) {
        this.tourneyEnrolledStudents = tourneyEnrolledStudents;
    }
}
