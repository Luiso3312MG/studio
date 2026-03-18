package com.kbseed.dto;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;

public class ClientMembershipDTO {
    private Long id;
    private Long clientId;
    private Long membershipPlanId;
    private String membershipPlanName;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private BigDecimal pricePaid;
    private List<Long> disciplineIds;
    private List<String> disciplineNames;
    private Integer remainingPresotherapySessions;
    private Integer remainingAparatologySessions;
    private Boolean canReserveDisciplineToday;
    private String reservationAvailabilityMessage;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public Long getMembershipPlanId() { return membershipPlanId; }
    public void setMembershipPlanId(Long membershipPlanId) { this.membershipPlanId = membershipPlanId; }
    public String getMembershipPlanName() { return membershipPlanName; }
    public void setMembershipPlanName(String membershipPlanName) { this.membershipPlanName = membershipPlanName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public BigDecimal getPricePaid() { return pricePaid; }
    public void setPricePaid(BigDecimal pricePaid) { this.pricePaid = pricePaid; }
    public List<Long> getDisciplineIds() { return disciplineIds; }
    public void setDisciplineIds(List<Long> disciplineIds) { this.disciplineIds = disciplineIds; }
    public List<String> getDisciplineNames() { return disciplineNames; }
    public void setDisciplineNames(List<String> disciplineNames) { this.disciplineNames = disciplineNames; }
    public Integer getRemainingPresotherapySessions() { return remainingPresotherapySessions; }
    public void setRemainingPresotherapySessions(Integer remainingPresotherapySessions) { this.remainingPresotherapySessions = remainingPresotherapySessions; }
    public Integer getRemainingAparatologySessions() { return remainingAparatologySessions; }
    public void setRemainingAparatologySessions(Integer remainingAparatologySessions) { this.remainingAparatologySessions = remainingAparatologySessions; }

    public Boolean getCanReserveDisciplineToday() { return canReserveDisciplineToday; }
    public void setCanReserveDisciplineToday(Boolean canReserveDisciplineToday) { this.canReserveDisciplineToday = canReserveDisciplineToday; }
    public String getReservationAvailabilityMessage() { return reservationAvailabilityMessage; }
    public void setReservationAvailabilityMessage(String reservationAvailabilityMessage) { this.reservationAvailabilityMessage = reservationAvailabilityMessage; }
}
