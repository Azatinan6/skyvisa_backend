package com.skyvisa.dto;

import java.time.LocalDate;

import com.skyvisa.entity.Country;
import com.skyvisa.entity.User;
import lombok.Data;

@Data
public class TripDto {

	private Long id;

	private User user;

	private Country destinationCountry;

	private Country destinationCity;

	private Double budget;

	private LocalDate startDate;

	private LocalDate endDate;

}
