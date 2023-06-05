package com.oleg.educationalplatform.taskmodule.testmodule;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "test")
@Data
@RequiredArgsConstructor
public class Test {
    @Id
    @SequenceGenerator(name = "post_gen")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "post_gen")
    private Integer id;
    private Integer courseId;
    private Date beginDate;
    private Date endDate;
    private Integer duration;
}
