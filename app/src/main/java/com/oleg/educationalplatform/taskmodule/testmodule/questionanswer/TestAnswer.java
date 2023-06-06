package com.oleg.educationalplatform.taskmodule.testmodule.questionanswer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "test_answer")
@Data
public class TestAnswer {
    @Id
    @GeneratedValue
    private Integer id;

    private Integer studentId;
    private Integer testId;
    private Date date;
    private Double mark;
}
