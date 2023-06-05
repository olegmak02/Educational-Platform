package com.oleg.educationalplatform.course.studentcourse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentCourseRepository extends JpaRepository<StudentCourse, Integer> {
    @Query(value = "SELECT course_id FROM student_course WHERE student_id = ?1", nativeQuery = true)
    List<Integer> findCourseIdsByStudentId(Integer studentId);

    @Query(value = "SELECT * FROM student_course WHERE student_id = ?1 AND course_id = ?2", nativeQuery = true)
    StudentCourse findByStudentAndCourse(Integer studentId, Integer courseId);
}
