package com.AirBnd.AirBnB_backend.service;

import com.AirBnd.AirBnB_backend.dto.RoomDTO;
import com.AirBnd.AirBnB_backend.entities.HotelEntity;
import com.AirBnd.AirBnB_backend.entities.RoomEntity;
import com.AirBnd.AirBnB_backend.entities.UserEntity;
import com.AirBnd.AirBnB_backend.exceptions.ResourceNotFoundException;
import com.AirBnd.AirBnB_backend.exceptions.UnAuthorisedException;
import com.AirBnd.AirBnB_backend.repository.HotelRepository;
import com.AirBnd.AirBnB_backend.repository.RoomRepository;
import com.AirBnd.AirBnB_backend.service.interfaces.InventoryService;
import com.AirBnd.AirBnB_backend.service.interfaces.RoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.AirBnd.AirBnB_backend.utils.AppUtils.getCurrentUser;


@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private  final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;

    @Override
    public RoomDTO createNewRoom(Long hotelId, RoomDTO roomDto) {
        log.info("Creating a new room in hotel with ID: {}", hotelId);
        HotelEntity hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+hotelId));

        UserEntity currentUser = getCurrentUser();
        if(!currentUser.equals(hotel.getOwner())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: "+hotelId);
        }
        RoomEntity room = modelMapper.map(roomDto, RoomEntity.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);

        if (hotel.getActive()) {
            inventoryService.initializeRoomForAYear(room);
        }

        return modelMapper.map(room, RoomDTO.class);

    }

    @Override
    public List<RoomDTO> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting all rooms in hotel with ID: {}", hotelId);
        HotelEntity hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+hotelId));

        UserEntity currentUser = getCurrentUser();
        if(!currentUser.equals(hotel.getOwner())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: "+hotelId);
        }

        return hotel.getRooms()
                .stream()
                .map((rooms) -> modelMapper.map(rooms, RoomDTO.class))
                .collect(Collectors.toList());
    }
    @Override
    public RoomDTO getRoomById(Long roomId) {
        log.info("Getting the room with ID: {}", roomId);
        RoomEntity room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: "+roomId));
        return modelMapper.map(room, RoomDTO.class);
    }
    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        log.info("Deleting the room with ID: {}", roomId);
        RoomEntity room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: "+roomId));

        UserEntity currentUser = getCurrentUser();
        if(!currentUser.equals(room.getHotel().getOwner())) {
            throw new UnAuthorisedException("This user does not own this room with id: "+roomId);
        }
        inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(roomId);
    }
    @Override
    @Transactional
    public RoomDTO updateRoomById(Long hotelId, Long roomId,RoomDTO roomDto) {
        log.info("Updating the room with ID: {}", roomId);

        HotelEntity hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hotel not found with ID: " + hotelId));

        UserEntity user = getCurrentUser();
        if(!user.equals(hotel.getOwner())) {
            throw new UnAuthorisedException(
                    "This user does not own this hotel with id: "
                            + hotelId);
        }
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with ID: " + roomId));
        BigDecimal oldPrice = room.getBasePrice();
        modelMapper.map(roomDto, room);
        room.setId(roomId);


        room = roomRepository.save(room);
        // Check if price changed
        BigDecimal newPrice = room.getBasePrice();

        if(oldPrice.compareTo(newPrice) != 0) {
            log.info("Price changed from {} to {} for room: {}",
                    oldPrice, newPrice, roomId);

            inventoryService.updateInventoryPrices(room, newPrice);
        }

        return modelMapper.map(room, RoomDTO.class);
    }
}
