package com.kbseed.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "studio_id", nullable = false)
    private Long studioId;

    @Column(name = "class_id", nullable = false)
    private Long classId;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "reservation_status", nullable = false)
    private String reservationStatus;

    @Column(name = "booked_at")
    @CreationTimestamp
    private LocalDateTime bookedAt;

    @Column(name = "checked_in_at")
    private LocalDateTime checkedInAt;

    @Column(name = "cancellation_at")
    private LocalDateTime cancellationAt;

    @Column(name = "notes")
    private String notes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudioId() {
        return studioId;
    }

    public void setStudioId(Long studioId) {
        this.studioId = studioId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public LocalDateTime getCheckedInAt() {
        return checkedInAt;
    }

    public void setCheckedInAt(LocalDateTime checkedInAt) {
        this.checkedInAt = checkedInAt;
    }

    public LocalDateTime getCancellationAt() {
        return cancellationAt;
    }

    public void setCancellationAt(LocalDateTime cancellationAt) {
        this.cancellationAt = cancellationAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}