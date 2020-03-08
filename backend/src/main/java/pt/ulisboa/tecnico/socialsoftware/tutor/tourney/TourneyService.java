package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourneyService {

    @Autowired
    private TourneyRepository tourneyRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TourneyDto> getOpenTourneys() {
        return tourneyRepository.findByStatus(Tourney.Status.OPEN.name()).stream()
                .map(TourneyDto::new)
                .sorted(Comparator.comparing(TourneyDto::getTourneyAvailableDate))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TourneyDto createTourney(TourneyDto tourneyDto) {
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Tourney tourney = new Tourney(tourneyDto);
        if(tourney.getAvailableDate() == null)  throw new TutorException(ErrorMessage.TOURNEY_NOT_CONSISTENT,"availableDate");
        if(tourney.getConclusionDate() == null)  throw new TutorException(ErrorMessage.TOURNEY_NOT_CONSISTENT, "conclusionDate");
        try{
            if(date.parse(tourney.getConclusionDate()).before(date.parse(tourney.getAvailableDate())))  throw new TutorException(ErrorMessage.TOURNEY_AVAILABLEDATE_BIGGER_THAN_CONCLUSIONDATE);
        }catch (ParseException e) {throw new TutorException(ErrorMessage.TOURNEY_DATE_WRONG_FORMAT);}
        if(tourney.getTopics().size() == 0)  throw new TutorException(ErrorMessage.TOURNEY_NOT_CONSISTENT, "topics");
        if(tourney.getNumberOfQuestions() == null) throw new TutorException(ErrorMessage.TOURNEY_NOT_CONSISTENT, "numberOfQuestions");
        entityManager.persist(tourney);
        return new TourneyDto(tourney);


    }
}
