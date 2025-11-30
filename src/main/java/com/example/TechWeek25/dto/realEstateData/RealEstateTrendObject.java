package com.example.TechWeek25.dto.realEstateData;

public class RealEstateTrendObject {
    private String yearMonth;
    private int medianPricePerSqm;

    public RealEstateTrendObject(String yearMonth, int medianPricePerSqm) {
        this.yearMonth = yearMonth;
        this.medianPricePerSqm = medianPricePerSqm;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public int getMedianPricePerSqm() {
        return medianPricePerSqm;
    }

    public void setMedianPricePerSqm(int medianPricePerSqm) {
        this.medianPricePerSqm = medianPricePerSqm;
    }
}
