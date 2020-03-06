
package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import org.apache.tomcat.jni.Local;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.Importable;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MessageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name= "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String sentence;

    private LocalDateTime messageDate;

    public Message(){
    }

    public Message(User user, String sentence, LocalDateTime messageDate){
        this.user = user;
        this.sentence = sentence;
        this.messageDate= messageDate;
    }

    public Integer getId() {return this.id;}

    public void setId(Integer id) {this.id=id;}

    public User getUser() {return user;}

    public void setUser(User user) {this.user=user;}

    public String getSentence() {return this.sentence;}

    public void setSentence(String string) {this.sentence=string;}

    public LocalDateTime getMessageDate() {return this.messageDate;}

    public void setMessageDate(LocalDateTime messageDate) {this.messageDate=messageDate;}

    public String displayMessage(){
        return user.getName() + "(" + user.getRole() + ") - " + getSentence() + getMessageDate();
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", user=" + user +
                ", sentence='" + sentence + '\'' +
                ", messageDate=" + messageDate +
                '}';
    }
}

