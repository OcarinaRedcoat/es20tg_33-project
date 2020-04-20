package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Message;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MessageDto implements Serializable {

    private Integer id;

    private User user;

    private String sentence;

    private LocalDateTime messageDate;

    public MessageDto(){
    }

    public MessageDto(Message message){
        this.id=message.getId();
        this.sentence=message.getSentence();
        this.messageDate=message.getMessageDate();
        this.user = message.getUser();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public LocalDateTime getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(LocalDateTime messageDate) {
        this.messageDate = messageDate;
    }

    public User.Role getUserRole(){ return user.getRole(); }

    @Override
    public String toString() {
        return "MessageDto{" +
                "id=" + id +
                ", user=" + user +
                ", sentence='" + sentence + '\'' +
                ", messageDate=" + messageDate +
                '}';
    }
}
