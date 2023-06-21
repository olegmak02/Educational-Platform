package com.oleg.educationalplatform.taskmodule.taskanswer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "answer")
public class TaskAnswer {

    @Id
    @SequenceGenerator(name = "post_gen")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "post_gen")
    private Integer id;

    private Integer studentId;
    @NotNull
    private Integer taskId;
    private Date date;

    @Transient
    private List<Integer> attachments;
}
