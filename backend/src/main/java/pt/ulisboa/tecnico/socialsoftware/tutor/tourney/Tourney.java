package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import java.time.LocalDateTime;
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
    // TODO topicConjunction
    private LocalDateTime availableDate, conclusionDate;

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

    public LocalDateTime getAvailableDate(){
        return availableDate;
    }

    public void setNumberOfQuestions(LocalDateTime availableDate){
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
