package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Message;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageDto implements Serializable {

    private Integer id;

    private Integer discussionId;

    private String userName;

    private String name;

    private String sentence;

    private String messageDate;



    public MessageDto(){}

    public MessageDto(Message message){
        this.id=message.getId();
        this.discussionId = message.getDiscussion().getId();
        this.userName = message.getUserName();
        this.name = message.getName();
        this.sentence=message.getSentence();

        if (message.getMessageDate() != null){
            this.messageDate = message.getMessageDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDiscussionId() {
        return discussionId;
    }

    public void setDiscussionId(Integer discussionId) {
        this.discussionId = discussionId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }
}
