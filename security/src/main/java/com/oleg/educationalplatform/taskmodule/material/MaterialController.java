package com.oleg.educationalplatform.taskmodule.material;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/material")
@RequiredArgsConstructor
public class MaterialController {

    private final MaterialService materialService;

    @GetMapping("/files/course/{courseId}")
    public ResponseEntity<List<Integer>> getFiles(@PathVariable Integer courseId) {
        return ResponseEntity.ok(materialService.getFilesByCourseId(courseId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<Integer> getIdByCourse(@PathVariable Integer courseId) {
        return ResponseEntity.ok(materialService.getIdByCourseId(courseId));
    }

    @PostMapping("/create")
    public ResponseEntity<Material> create(@RequestBody Material material) {
        return ResponseEntity.ok(materialService.create(material));
    }
}
