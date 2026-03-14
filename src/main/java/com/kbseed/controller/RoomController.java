package com.kbseed.controller;

import com.kbseed.dto.RoomDTO;
import com.kbseed.service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;
    public RoomController(RoomService roomService) { this.roomService = roomService; }

    @GetMapping
    public List<RoomDTO> obtenerTodos() { return roomService.obtenerTodos(); }

    @GetMapping("/{id}")
    public RoomDTO obtenerPorId(@PathVariable Long id) { return roomService.obtenerPorId(id); }

    @PostMapping
    public RoomDTO crear(@RequestBody RoomDTO dto) { return roomService.crear(dto); }

    @PutMapping("/{id}")
    public RoomDTO actualizar(@PathVariable Long id, @RequestBody RoomDTO dto) { return roomService.actualizar(id, dto); }

    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Long id) { roomService.eliminar(id); return "Salón eliminado correctamente"; }
}
