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

    private Integer discussionId;
    private MessageDto  studentMessage;
    private MessageDto teacherMessage;
    private List<MessageDto> discussionListMessages= new ArrayList<>();

    private int courseId;
    private int questionAnswerId;

    private StudentDto student;
    private UserDto teacher;



    public DiscussionDto(){
    }

    public DiscussionDto(Discussion discussion){

        this.discussionId= discussion.getId();
        if (discussion.getStudentMessage() != null) {
            this.studentMessage = new MessageDto(discussion.getStudentMessage());
        }
        if (discussion.getTeacherMessage() != null) {
            this.teacherMessage = new MessageDto(discussion.getTeacherMessage());
        }
        if (discussion.getDiscussionListMessages() != null) {
            this.discussionListMessages = discussion.getDiscussionListMessages().stream().map(MessageDto::new).collect(Collectors.toList());
        }
        if (discussion.getStudent() != null) {
            this.student = new StudentDto(discussion.getStudent());
        }
        if (discussion.getTeacher() != null) {
            this.teacher = new UserDto(discussion.getTeacher());
        }
    }

    public Integer getId() {
        return discussionId;
    }

    public void setId(Integer id) {
        this.discussionId = id;
    }

    public MessageDto getStudentMessage() {
        return studentMessage;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getQuestionAnswerId() {
        return questionAnswerId;
    }

    public void setQuestionAnswerId(int questionAnswerId) {
        this.questionAnswerId = questionAnswerId;
    }

    public void setStudentMessage(MessageDto studentMessage) {
        this.studentMessage = studentMessage;
    }

    public MessageDto getTeacherMessage() {
        return teacherMessage;
    }

    public void setTeacherMessage(MessageDto teacherMessage) {
        this.teacherMessage = teacherMessage;
    }

    public List<MessageDto> getDiscussionListMessages() {
        return discussionListMessages;
    }

    public void setDiscussionListMessages(List<MessageDto> threadListMessages) {
        this.discussionListMessages = threadListMessages;
    }

    public void addDiscussionMessage(MessageDto messageDto){
        this.discussionListMessages.add(messageDto);
    }

    public StudentDto getStudent() { return student; }

    public void setStudent(StudentDto student) { this.student = student; }

    public UserDto getTeacher() { return teacher; }

    public void setTeacher(UserDto teacher) { this.teacher = teacher; }


    @Override
    public String toString() {
        return "DiscussionDto{" +
                "discussionId=" + discussionId +
                ", studentMessage=" + studentMessage +
                ", teacherMessage=" + teacherMessage +
                ", threadListMessages=" + discussionListMessages +
                '}';
    }
}
