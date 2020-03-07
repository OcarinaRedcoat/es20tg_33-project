package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;

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

    public Tourney(){}

    public Tourney(TourneyDto tourneyDto){}

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

}
