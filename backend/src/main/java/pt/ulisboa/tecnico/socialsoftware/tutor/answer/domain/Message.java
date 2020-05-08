
package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.MessageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name= "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String userName;

    @Column(name = "name")
    private String name;

    @ManyToOne(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "discussion_id")
    private Discussion discussion;

    private String sentence;

    private LocalDateTime messageDate;


    public Message(){
    }

    public Message(Discussion discussion, MessageDto messageDto){
        this.discussion = discussion;
        this.userName = messageDto.getUserName();
        this.name = messageDto.getName();
        this.sentence = messageDto.getSentence();
        if (messageDto.getMessageDate() != null){
            this.messageDate = LocalDateTime.parse(messageDto.getMessageDate(), Course.formatter);
        }
        this.discussion.addDiscussionMessages(this);

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Discussion getDiscussion() {
        return discussion;
    }

    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
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

    public void checkConsistentMessage(MessageDto messageDto) {
        if (messageDto.getMessageDate() == null){
            throw new TutorException(MESSAGE_DATE_NULL);
        }
        if (messageDto.getSentence() == null ){
            throw new TutorException(MESSAGE_NULL);
        }
        if (messageDto.getSentence().trim().length() == 0){
            throw new TutorException(MESSAGE_EMPTY);
        }


    }

}

