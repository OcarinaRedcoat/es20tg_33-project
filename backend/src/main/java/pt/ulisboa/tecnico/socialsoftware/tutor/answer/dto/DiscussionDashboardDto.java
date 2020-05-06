package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import java.io.Serializable;

public class DiscussionDashboardDto implements Serializable {

    private String username;

    private String name;

    private Integer numberOfDiscussions;

    private Integer numberOfSolvedDiscussions;

    private Double percentage;

    public DiscussionDashboardDto(){}


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumberOfDiscussions() {
        return numberOfDiscussions;
    }

    public void setNumberOfDiscussions(Integer numberOfDiscussions) {
        this.numberOfDiscussions = numberOfDiscussions;
    }

    public Integer getNumberOfSolvedDiscussions() {
        return numberOfSolvedDiscussions;
    }

    public void setNumberOfSolvedDiscussions(Integer numberOfSolvedDiscussions) {
        this.numberOfSolvedDiscussions = numberOfSolvedDiscussions;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}
