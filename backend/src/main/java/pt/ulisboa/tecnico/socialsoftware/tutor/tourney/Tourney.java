package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import java.time.LocalDateTime;

public class Tourney {

    public enum Status {CLOSED, OPEN}

    private int numberOfQuestions;
    private int id;
    //TODO topicConjunction
    private LocalDateTime availableDate, conclusionDate;
    private Tourney.Status status;

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
