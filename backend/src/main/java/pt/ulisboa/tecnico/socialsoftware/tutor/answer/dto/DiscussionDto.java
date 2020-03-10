package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Message;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DiscussionDto implements Serializable {

    private Integer id;
    private Message  studentMessage;
    private Message teacherMessage;
    private List<Message> threadListMessages= new ArrayList<>();


    private User student;
    private User teacher;



    public DiscussionDto(){
    }

    public DiscussionDto(Discussion discussion){
        this.id=discussion.getId();
        this.studentMessage=discussion.getStudentMessage();
        this.teacherMessage=discussion.getTeacherMessage();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Message getStudentMessage() {
        return studentMessage;
    }

    public void setStudentMessage(Message studentMessage) {
        this.studentMessage = studentMessage;
    }

    public Message getTeacherMessage() {
        return teacherMessage;
    }

    public void setTeacherMessage(Message teacherMessage) {
        this.teacherMessage = teacherMessage;
    }

    public List<Message> getDiscussionListMessages() {
        return threadListMessages;
    }

    public void setDiscussionListMessages(List<Message> threadListMessages) {
        this.threadListMessages = threadListMessages;
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
                ", threadListMessages=" + threadListMessages +
                '}';
    }
}
