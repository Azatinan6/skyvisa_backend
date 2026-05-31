package com.skyvisa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "visa_rules")
public class VisaRule {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "target_country_id", nullable = false)
	private	Country targetCountry;
	
	
	@ManyToOne
    @JoinColumn(name = "citizen_country_id", nullable = false)
    private Country citizenCountry; 

    @Column(nullable = false)
    private String status; 

    private Integer maxDays;
    
    @Column(length = 500)
    private String details;
	
	
}