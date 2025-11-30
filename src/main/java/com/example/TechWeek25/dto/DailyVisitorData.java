package com.example.TechWeek25.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyVisitorData {
    private double approxVisitorCount;
    private String date;

    public double getApproxVisitorCount() {
        return approxVisitorCount;
    }

    public void setApproxVisitorCount(double approxVisitorCount) {
        this.approxVisitorCount = approxVisitorCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}