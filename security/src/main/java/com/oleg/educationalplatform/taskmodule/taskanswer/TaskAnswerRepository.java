package com.oleg.educationalplatform.taskmodule.taskanswer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskAnswerRepository extends JpaRepository<TaskAnswer, Integer> {
    @Query(value = "SELECT * FROM answer WHERE student_id is not null and task_id = ?1", nativeQuery = true)
    List<TaskAnswer> getAnswersByTaskId(Integer taskId);

    @Query(value = "SELECT * FROM answer WHERE student_id = ?1 and task_id = ?2", nativeQuery = true)
    TaskAnswer getAnswersByTaskIdAndUser(Integer studentId, Integer taskId);
}
