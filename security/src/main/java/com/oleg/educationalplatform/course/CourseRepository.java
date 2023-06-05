package com.oleg.educationalplatform.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query(value = "SELECT * FROM course WHERE teacher_id = ?1", nativeQuery = true)
    List<Course> findAllByTeacherId(Integer teacherId);

    @Query(value = "SELECT * FROM course WHERE title ILIKE %?1%", nativeQuery = true)
    List<Course> findAllByTitle(String title);
}
