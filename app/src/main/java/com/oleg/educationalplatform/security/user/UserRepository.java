package com.oleg.educationalplatform.security.user;

import jakarta.persistence.GeneratedValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    @Query(value = "SELECT * FROM _user WHERE id in (SELECT student_id FROM student_course WHERE course_id = ?1)", nativeQuery = true)
    List<User> findStudentsByCourseId(Integer courseId);

    @Query(value = "SELECT * FROM _user WHERE role = 'TEACHER'", nativeQuery = true)
    List<User> findAllTeachers();

    @Query(value = "SELECT * FROM _user WHERE role = 'STUDENT'", nativeQuery = true)
    List<User> findAllStudents();
}
