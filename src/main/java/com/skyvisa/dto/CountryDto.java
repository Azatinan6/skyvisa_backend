package com.skyvisa.dto;

import lombok.Data;

@Data
public class CountryDto {

	private Long id;

	private String isoCode;

	private String name;

	private String imageUrl;

}