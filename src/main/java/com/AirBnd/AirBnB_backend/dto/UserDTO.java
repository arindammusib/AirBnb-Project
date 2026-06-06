package com.AirBnd.AirBnB_backend.dto;

import com.AirBnd.AirBnB_backend.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private Gender gender;
    private LocalDate dateOfBirth;

}
