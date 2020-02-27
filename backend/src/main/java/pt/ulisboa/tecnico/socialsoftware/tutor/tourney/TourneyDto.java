package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TourneyDto implements Serializable {
    private int tourneyNumberOfQuestions;
    private int tourneyId;
    private LocalDateTime tourneyAvailableDate, tourneyConclusionDate;
    private Tourney.Status tourneyStatus;

    public TourneyDto(){}

    public TourneyDto(Tourney tourney){
        this.tourneyNumberOfQuestions = tourney.getNumberOfQuestions();
        this.tourneyId = tourney.getId();
        this.tourneyAvailableDate = tourney.getAvailableDate();
        this.tourneyConclusionDate = tourney.getConclusionDate();
        this.tourneyStatus = tourney.getStatus();
    }

    public int getTourneyNumberOfQuestions(){
        return tourneyNumberOfQuestions;
    }

    public void setTourneyNumberOfQuestions(int tourneyNumberOfQuestions){
        this.tourneyNumberOfQuestions = tourneyNumberOfQuestions;
    }

    public int getTourneyId(){
        return tourneyId;
    }

    public void setTourneyId(int tourneyId){
        this.tourneyId = tourneyId;
    }

    public LocalDateTime getTourneyAvailableDate(){
        return tourneyAvailableDate;
    }

    public void setTourneyNumberOfQuestions(LocalDateTime tourneyAvailableDate){
        this.tourneyAvailableDate = tourneyAvailableDate;
    }

    public LocalDateTime getTourneyConclusionDate(){
        return tourneyConclusionDate;
    }

    public void setTourneyConclusionDate(LocalDateTime tourneyConclusionDate){
        this.tourneyConclusionDate = tourneyConclusionDate;
    }

    public Tourney.Status getTourneyStatus(){
        return tourneyStatus;
    }

    public void setTourneyStatus(Tourney.Status tourneyStatus){
        this.tourneyStatus = tourneyStatus;
    }
}
