package com.AirBnd.AirBnB_backend.service.interfaces;

import com.AirBnd.AirBnB_backend.dto.HotelDTO;
import com.AirBnd.AirBnB_backend.dto.HotelInfoDTO;

import java.util.List;

public interface HotelService {

    HotelDTO createNewHotel(HotelDTO hotelDto);
    HotelDTO getHotelById(Long hotelId);

    HotelDTO updateHotelById(Long hotelId, HotelDTO hotelDto);

    void deleteHotelById(Long hotelId);

    void activateHotel(Long hotelId);

    List<HotelDTO> getAllHotels();
    HotelInfoDTO getHotelInfoById(Long hotelId);
}
