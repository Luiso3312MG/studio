package com.kbseed.dto;

import java.math.BigDecimal;
import java.util.List;

public class ActivateMembershipRequest {
    private Long planId;
    private List<Long> disciplineIds;
    private BigDecimal amount;
    private String paymentMethod;
    private String reference;
    private String notes;

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public List<Long> getDisciplineIds() { return disciplineIds; }
    public void setDisciplineIds(List<Long> disciplineIds) { this.disciplineIds = disciplineIds; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
