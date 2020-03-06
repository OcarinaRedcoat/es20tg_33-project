package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Message;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Thread;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ThreadDto implements Serializable {

    private Integer id;
    private Message  studentMessage;
    private Message teacherMessage;
    private List<Message> threadListMessages= new ArrayList<>();

    public  ThreadDto(){
    }

    public ThreadDto(Thread thread){
        this.id=thread.getId();
        this.studentMessage=thread.getStudentMessage();
        this.teacherMessage=thread.getTeacherMessage();
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

    public List<Message> getThreadListMessages() {
        return threadListMessages;
    }

    public void setThreadListMessages(List<Message> threadListMessages) {
        this.threadListMessages = threadListMessages;
    }

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
