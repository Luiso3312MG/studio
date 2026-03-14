package com.kbseed.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "membership_plans")
public class MembershipPlanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "studio_id", nullable = false)
    private Long studioId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "plan_code", nullable = false)
    private String planCode;

    @Column(name = "discipline_limit", nullable = false)
    private Integer disciplineLimit;

    @Column(name = "allows_all_disciplines", nullable = false)
    private Boolean allowsAllDisciplines;

    @Column(name = "days_duration", nullable = false)
    private Integer daysDuration;

    @Column(name = "includes_complement", nullable = false)
    private Boolean includesComplement;

    @Column(name = "presotherapy_sessions", nullable = false)
    private Integer presotherapySessions;

    @Column(name = "aparatology_sessions", nullable = false)
    private Integer aparatologySessions;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "is_active", nullable = false)
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
