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

    @ManyToOne
    @JoinColumn(name = "student_str")
    private Message studentMessage;

    @ManyToOne
    @JoinColumn(name = "teacher_str")
    private Message teacherMessage;

    @OneToOne
    @JoinColumn(name = "student_id")
    private User student;

    @OneToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @OneToOne
    @JoinColumn(name = "questionAnswer_id")
    private QuestionAnswer questionAnswer;

    @OneToMany(cascade = CascadeType.ALL,fetch=FetchType.LAZY, orphanRemoval=true)
    private List<Message> threadListMessages = new ArrayList<>();

    public Discussion(){
    }

    public Discussion(QuestionAnswer questionAnswer, DiscussionDto discussionDto){
        checkConsistentDiscussion(discussionDto);
        this.questionAnswer = questionAnswer;

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

    public void checkConsistentDiscussion(DiscussionDto discussionDto){
        //TODO need to check if the student answered the question which this discussion is about
        //check if the questionAnswer id is a questionAnswer of the student in question, only if is a Student
        for (QuizAnswer qzA: student.getQuizAnswers()) {
            for (QuestionAnswer qA: qzA.getQuestionAnswers()) {
                if (qA.getId() == questionAnswer.getId()){
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
                ", threadListMessages=" + threadListMessages +
                '}';
    }

}
