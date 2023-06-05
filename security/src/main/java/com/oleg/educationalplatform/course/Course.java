package com.oleg.educationalplatform.course;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue
    private Integer id;
    @NotNull
    private String title;
    @NotNull
    private Integer teacherId;
    private Date beginDate;
    private Date endDate;

    @Transient
    private boolean isFinished;

    @PostLoad
    private void onPostLoad() {
        if (endDate.before(new Date())) {
            isFinished = true;
        } else {
            isFinished = false;
        }
    }
}
