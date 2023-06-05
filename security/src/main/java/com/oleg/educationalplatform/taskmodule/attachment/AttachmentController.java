package com.oleg.educationalplatform.taskmodule.attachment;

import org.apache.commons.io.IOUtils;
import jakarta.servlet.ServletContext;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

@Data
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/file")
public class AttachmentController {

    private final AttachmentService service;

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Integer>> getAllByPost(@PathVariable("postId") Integer pathId) {
        List<Integer> res = service.getAllByPostId(pathId);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attachment> findById(@PathVariable("id") Integer attachmentId) {
        return ResponseEntity.ok(service.getAttachmentById(attachmentId));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable Integer id) throws IOException {
        Attachment attachment = service.getAttachmentById(id);
        File file = new File(attachment.getPath());
        InputStream is = new FileInputStream(file);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(Files.probeContentType(file.toPath())))
                .header("Content-Disposition", "attachment; filename=\"" + file.toPath().getFileName().toString() + "\"")
                .body(IOUtils.toByteArray(is));
    }

    @PostMapping("/upload")
    public ResponseEntity<Integer> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("postId") Integer postId) {
        Attachment attachment = Attachment.builder()
                    .postId(postId)
                    .file(file)
                    .build();

        return ResponseEntity.ok(service.saveAttachment(attachment));
    }
}
