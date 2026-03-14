package com.kbseed.dto;

import java.util.List;

public class UpdateMembershipDisciplinesRequest {
    private List<Long> disciplineIds;

    public List<Long> getDisciplineIds() { return disciplineIds; }
    public void setDisciplineIds(List<Long> disciplineIds) { this.disciplineIds = disciplineIds; }
}
