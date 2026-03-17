package com.kbseed.controller;

import com.kbseed.dto.DropInPaymentRequest;
import com.kbseed.dto.PaymentDTO;
import com.kbseed.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public List<PaymentDTO> listar() {
        return paymentService.listarPagos();
    }

    @GetMapping("/client/{clientId}")
    public List<PaymentDTO> listarPorCliente(@PathVariable Long clientId) {
        return paymentService.listarPagosPorCliente(clientId);
    }

    @PostMapping("/drop-in")
    public PaymentDTO registrarDropIn(@RequestBody DropInPaymentRequest request) {
        return paymentService.registrarDropIn(request);
    }
}
