package com.AirBnd.AirBnB_backend.service.interfaces;

import com.AirBnd.AirBnB_backend.dto.RoomDTO;
import com.AirBnd.AirBnB_backend.entities.RoomEntity;

import java.math.BigDecimal;
import java.util.List;

public interface RoomService {
    RoomDTO createNewRoom(Long hotelId, RoomDTO roomDTO);

    List<RoomDTO> getAllRoomsInHotel(Long hotelId);
    RoomDTO getRoomById(Long roomId);
    void deleteRoomById(Long roomId);
    RoomDTO updateRoomById(Long hotelId, Long roomId, RoomDTO roomDto);

}
