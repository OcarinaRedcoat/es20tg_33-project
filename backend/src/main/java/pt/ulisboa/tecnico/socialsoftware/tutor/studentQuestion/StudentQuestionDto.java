package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;

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
    private List<OptionDto> options = new ArrayList<>();

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
        this.options = question.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
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

    public List<OptionDto> getOptions() { return options; }

    public void setOptions(List<OptionDto> options) { this.options = options; }

    @Override
    public String toString() {
        return "StudentQuestion{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", options=" + options +
                ", submittingUser=" + submittingUser +
                ", justification='" + justification + '\'' +
                '}';
    }
}
