package com.kbseed.dto;

public class ClassTypeDTO {
    private Long id;
    private Long studioId;
    private String name;
    private String description;
    private Integer durationMinutes;
    private Integer capacity;
    private String level;
    private String category;
    private String serviceKind;
    private Boolean isActive;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudioId() { return studioId; }
    public void setStudioId(Long studioId) { this.studioId = studioId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getServiceKind() { return serviceKind; }
    public void setServiceKind(String serviceKind) { this.serviceKind = serviceKind; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
}
