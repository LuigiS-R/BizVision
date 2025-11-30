package com.example.TechWeek25.dto.realEstateData;

import java.util.List;

public class RealEstateTrends {
    private List<RealEstateTrendObject> commercial;
    private List<RealEstateTrendObject> office;

    public RealEstateTrends(List<RealEstateTrendObject> commercial, List<RealEstateTrendObject> office) {
        this.commercial = commercial;
        this.office = office;
    }

    public List<RealEstateTrendObject> getCommercial() {
        return commercial;
    }

    public void setCommercial(List<RealEstateTrendObject> commercial) {
        this.commercial = commercial;
    }

    public List<RealEstateTrendObject> getOffice() {
        return office;
    }

    public void setOffice(List<RealEstateTrendObject> office) {
        this.office = office;
    }
}
