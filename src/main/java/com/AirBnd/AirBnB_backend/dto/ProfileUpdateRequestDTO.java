package com.AirBnd.AirBnB_backend.dto;

import com.AirBnd.AirBnB_backend.enums.Gender;
import lombok.Data;

import java.time.LocalDate;
@Data
public class ProfileUpdateRequestDTO {
    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;
}
