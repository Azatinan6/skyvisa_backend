package com.skyvisa.dto;

import com.skyvisa.entity.Country;
import lombok.Data;

@Data
public class VisaRuleDto {

    private Long id;

    private Country targetCountry;

    private Country citizenCountry;

    private String status;

    private Integer maxDays;

    private String details;
}
