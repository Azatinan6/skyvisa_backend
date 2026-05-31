package com.skyvisa.dto;

import java.util.List;
import com.skyvisa.entity.Trip;
import lombok.Data;

@Data
public class UserDto {

	private Long id;

	private String email;

	private String passwordHash;

	private String fullName;

	private List<Trip> trips;

}
