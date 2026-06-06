package com.AirBnd.AirBnB_backend.repository;

import com.AirBnd.AirBnB_backend.dto.GuestDTO;
import com.AirBnd.AirBnB_backend.entities.GuestEntity;
import com.AirBnd.AirBnB_backend.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestRepository extends JpaRepository<GuestEntity,Long> {
    List<GuestDTO> findByUser(UserEntity user);
}
