package com.oleg.educationalplatform.taskmodule.testmodule.questionanswer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "question_answer")
@Data
public class QuestionAnswer {
    @Id
    @GeneratedValue
    private Integer id;

    private Integer testAnswerId;
    private Integer questionId;
    private String answer;
    private Double mark;
}
