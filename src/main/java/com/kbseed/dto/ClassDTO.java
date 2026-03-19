package com.kbseed.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ClassDTO {
    private Long id;
    private Long studioId;
    private String classTypeName;
    private String roomName;
    private String coachUserName;
    private LocalDate classDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer capacity;
    private Integer capacityTotal;
    private String status;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudioId() { return studioId; }
    public void setStudioId(Long studioId) { this.studioId = studioId; }
    public String getClassTypeName() { return classTypeName; }
    public void setClassTypeName(String classTypeName) { this.classTypeName = classTypeName; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public String getCoachUserName() { return coachUserName; }
    public void setCoachUserName(String coachUserName) { this.coachUserName = coachUserName; }
    public LocalDate getClassDate() { return classDate; }
    public void setClassDate(LocalDate classDate) { this.classDate = classDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Integer getCapacityTotal() { return capacityTotal; }
    public void setCapacityTotal(Integer capacityTotal) { this.capacityTotal = capacityTotal; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
