
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

    @ManyToOne(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
    private Discussion discussion;

    public Message(){
    }

    public Message(MessageDto messageDto,User user){
        checkConsistentMessage(messageDto);
        this.user = user;

    }

    public Integer getId() {return this.id;}

    public void setId(Integer id) {this.id=id;}

    public User getUser() {return user;}

    public void setUser(User user) {this.user=user;}

    public String getSentence() {return this.sentence;}

    public void setSentence(String string) {this.sentence=string;}

    public LocalDateTime getMessageDate() {return this.messageDate;}

    public void setMessageDate(LocalDateTime messageDate) {this.messageDate=messageDate;}

    public Discussion getDiscussion() {
        return discussion;
    }

    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
    }

    public String displayMessage(){
        return user.getName() + "(" + user.getRole() + ") - " + getSentence() + getMessageDate();
    }

    public void checkConsistentMessage(MessageDto messageDto) {
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

