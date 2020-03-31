package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface StudentQuestionRepository extends JpaRepository<StudentQuestion, Integer> {
    @Query(value = "SELECT * FROM student_questions q WHERE q.course_id = :courseId AND q.status = :status", nativeQuery = true)
    List<StudentQuestion> findQuestionsByStatus(int courseId, String status);

    @Query(value = "SELECT * FROM student_questions q WHERE q.course_id = :courseId AND q.user_id = :userId", nativeQuery = true)
    List<StudentQuestion> findStudentSubmittedQuestions(int courseId, int userId);

    @Query(value = "SELECT MAX(key) FROM student_questions", nativeQuery = true)
    Integer getMaxQuestionNumber();
}
