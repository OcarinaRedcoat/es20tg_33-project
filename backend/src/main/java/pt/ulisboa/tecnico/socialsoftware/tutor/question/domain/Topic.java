package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tourney.Tourney;

import javax.persistence.*;
import java.util.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_NAME_FOR_TOPIC;

@Entity
@Table(name = "topics")
public class Topic implements DomainEntity {
    public enum Status {
        DISABLED, REMOVED, AVAILABLE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToMany
    private final Set<Question> questions = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    private final List<TopicConjunction> topicConjunctions = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
    private Set<Tourney> tourneys = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public Topic() {
    }

    public Topic(Course course, TopicDto topicDto) {
        setName(topicDto.getName());
        setCourse(course);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTopic(this);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new TutorException(INVALID_NAME_FOR_TOPIC);

        this.name = name;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    public List<TopicConjunction> getTopicConjunctions() {
        return topicConjunctions;
    }

    public void addTopicConjunction(TopicConjunction topicConjunction) {
        this.topicConjunctions.add(topicConjunction);
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
        course.addTopic(this);
    }

    public Set<Tourney> getTourneys() {
        return tourneys;
    }

    public void addTourney(Tourney tourney) {
        this.tourneys.add(tourney);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topic topic = (Topic) o;
        return name.equals(topic.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public void remove() {
        getCourse().getTopics().remove(this);
        course = null;

        getQuestions().forEach(question -> question.getTopics().remove(this));
        getQuestions().clear();

        this.topicConjunctions.forEach(topicConjunction -> topicConjunction.getTopics().remove(this));

    }
}
