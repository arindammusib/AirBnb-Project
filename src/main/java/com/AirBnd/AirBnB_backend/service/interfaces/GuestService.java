package com.AirBnd.AirBnB_backend.service.interfaces;

import com.AirBnd.AirBnB_backend.dto.GuestDTO;

import java.util.List;

public interface GuestService {
    List<GuestDTO> getAllGuests();

    void updateGuest(Long guestId, GuestDTO guestDto);

    void deleteGuest(Long guestId);

    GuestDTO addNewGuest(GuestDTO guestDto);
}
