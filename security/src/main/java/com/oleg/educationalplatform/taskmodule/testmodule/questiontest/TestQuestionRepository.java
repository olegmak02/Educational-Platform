package com.oleg.educationalplatform.taskmodule.testmodule.questiontest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestQuestionRepository extends JpaRepository<TestQuestion, Integer> {

    @Query(value = "SELECT * FROM test_question WHERE test_id = ?1", nativeQuery = true)
    List<TestQuestion> findByTestId(Integer testId);
}
