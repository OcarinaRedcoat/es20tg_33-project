package pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface DiscussionRepository extends JpaRepository<Discussion, Integer> {
    @Query(value = "SELECT * FROM discussion d WHERE s.course_id = :courseId", nativeQuery = true)
    List<Discussion> findByCourseId(int courseId);
}

