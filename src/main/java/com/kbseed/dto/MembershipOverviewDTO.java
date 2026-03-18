package com.kbseed.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;

public class MembershipOverviewDTO {
    private Long clientId;
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private String membershipStatus;
    private String planName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate membershipEndDate;
    private List<String> disciplines;
    private Integer remainingPresotherapySessions;
    private Integer remainingAparatologySessions;
    private String reservationAvailabilityMessage;

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }
    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }
    public String getMembershipStatus() { return membershipStatus; }
    public void setMembershipStatus(String membershipStatus) { this.membershipStatus = membershipStatus; }
    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public LocalDate getMembershipEndDate() { return membershipEndDate; }
    public void setMembershipEndDate(LocalDate membershipEndDate) { this.membershipEndDate = membershipEndDate; }
    public List<String> getDisciplines() { return disciplines; }
    public void setDisciplines(List<String> disciplines) { this.disciplines = disciplines; }
    public Integer getRemainingPresotherapySessions() { return remainingPresotherapySessions; }
    public void setRemainingPresotherapySessions(Integer remainingPresotherapySessions) { this.remainingPresotherapySessions = remainingPresotherapySessions; }
    public Integer getRemainingAparatologySessions() { return remainingAparatologySessions; }
    public void setRemainingAparatologySessions(Integer remainingAparatologySessions) { this.remainingAparatologySessions = remainingAparatologySessions; }

    public String getReservationAvailabilityMessage() { return reservationAvailabilityMessage; }
    public void setReservationAvailabilityMessage(String reservationAvailabilityMessage) { this.reservationAvailabilityMessage = reservationAvailabilityMessage; }
}
