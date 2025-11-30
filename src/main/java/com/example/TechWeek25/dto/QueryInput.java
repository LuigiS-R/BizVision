package com.example.TechWeek25.dto;

//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
public class QueryInput {
    private String latitude;
    private String longitude;
    private String category;

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public QueryInput(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public QueryInput() {}
}
