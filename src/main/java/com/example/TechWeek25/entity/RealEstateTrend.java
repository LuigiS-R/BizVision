package com.example.TechWeek25.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class RealEstateTrend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String district;
    private String yearMonth;
    private int medianPricePerSqm;
    private String propertyType;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getYearMonth() { return yearMonth; }
    public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }
    public int getMedianPricePerSqm() { return medianPricePerSqm; }
    public void setMedianPricePerSqm(int medianPricePerSqm) { this.medianPricePerSqm = medianPricePerSqm; }
    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }
}