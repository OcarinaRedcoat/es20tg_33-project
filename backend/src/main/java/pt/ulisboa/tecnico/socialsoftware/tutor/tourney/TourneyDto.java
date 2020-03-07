package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;

import java.io.Serializable;
import java.util.ArrayList;

public class TourneyDto implements Serializable {
    private Integer tourneyNumberOfQuestions;
    private Integer tourneyId;
    private String tourneyAvailableDate, tourneyConclusionDate;
    private Tourney.Status tourneyStatus;

    private ArrayList<TopicDto> topics = new ArrayList<>();

    public TourneyDto(){}

    public TourneyDto(Tourney tourney){
        this.tourneyNumberOfQuestions = tourney.getNumberOfQuestions();
        this.tourneyId = tourney.getId();
        this.tourneyAvailableDate = tourney.getAvailableDate();
        this.tourneyConclusionDate = tourney.getConclusionDate();
        this.tourneyStatus = tourney.getStatus();
        //TODO this.topics = tourney.getTopics();
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

    public ArrayList<TopicDto> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<TopicDto> topics) {
        this.topics = topics;
    }
}
