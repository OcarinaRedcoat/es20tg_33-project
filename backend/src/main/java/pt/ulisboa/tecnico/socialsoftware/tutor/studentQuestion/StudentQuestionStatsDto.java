package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import java.io.Serializable;

public class StudentQuestionStatsDto implements Serializable {

    private int submitted;
    private int approved;
    private int pending;

    public int getSubmitted() {
        return submitted;
    }

    public void setSubmitted(int submitted) {
        this.submitted = submitted;
    }

    public int getApproved() {
        return approved;
    }

    public void setApproved(int approved) {
        this.approved = approved;
    }

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }

}
