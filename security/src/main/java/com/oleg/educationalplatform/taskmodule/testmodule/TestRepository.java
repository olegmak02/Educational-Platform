package com.oleg.educationalplatform.taskmodule.testmodule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestRepository extends JpaRepository<Test, Integer> {

    @Query(value = "SELECT * FROM test WHERE course_id = ?1", nativeQuery = true)
    List<Test> findAllByCourseId(Integer courseId);

}
