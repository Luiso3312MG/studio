package com.kbseed.controller;

import com.kbseed.dto.ClassTypeDTO;
import com.kbseed.service.ClassTypeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class-types")
public class ClassTypeController {

    private final ClassTypeService classTypeService;

    public ClassTypeController(ClassTypeService classTypeService) {
        this.classTypeService = classTypeService;
    }

    @GetMapping
    public List<ClassTypeDTO> obtenerTodos(@RequestParam(required = false) Long studioId) {
        if (studioId != null) {
            return classTypeService.obtenerPorStudioId(studioId);
        }
        return classTypeService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ClassTypeDTO obtenerPorId(@PathVariable Long id) {
        return classTypeService.obtenerPorId(id);
    }

    @PostMapping
    public ClassTypeDTO crear(@RequestBody ClassTypeDTO dto) {
        return classTypeService.crear(dto);
    }

    @PutMapping("/{id}")
    public ClassTypeDTO actualizar(@PathVariable Long id, @RequestBody ClassTypeDTO dto) {
        return classTypeService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Long id) {
        classTypeService.eliminar(id);
        return "Tipo de clase eliminado correctamente";
    }
}
