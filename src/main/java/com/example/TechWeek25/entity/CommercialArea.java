package com.example.TechWeek25.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data; // Awesome library for less boilerplate
import lombok.NoArgsConstructor;

@Data
@Entity // Tells Spring this is a database entity
@Table(name = "commercial_area") // Maps this class to the "commercial_area" table
public class CommercialArea {
    @Id // Marks this field as the primary key
    @Column(name = "area_id") // Maps this field to the "area_id" column
    private Long areaId;

    @Column(name = "area_name", nullable = false) // Maps to "area_name", cannot be null
    private String areaName;

    private String city;
    private String district;
    private double latitude;
    private double longitude;

    public CommercialArea(Long areaId, String areaName, String city, String district, double latitude, double longitude) {
        this.areaId = areaId;
        this.areaName = areaName;
        this.city = city;
        this.district = district;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public CommercialArea() {
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}