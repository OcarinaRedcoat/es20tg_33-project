package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import java.io.Serializable;

public class TourneyStatsDto implements Serializable {

	private String title;
	private int score;

	public void setScore(int score) {
		this.score = score;
	}

	public int getScore() {
		return score;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}