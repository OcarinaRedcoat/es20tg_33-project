package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "tourneys")
public class Tourney {

    public enum Status {CLOSED, OPEN}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Tourney.Status status;

    private Integer numberOfQuestions;

    private String availableDate, conclusionDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tourney", fetch=FetchType.LAZY, orphanRemoval=true)
    private List<Topic> topics = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "tourney")
    private User creator;

    public Tourney(){}

    public Tourney(Integer numberOfQuestions, String availableDate, String conclusionDate, User creator){
        this.numberOfQuestions = numberOfQuestions;
        this.availableDate = availableDate;
        this.conclusionDate = conclusionDate;
        this.status = Tourney.Status.OPEN;
        this.creator = creator;
    }

    public Tourney(TourneyDto tourneyDto, User user){
        this.numberOfQuestions = tourneyDto.getTourneyNumberOfQuestions();
        this.id = tourneyDto.getTourneyId();
        this.availableDate = tourneyDto.getTourneyAvailableDate();
        this.conclusionDate = tourneyDto.getTourneyConclusionDate();
        this.status = tourneyDto.getTourneyStatus();
        this.topics = tourneyDto.getTourneyTopics();
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

    public void closeTourney() {}

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

}
