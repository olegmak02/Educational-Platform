package com.oleg.educationalplatform.taskmodule.testmodule.questionanswer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestAnswerRepository extends JpaRepository<TestAnswer, Integer> {
    @Query(value = "SELECT * FROM test_answer WHERE test_id = ?1 AND student_id = ?2", nativeQuery = true)
    TestAnswer findByTestId(Integer testId, Integer studentId);

    @Query(value = "SELECT * FROM test_answer WHERE student_id = ?1 AND test_id IN (SELECT id FROM test WHERE course_id = ?2)", nativeQuery = true)
    List<TestAnswer> findAnswersByCourse(Integer studentId, Integer courseId);
}
