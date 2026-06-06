package com.AirBnd.AirBnB_backend.repository;

import com.AirBnd.AirBnB_backend.entities.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity,Long> {
}
