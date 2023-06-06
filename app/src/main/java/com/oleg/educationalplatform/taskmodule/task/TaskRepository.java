package com.oleg.educationalplatform.taskmodule.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    @Query(value = "SELECT * FROM task WHERE course_id = ?1", nativeQuery = true)
    List<Task> findAllByCourseId(Integer courseId);

    @Query(value = "SELECT * FROM task WHERE course_id = ?1 AND id IN (SELECT task_id FROM answer WHERE student_id = ?2)", nativeQuery = true)
    List<Task> findSubmittedByCourseId(Integer courseId, Integer studentId);

    @Query(value = "SELECT * FROM task WHERE course_id = ?1 AND id NOT IN (SELECT task_id FROM answer WHERE student_id = ?2)", nativeQuery = true)
    List<Task> findIncompliteByCourseId(Integer courseId, Integer studentId);

    @Query(value = "SELECT * FROM task WHERE course_id = ?1 AND title ILIKE %?2%", nativeQuery = true)
    List<Task> findTasksByCourseIdAndTitle(Integer courseId, String title);
}
