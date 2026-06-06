package com.AirBnd.AirBnB_backend.repository;

import com.AirBnd.AirBnB_backend.entities.HotelEntity;
import com.AirBnd.AirBnB_backend.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<HotelEntity,Long> {
    List<HotelEntity> findByOwner(UserEntity user);
}
