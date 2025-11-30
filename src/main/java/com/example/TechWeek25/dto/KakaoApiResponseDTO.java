package com.example.TechWeek25.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

public class KakaoApiResponseDTO {

    @JsonProperty("meta")
    private MetaDTO meta;

    // Getters and setters
    public MetaDTO getMeta() {
        return meta;
    }

    public void setMeta(MetaDTO meta) {
        this.meta = meta;
    }
}