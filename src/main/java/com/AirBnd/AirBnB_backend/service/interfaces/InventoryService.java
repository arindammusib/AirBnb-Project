package com.AirBnd.AirBnB_backend.service.interfaces;

import com.AirBnd.AirBnB_backend.dto.HotelPriceDTO;
import com.AirBnd.AirBnB_backend.dto.HotelSearchRequest;
import com.AirBnd.AirBnB_backend.dto.InventoryDTO;
import com.AirBnd.AirBnB_backend.dto.UpdateInventoryRequestDTO;
import com.AirBnd.AirBnB_backend.entities.RoomEntity;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface InventoryService {
    void deleteAllInventories(RoomEntity room);

    void initializeRoomForAYear(RoomEntity room);
    void updateInventoryPrices(RoomEntity room, BigDecimal newPrice);
    List<InventoryDTO> getAllInventoryByRoom(Long roomId);
    void updateInventory(Long roomId, UpdateInventoryRequestDTO updateInventoryRequestDto);
    Page<HotelPriceDTO> searchHotels(HotelSearchRequest hotelSearchRequest);

}
