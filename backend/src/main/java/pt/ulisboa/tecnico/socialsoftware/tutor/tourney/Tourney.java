package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
@Table(name = "tourneys")
public class Tourney {

    public enum Status {CLOSED, OPEN}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private Tourney.Status status;

    private int numberOfQuestions;

    private LocalDateTime availableDate, conclusionDate;

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tourney", fetch=FetchType.LAZY, orphanRemoval=true)
    private List<Topic> topics = new ArrayList<>();

    public Tourney(){}

    public int getNumberOfQuestions(){
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions){
        this.numberOfQuestions = numberOfQuestions;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public LocalDateTime getAvailableDate(){ return availableDate; }

    public void setAvailableDate(LocalDateTime availableDate){
        this.availableDate = availableDate;
    }

    public LocalDateTime getConclusionDate(){
        return conclusionDate;
    }

    public void setConclusionDate(LocalDateTime conclusionDate){
        this.conclusionDate = conclusionDate;
    }

    public Tourney.Status getStatus(){
        return status;
    }

    public void setStatus(Tourney.Status status){
        this.status = status;
    }

    public void closeTourney() {}

}
