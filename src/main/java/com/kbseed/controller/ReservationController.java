package com.kbseed.controller;

import com.kbseed.dto.CreateReservationRequest;
import com.kbseed.dto.ReservationDTO;
import com.kbseed.service.ReservationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classes")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/{classId}/reservations")
    public ReservationDTO inscribirAlumno(
            @PathVariable Long classId,
            @RequestBody CreateReservationRequest request
    ) {
        return reservationService.inscribirAlumno(classId, request);
    }
}