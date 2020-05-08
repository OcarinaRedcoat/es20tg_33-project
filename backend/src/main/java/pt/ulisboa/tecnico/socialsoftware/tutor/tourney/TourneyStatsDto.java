package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import java.io.Serializable;

public class TourneyStatsDto implements Serializable {

	private String title;
	private String score;
	private String completionDate;
	private int id;

	public void setScore(String score) {
		this.score = score;
	}

	public String getScore() {
		return score;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(String completionDate) {
		this.completionDate = completionDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}