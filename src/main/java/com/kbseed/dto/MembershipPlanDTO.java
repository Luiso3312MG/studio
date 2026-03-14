package com.kbseed.dto;

import java.math.BigDecimal;

public class MembershipPlanDTO {
    private Long id;
    private Long studioId;
    private String name;
    private String planCode;
    private Integer disciplineLimit;
    private Boolean allowsAllDisciplines;
    private Integer daysDuration;
    private Boolean includesComplement;
    private Integer presotherapySessions;
    private Integer aparatologySessions;
    private BigDecimal price;
    private Boolean isActive;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudioId() { return studioId; }
    public void setStudioId(Long studioId) { this.studioId = studioId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPlanCode() { return planCode; }
    public void setPlanCode(String planCode) { this.planCode = planCode; }
    public Integer getDisciplineLimit() { return disciplineLimit; }
    public void setDisciplineLimit(Integer disciplineLimit) { this.disciplineLimit = disciplineLimit; }
    public Boolean getAllowsAllDisciplines() { return allowsAllDisciplines; }
    public void setAllowsAllDisciplines(Boolean allowsAllDisciplines) { this.allowsAllDisciplines = allowsAllDisciplines; }
    public Integer getDaysDuration() { return daysDuration; }
    public void setDaysDuration(Integer daysDuration) { this.daysDuration = daysDuration; }
    public Boolean getIncludesComplement() { return includesComplement; }
    public void setIncludesComplement(Boolean includesComplement) { this.includesComplement = includesComplement; }
    public Integer getPresotherapySessions() { return presotherapySessions; }
    public void setPresotherapySessions(Integer presotherapySessions) { this.presotherapySessions = presotherapySessions; }
    public Integer getAparatologySessions() { return aparatologySessions; }
    public void setAparatologySessions(Integer aparatologySessions) { this.aparatologySessions = aparatologySessions; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
}
