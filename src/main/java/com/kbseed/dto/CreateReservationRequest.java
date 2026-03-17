package com.kbseed.dto;

import java.math.BigDecimal;

public class CreateReservationRequest {

    private Long clientId;
    private String notes;
    private Boolean useSingleClassPayment;
    private BigDecimal paymentAmount;
    private String paymentMethod;
    private String paymentReference;

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

    public Boolean getUseSingleClassPayment() {
        return useSingleClassPayment;
    }

    public void setUseSingleClassPayment(Boolean useSingleClassPayment) {
        this.useSingleClassPayment = useSingleClassPayment;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }
}
