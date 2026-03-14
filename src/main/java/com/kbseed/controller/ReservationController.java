package com.kbseed.controller;

import com.kbseed.dto.CreateReservationRequest;
import com.kbseed.dto.ReservationDTO;
import com.kbseed.service.ReservationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/class/{classId}")
    public List<ReservationDTO> obtenerReservasPorClase(@PathVariable Long classId) {
        return reservationService.obtenerReservasPorClase(classId);
    }

    @PostMapping("/class/{classId}")
    public ReservationDTO inscribirAlumno(
            @PathVariable Long classId,
            @RequestBody CreateReservationRequest request
    ) {
        return reservationService.inscribirAlumno(classId, request);
    }
}