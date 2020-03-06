package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ThreadDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "threads")
public class Thread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Message studentMessage;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Message teacherMessage;

    @OneToMany(cascade = CascadeType.ALL,fetch=FetchType.LAZY, orphanRemoval=true)
    private List<Message> threadListMessages = new ArrayList<>();

    public Thread(){
    }

    public Thread(Integer id,Message studentMessage,Message teacherMessage){
        this.id= id;
        this.studentMessage= studentMessage;
        this.teacherMessage= teacherMessage;
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

    public void saveStudentMessage(){
        this.threadListMessages.add(this.studentMessage);
    }

    public void saveTeacherMessage(){
        this.threadListMessages.add(this.teacherMessage);
    }

    public String displayThread(){
        return threadListMessages.get(0).displayMessage() + "\n" +
                threadListMessages.get(1).displayMessage();
    }

    @Override
    public String toString() {
        return "Thread{" +
                "id=" + id +
                ", studentMessage=" + studentMessage +
                ", teacherMessage=" + teacherMessage +
                ", threadListMessages=" + threadListMessages +
                '}';
    }

}
