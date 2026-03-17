package com.kbseed.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateReservationRequest {

    @NotNull(message = "El clientId es obligatorio")
    private Long clientId;

    @Size(max = 255, message = "Las notas no pueden exceder 255 caracteres")
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
