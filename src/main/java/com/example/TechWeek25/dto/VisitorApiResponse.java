package com.example.TechWeek25.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisitorApiResponse {
    private Map<String, Object> status;
    private VisitorContents contents; // <-- Now uses our specific nested DTO

    public Map<String, Object> getStatus() {
        return status;
    }

    public void setStatus(Map<String, Object> status) {
        this.status = status;
    }

    public VisitorContents getContents() {
        return contents;
    }

    public void setContents(VisitorContents contents) {
        this.contents = contents;
    }
}