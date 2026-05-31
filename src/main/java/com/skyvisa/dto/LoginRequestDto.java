package com.skyvisa.dto;

import lombok.Data;

@Data
public class LoginRequestDto {

    private String fullName;
    private String email;
    private String password;

}
