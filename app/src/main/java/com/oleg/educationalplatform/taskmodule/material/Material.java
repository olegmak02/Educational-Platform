package com.oleg.educationalplatform.taskmodule.material;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "material")
public class Material {

    @Id
    @SequenceGenerator(name = "post_gen")
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "post_gen")
    private Integer id;

    private Integer courseId;

    @Transient
    private List<Integer> attachments;
}
