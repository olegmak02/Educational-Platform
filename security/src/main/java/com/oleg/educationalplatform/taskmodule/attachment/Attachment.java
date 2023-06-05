package com.oleg.educationalplatform.taskmodule.attachment;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "attachment")
public class Attachment {

    @Id
    @GeneratedValue
    private Integer id;

    private String path;

    private Integer postId;

    @Transient
    private MultipartFile file;

    @Transient
    private String title;

    @PostLoad
    private void onPostLoad() {
        this.title = Paths.get(path).getFileName().toString();
    }
}
