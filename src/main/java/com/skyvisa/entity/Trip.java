package com.skyvisa.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trips")
public class Trip {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private User user;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "destination_country_id", nullable = false)
	private Country destinationCountry;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "destination_city_id")
	private Country destinationCity;

	private Double budget;

	private LocalDate startDate;

	private LocalDate endDate;

}
