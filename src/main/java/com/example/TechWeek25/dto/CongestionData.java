package com.example.TechWeek25.dto;

import java.util.List;
import java.util.Map;

public class CongestionData {
    Map<String, Double> weeklyRhythm;
    Map<String, List<Double>> hourlyBreakdown;

    public CongestionData(Map<String, Double> weeklyRhythm, Map<String, List<Double>> hourlyBreakdown) {
        this.weeklyRhythm = weeklyRhythm;
        this.hourlyBreakdown = hourlyBreakdown;
    }

    public Map<String, Double> getWeeklyRhythm() {
        return weeklyRhythm;
    }

    public void setWeeklyRhythm(Map<String, Double> weeklyRhythm) {
        this.weeklyRhythm = weeklyRhythm;
    }

    public Map<String, List<Double>> getHourlyBreakdown() {
        return hourlyBreakdown;
    }

    public void setHourlyBreakdown(Map<String, List<Double>> hourlyBreakdown) {
        this.hourlyBreakdown = hourlyBreakdown;
    }
}
