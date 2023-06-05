package com.oleg.educationalplatform.taskmodule.testmodule.questiontest;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "test_question")
public class TestQuestion {
    @Id
    @SequenceGenerator(name = "post_gen")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "post_gen")
    private Integer id;
    private Integer testId;
    private String questionText;
    private double mark;
    private List<String> correct;
    private List<String> options;

    @Transient
    private List<Integer> attachments;
}
