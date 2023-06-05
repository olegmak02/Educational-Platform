package com.oleg.educationalplatform.assessment;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "assessment")
public class Assessment {
    @Id
    @GeneratedValue
    private Integer id;

    @NotNull
    private Integer studentId;
    @NotNull
    private Integer taskId;
    private Integer mark;

    private Date date;
}
