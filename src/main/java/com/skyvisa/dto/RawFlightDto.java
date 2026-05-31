package com.skyvisa.dto;

import lombok.Data;

@Data
public class RawFlightDto {

    private String origin;
    private String destination;
    private String destinationCountryIso;
    private String airline;
    private Double price;
    private String departureDate;
    private String imageUrl;

}
