package pt.ulisboa.tecnico.socialsoftware.tutor.tourney;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TourneyRepository extends JpaRepository<Tourney, Integer> {
    @Query(value = "select * from tourneys t where t.status = :status ", nativeQuery = true)
    List<Tourney> findByStatus(String status);
}
