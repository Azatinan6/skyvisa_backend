package com.skyvisa.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlightRecommendationDTO {
    private String destinationCountry;
    private String destinationCity;
    private Double price;
    private String airline;
    private String visaStatus; // "VISA_FREE", "REQUIRED", "SCHENGEN"
    private String visaDetails;
    private String imageUrl;
}