package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.DiscussionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "discussion")
public class Discussion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "student_str")
    private Message studentMessage;

    @OneToOne
    @JoinColumn(name = "teacher_str")
    private Message teacherMessage;

    @OneToOne
    @JoinColumn(name = "student_id")
    private User student;

    @OneToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @Column(nullable = false)
    private Integer questionAnswerId;

    @OneToMany
    private List<Message> discussionListMessages = new ArrayList<>();

    public Discussion(){
    }

    public Discussion(QuestionAnswer questionAnswerId, DiscussionDto discussionDto){
        checkConsistentDiscussion(discussionDto);
        this.questionAnswerId = questionAnswerId.getId();
        this.student =  discussionDto.getStudent();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStudent(User st) { this.student = st; }

    public void setTeacher(User th) { this.teacher = th;}

    public User getStudent() { return this.student; }

    public User getTeacher() { return this.teacher; }

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
        return discussionListMessages;
    }

    public void setDiscussionListMessages(List<Message> threadListMessages) {
        this.discussionListMessages = threadListMessages;
    }


    public void saveStudentMessage(){
        this.discussionListMessages.add(this.studentMessage);
    }

    public void saveTeacherMessage(){
        this.discussionListMessages.add(this.teacherMessage);
    }

    public String displayThread(){
        return discussionListMessages.get(0).displayMessage() + "\n" +
                discussionListMessages.get(1).displayMessage();
    }

    public void checkConsistentDiscussion(DiscussionDto discussionDto){
        for (QuizAnswer qzA : student.getQuizAnswers()) {
            for (QuestionAnswer qA : qzA.getQuestionAnswers()) {
                if (qA.getId().equals(questionAnswerId)) {
                    return;
                }
            }
        }
        throw new TutorException(STUDENT_DID_NOT_ANSWER_QUESTION);

    }

    @Override
    public String toString() {
        return "Thread{" +
                "id=" + id +
                ", studentMessage=" + studentMessage +
                ", teacherMessage=" + teacherMessage +
                ", threadListMessages=" + discussionListMessages +
                '}';
    }

}
