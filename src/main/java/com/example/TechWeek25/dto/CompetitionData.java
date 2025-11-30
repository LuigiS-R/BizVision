package com.example.TechWeek25.dto;

import com.example.TechWeek25.dto.realEstateData.RealEstateTrendObject;
import com.example.TechWeek25.dto.realEstateData.RealEstateTrends;

import java.util.List;
import java.util.Map;

// Or, the traditional class way:
public class CompetitionData {
    private int feasibilityIndex;
    private int tailoredFeasibilityIndex;
    private final int totalCount;
    private final Map<String, Integer> categoryCounts;
    //double averageCongestion;
    //int averageCongestionLevel;
    CongestionData congestionData;
    Map<String, Double> visitorsDistribution;
    //Map <String, List<RealEstateTrendObject>> realEstateTrends;
    RealEstateTrends realEstateTrends;

    public CompetitionData(int totalCount, Map<String, Integer> categoryCounts) {
        this.totalCount = totalCount;
        this.categoryCounts = categoryCounts;
    }

    public RealEstateTrends getRealEstateTrends() {
        return realEstateTrends;
    }

    public void setRealEstateTrends(RealEstateTrends realEstateTrends) {
        this.realEstateTrends = realEstateTrends;
    }

    /*public double getAverageCongestion() {
        return averageCongestion;
    }

    public void setAverageCongestion(double averageCongestion) {
        this.averageCongestion = averageCongestion;
    }

    public int getAverageCongestionLevel() {
        return averageCongestionLevel;
    }

    public void setAverageCongestionLevel(int averageCongestionLevel) {
        this.averageCongestionLevel = averageCongestionLevel;
    }*/

    public int getTotalCount() {
        return totalCount;
    }

    public Map<String, Integer> getCategoryCounts() {
        return categoryCounts;
    }

    public CongestionData getCongestionData() {
        return congestionData;
    }

    public void setCongestionData(CongestionData congestionData) {
        this.congestionData = congestionData;
    }

    public Map<String, Double> getVisitorsDistribution() {
        return visitorsDistribution;
    }

    public void setVisitorsDistribution(Map<String, Double> visitorsDistribution) {
        this.visitorsDistribution = visitorsDistribution;
    }

    public int getFeasibilityIndex() {
        return feasibilityIndex;
    }

    public void setFeasibilityIndex(int feasibilityIndex) {
        this.feasibilityIndex = feasibilityIndex;
    }

    public int getTailoredFeasibilityIndex() {
        return tailoredFeasibilityIndex;
    }

    public void setTailoredFeasibilityIndex(int tailoredFeasibilityIndex) {
        this.tailoredFeasibilityIndex = tailoredFeasibilityIndex;
    }
}