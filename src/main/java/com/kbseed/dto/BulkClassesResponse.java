package com.kbseed.dto;

import java.util.List;

public class BulkClassesResponse {

    private Integer totalCreated;
    private List<Long> createdIds;

    public Integer getTotalCreated() {
        return totalCreated;
    }

    public void setTotalCreated(Integer totalCreated) {
        this.totalCreated = totalCreated;
    }

    public List<Long> getCreatedIds() {
        return createdIds;
    }

    public void setCreatedIds(List<Long> createdIds) {
        this.createdIds = createdIds;
    }
}
