package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Message;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DiscussionDto implements Serializable {

    private Integer id;
    private MessageDto  studentMessage;
    private MessageDto teacherMessage;
    private List<MessageDto> discussionListMessages= new ArrayList<>();


    private User student;
    private User teacher;



    public DiscussionDto(){
    }

    public DiscussionDto(Discussion discussion){
        this.id= discussion.getId();
        if (discussion.getStudentMessage() != null) {
            this.studentMessage = new MessageDto(discussion.getStudentMessage());
        }
        if (discussion.getTeacherMessage() != null) {
            this.teacherMessage = new MessageDto(discussion.getTeacherMessage());
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MessageDto getStudentMessage() {
        return studentMessage;
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

    public User getStudent() { return student; }

    public void setStudent(User student) { this.student = student; }

    public User getTeacher() { return teacher; }

    public void setTeacher(User teacher) { this.teacher = teacher; }


    @Override
    public String toString() {
        return "ThreadDto{" +
                "id=" + id +
                ", studentMessage=" + studentMessage +
                ", teacherMessage=" + teacherMessage +
                ", threadListMessages=" + discussionListMessages +
                '}';
    }
}
