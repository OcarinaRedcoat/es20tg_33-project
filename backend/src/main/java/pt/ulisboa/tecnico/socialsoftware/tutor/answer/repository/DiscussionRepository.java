package pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface DiscussionRepository extends JpaRepository<Discussion, Integer> {
    @Query(value = "SELECT * FROM discussion d WHERE d.course_id = :courseId", nativeQuery = true)
    List<Discussion> findByCourseId(int courseId);
    @Query(value = "SELECT * FROM discussion d WHERE d.course_id = :courseId and d.student_id = :student or d.status = :PUBLIC ", nativeQuery = true)
    List<Discussion> findByCourseIdandStudent(int courseId, User student);
    /*@Query(value = "SELECT discussion FROM discussion d WHERE d.course_id = :courseId and d.id = :discussionId", nativeQuery = true)
    List<Discussion> findByDiscussionId(int courseId, int discussionId);*/
}

