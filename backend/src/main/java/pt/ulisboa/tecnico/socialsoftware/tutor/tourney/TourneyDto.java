package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TourneyDto implements Serializable {
    private Integer tourneyNumberOfQuestions;
    private Integer tourneyId;
    private String tourneyAvailableDate, tourneyConclusionDate;
    private Tourney.Status tourneyStatus;

    private List<Topic> topics = new ArrayList<>();

    public TourneyDto(){}

    public TourneyDto(Tourney tourney){
        this.tourneyNumberOfQuestions = tourney.getNumberOfQuestions();
        this.tourneyId = tourney.getId();
        this.tourneyAvailableDate = tourney.getAvailableDate();
        this.tourneyConclusionDate = tourney.getConclusionDate();
        this.tourneyStatus = tourney.getStatus();
        this.topics = tourney.getTopics();
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

    public List<Topic> getTourneyTopics() {
        return topics;
    }

    public void setTourneyTopics(List<Topic> topics) {
        this.topics = topics;
    }
}
