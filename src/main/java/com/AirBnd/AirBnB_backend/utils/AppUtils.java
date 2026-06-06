package com.AirBnd.AirBnB_backend.utils;

import com.AirBnd.AirBnB_backend.entities.UserEntity;
import org.springframework.security.core.context.SecurityContextHolder;

public class AppUtils {
    public static UserEntity getCurrentUser() {
        return (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
