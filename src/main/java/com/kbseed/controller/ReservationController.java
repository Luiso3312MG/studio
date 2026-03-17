package com.kbseed.controller;

import com.kbseed.dto.CreateReservationRequest;
import com.kbseed.dto.ReservationActionRequest;
import com.kbseed.dto.ReservationDTO;
import com.kbseed.service.ReservationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;
    public ReservationController(ReservationService reservationService) { this.reservationService = reservationService; }

    @GetMapping("/class/{classId}")
    public List<ReservationDTO> obtenerReservasPorClase(@PathVariable Long classId) { return reservationService.obtenerReservasPorClase(classId); }

    @PostMapping("/class/{classId}")
    public ReservationDTO inscribirAlumno(@PathVariable Long classId, @RequestBody CreateReservationRequest request) {
        return reservationService.inscribirAlumno(classId, request);
    }

    @PutMapping("/{reservationId}/cancel")
    public ReservationDTO cancelar(@PathVariable Long reservationId, @RequestBody(required = false) ReservationActionRequest request) {
        return reservationService.cancelarReserva(reservationId, request != null ? request.getNotes() : null);
    }

    @PutMapping("/{reservationId}/check-in")
    public ReservationDTO checkIn(@PathVariable Long reservationId, @RequestBody(required = false) ReservationActionRequest request) {
        return reservationService.checkInReserva(reservationId, request != null ? request.getNotes() : null);
    }

    @PutMapping("/{reservationId}/no-show")
    public ReservationDTO noShow(@PathVariable Long reservationId, @RequestBody(required = false) ReservationActionRequest request) {
        return reservationService.marcarNoAsistio(reservationId, request != null ? request.getNotes() : null);
    }
}
