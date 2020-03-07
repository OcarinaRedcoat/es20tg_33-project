package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourneyService {

    @Autowired
    private TourneyRepository tourneyRepository;

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
        Tourney tourney = new Tourney(tourneyDto);
        return new TourneyDto();
    }
}
