package com.oleg.educationalplatform.assessment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssessmentRepository extends JpaRepository<Assessment, Integer> {

    @Query(value = "SELECT * FROM assessment WHERE student_id = ?1 AND task_id IN (SELECT id FROM task WHERE course_id = ?2 UNION SELECT id FROM test WHERE course_id = ?2) ORDER BY date DESC", nativeQuery = true)
    List<Assessment> findAllByCourseId(Integer studentId, Integer courseId);

}
