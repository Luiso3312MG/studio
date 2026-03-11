package com.kbseed.controller;

import com.kbseed.dto.ClassDTO;
import com.kbseed.service.ClassService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class ClassController {
    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @GetMapping
    public List<ClassDTO> obtenerTodas() {
        return classService.obtenerTodas();
    }

    @PostMapping
    public ClassDTO crear(@RequestBody ClassDTO dto) {
        return classService.crear(dto);
    }
}
