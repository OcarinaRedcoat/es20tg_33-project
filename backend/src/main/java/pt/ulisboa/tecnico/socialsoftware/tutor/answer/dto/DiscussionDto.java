package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Message;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DiscussionDto implements Serializable {

    private Integer id;

    private List<MessageDto> discussionListMessages= new ArrayList<>();

    private int courseId;

    private int quizAnswerId;

    private StudentDto creatorStudent;

    private String title;

    private Discussion.Status status;

    public DiscussionDto(){
    }

    public DiscussionDto(Discussion discussion){
        this.id = discussion.getId();
        this.courseId = discussion.getCourse().getId();
        this.quizAnswerId = discussion.getQuizAnswer().getId();
        this.title = discussion.getQuizAnswer().getQuiz().getTitle();

        this.status = discussion.getStatus();

        this.creatorStudent = new StudentDto(discussion.getCreatorStudent());
        this.discussionListMessages = discussion.getDiscussionListMessages().stream().map(MessageDto::new).collect(Collectors.toList());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<MessageDto> getDiscussionListMessages() {
        return discussionListMessages;
    }

    public void setDiscussionListMessages(List<MessageDto> discussionListMessages) {
        this.discussionListMessages = discussionListMessages;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getQuizAnswerId() {
        return quizAnswerId;
    }

    public void setQuizAnswerId(int quizAnswerId) {
        this.quizAnswerId = quizAnswerId;
    }

    public StudentDto getCreatorStudent() {
        return creatorStudent;
    }

    public void setCreatorStudent(StudentDto creatorStudent) {
        this.creatorStudent = creatorStudent;
    }

}
