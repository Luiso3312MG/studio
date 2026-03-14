package com.kbseed.controller;

import com.kbseed.dto.ClientDTO;
import com.kbseed.service.ClientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    private final ClientService clientService;
    public ClientController(ClientService clientService) { this.clientService = clientService; }

    @GetMapping
    public List<ClientDTO> obtenerTodos() { return clientService.obtenerTodos(); }

    @GetMapping("/{id}")
    public ClientDTO obtenerPorId(@PathVariable Long id) { return clientService.obtenerPorId(id); }

    @PostMapping
    public ClientDTO crear(@RequestBody ClientDTO dto) { return clientService.crear(dto); }

    @PutMapping("/{id}")
    public ClientDTO actualizar(@PathVariable Long id, @RequestBody ClientDTO dto) { return clientService.actualizar(id, dto); }

    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Long id) { clientService.eliminar(id); return "Alumno eliminado correctamente"; }
}
