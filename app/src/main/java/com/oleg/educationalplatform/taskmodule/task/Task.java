package com.oleg.educationalplatform.taskmodule.task;

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
@Table(name = "task")
public class Task {

    @Id
    @SequenceGenerator(name = "post_gen")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "post_gen")
    private Integer id;

    @NotNull
    private String title;
    @NotNull
    private Integer courseId;
    private String taskText;
    private Date creationTime;
    private Date exposeTime;

    @Transient
    private List<Integer> attachments;
}
