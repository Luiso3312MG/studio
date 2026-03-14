package com.kbseed.controller;

import com.kbseed.dto.BulkClassesResponse;
import com.kbseed.dto.CreateBulkClassesRequest;
import com.kbseed.dto.ClassDTO;
import com.kbseed.service.ClassService;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ClassDTO obtenerPorId(@PathVariable Long id) {
        return classService.obtenerPorId(id);
    }

    @PostMapping
    public ClassDTO crear(@RequestBody ClassDTO dto) {
        return classService.crear(dto);
    }

    @PutMapping("/{id}")
    public ClassDTO actualizar(@PathVariable Long id, @RequestBody ClassDTO dto) {
        return classService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Long id) {
        classService.eliminar(id);
        return "Clase eliminada correctamente";
    }

    @PostMapping("/bulk")
    public BulkClassesResponse crearClasesMasivas(@RequestBody CreateBulkClassesRequest request) {
        return classService.crearClasesMasivas(request);
    }

}
