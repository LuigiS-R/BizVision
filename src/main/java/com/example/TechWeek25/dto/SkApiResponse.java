package com.example.TechWeek25.dto;

import com.example.TechWeek25.entity.CommercialArea;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Safely ignores parts of the JSON we don't need
public class SkApiResponse {

    private Map<String, Object> status;
    private List<CommercialArea> contents;

    public Map<String, Object> getStatus() {
        return status;
    }

    public void setStatus(Map<String, Object> status) {
        this.status = status;
    }

    public List<CommercialArea> getContents() {
        return contents;
    }

    public void setContents(List<CommercialArea> contents) {
        this.contents = contents;
    }
}