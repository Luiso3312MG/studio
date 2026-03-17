package com.kbseed.dto;

import jakarta.validation.constraints.NotNull;

public class CreateReservationRequest {

    @NotNull(message = "El clientId es obligatorio")
    private Long clientId;
    private String notes;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
