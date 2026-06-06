package com.AirBnd.AirBnB_backend.dto;

import com.AirBnd.AirBnB_backend.enums.Gender;
import lombok.Data;

@Data
public class GuestDTO {
    private Long id;
    private String name;
    private Gender gender;
    private Integer age;
}
