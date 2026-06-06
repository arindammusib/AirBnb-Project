package com.AirBnd.AirBnB_backend.repository;

import com.AirBnd.AirBnB_backend.dto.HotelPriceDTO;
import com.AirBnd.AirBnB_backend.entities.HotelEntity;
import com.AirBnd.AirBnB_backend.entities.HotelMinPriceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HotelMinPriceRepository extends JpaRepository<HotelMinPriceEntity,Long> {
    @Query("""
            SELECT new com.AirBnd.AirBnB_backend.dto.HotelPriceDTO(i.hotel, AVG(i.price))
            FROM HotelMinPriceEntity i
            WHERE i.hotel.city = :city
                AND i.date BETWEEN :startDate AND :endDate
                AND i.hotel.active = true
                AND i.availableRooms >= :roomsCount
           GROUP BY i.hotel
            HAVING COUNT(i.date) = :dateCount
           """)
    Page<HotelPriceDTO> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
           @Param("roomsCount") Integer roomsCount,
           @Param("dateCount") Long dateCount,
            Pageable pageable
    );
    Optional<HotelMinPriceEntity> findByHotelAndDate(HotelEntity hotel, LocalDate date);

}
