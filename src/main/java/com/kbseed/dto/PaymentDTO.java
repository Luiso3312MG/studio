package com.kbseed.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaymentDTO {
    private Long id;
    private Long studioId;
    private Long clientId;
    private String clientName;
    private Long clientMembershipId;
    private Long classId;
    private String classTypeName;
    private String classDate;
    private String paymentType; // MEMBRESIA | DROP_IN
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDate paymentDate;
    private String reference;
    private String notes;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudioId() { return studioId; }
    public void setStudioId(Long studioId) { this.studioId = studioId; }
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public Long getClientMembershipId() { return clientMembershipId; }
    public void setClientMembershipId(Long clientMembershipId) { this.clientMembershipId = clientMembershipId; }
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    public String getClassTypeName() { return classTypeName; }
    public void setClassTypeName(String classTypeName) { this.classTypeName = classTypeName; }
    public String getClassDate() { return classDate; }
    public void setClassDate(String classDate) { this.classDate = classDate; }
    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
