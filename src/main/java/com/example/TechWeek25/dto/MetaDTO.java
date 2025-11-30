package com.example.TechWeek25.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MetaDTO {
    @JsonProperty("total_count")
    private int totalCount;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public MetaDTO(int totalCount) {
        this.totalCount = totalCount;
    }
}
