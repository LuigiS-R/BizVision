package com.example.TechWeek25.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisitorContents {
    private String areaId;
    private String areaName;
    private String gender;
    private String ageGrp;
    private List<DailyVisitorData> raw;

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAgeGrp() {
        return ageGrp;
    }

    public void setAgeGrp(String ageGrp) {
        this.ageGrp = ageGrp;
    }

    public List<DailyVisitorData> getRaw() {
        return raw;
    }

    public void setRaw(List<DailyVisitorData> raw) {
        this.raw = raw;
    }

    // The 'raw' list is nested here!
}
