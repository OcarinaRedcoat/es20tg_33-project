package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;

import java.util.Optional;

public interface TourneyRepository extends JpaRepository<Tourney, Integer> {
    @Query(value = "select * from tourneys t where t.status = :status ", nativeQuery = true)
    Optional<Tourney> findByStatus(Tourney.Status status);
}
