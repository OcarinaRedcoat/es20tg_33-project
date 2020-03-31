package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StudentQuestionDto implements Serializable {
    private Integer id;
    private Integer key;
    private String title;
    private String content;
    private String status;
    private String submittingUser;
    private String justification;
    private List<String> options = new ArrayList<>();
    private Integer correctOptionIndex = 0;

    public StudentQuestionDto() {
    }

    public StudentQuestionDto(StudentQuestion question) {
        this.id = question.getId();
        this.key = question.getKey();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.status = question.getStatus().name();
        this.submittingUser = question.getSubmittingUser().getUsername();
        this.justification = question.getJustification();
        this.options = question.getOptions().stream().map(Option::getContent).collect(Collectors.toList());
        this.correctOptionIndex = (question.getOptions().stream()
                .filter(Option::getCorrect)
                .mapToInt(Option::getSequence)
                .findAny()
                .getAsInt()) + 1;
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getKey() { return key; }

    public void setKey(Integer key) { this.key = key; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }

    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getSubmittingUser() { return submittingUser; }

    public void setSubmittingUser(String submittingUser) { this.submittingUser = submittingUser; }

    public String getJustification() { return justification; }

    public void setJustification(String justification) { this.justification = justification; }

    public List<String> getOptions() { return options; }

    public void setOptions(List<String> options) { this.options = options; }

    public int getCorrectOptionIndex() { return correctOptionIndex; }

    public void setCorrectOptionIndex(Integer correctOptionIndex) { this.correctOptionIndex = correctOptionIndex; }

    @Override
    public String toString() {
        return "StudentQuestion{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", options=" + options +
                ", correctOptionIndex=" + correctOptionIndex +
                ", submittingUser=" + submittingUser +
                ", justification='" + justification + '\'' +
                '}';
    }
}
