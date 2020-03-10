
package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MessageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

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
    @OneToOne
    @JoinColumn(name = "question:id")
    private QuestionAnswer questionAnswer;

    public Message(){
    }

    public Message(MessageDto messageDto,User user, String sentence, LocalDateTime messageDate){
        checkConsistentMessage(messageDto);
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

    private void checkConsistentMessage(MessageDto messageDto) {
        if (!(messageDto.getMessageDate() == null || messageDto.getSentence() == null ||
                messageDto.getSentence().trim().length() == 0)){
            throw new TutorException(MESSAGE_MISSING_DATA);
        }

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

