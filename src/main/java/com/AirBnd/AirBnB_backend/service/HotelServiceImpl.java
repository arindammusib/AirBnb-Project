package com.AirBnd.AirBnB_backend.service;

import com.AirBnd.AirBnB_backend.dto.HotelDTO;
import com.AirBnd.AirBnB_backend.dto.HotelInfoDTO;
import com.AirBnd.AirBnB_backend.dto.RoomDTO;
import com.AirBnd.AirBnB_backend.entities.HotelEntity;
import com.AirBnd.AirBnB_backend.entities.RoomEntity;
import com.AirBnd.AirBnB_backend.entities.UserEntity;
import com.AirBnd.AirBnB_backend.exceptions.ResourceNotFoundException;
import com.AirBnd.AirBnB_backend.exceptions.UnAuthorisedException;
import com.AirBnd.AirBnB_backend.repository.HotelRepository;
import com.AirBnd.AirBnB_backend.repository.RoomRepository;
import com.AirBnd.AirBnB_backend.service.interfaces.HotelService;
import com.AirBnd.AirBnB_backend.service.interfaces.InventoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.AirBnd.AirBnB_backend.utils.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {
    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;
    @Override
    public HotelDTO createNewHotel(HotelDTO hotelDto) {
        log.info("Creating a new hotel with name: {}", hotelDto.getName());
        HotelEntity hotel=modelMapper.map(hotelDto,HotelEntity.class);
        hotel.setActive(false);
        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotel.setOwner(user);

        hotel = hotelRepository.save(hotel);
        log.info("Created a new hotel with ID: {}", hotel.getId());
        return modelMapper.map(hotel, HotelDTO.class);
    }

    @Override
    public HotelDTO getHotelById(Long hotelId) {
        log.info("Getting the hotel with ID: {}", hotelId);
        HotelEntity hotel=hotelRepository.findById(hotelId).orElseThrow(()->new ResourceNotFoundException("Hotel not found by id:"+hotelId));
        UserEntity user=(UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorisedException("This user does not own this hotel with id: "+hotelId);
        }
        return modelMapper.map(hotel, HotelDTO.class);
    }

    @Override
    public HotelDTO updateHotelById(Long id, HotelDTO hotelDto) {
        log.info("Updating the hotel with ID: {}", id);
        HotelEntity hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+id));

        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: "+id);
        }

        modelMapper.map(hotelDto, hotel);
        hotel.setId(id);
        hotel = hotelRepository.save(hotel);
        return modelMapper.map(hotel, HotelDTO.class);
    }
    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        HotelEntity hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+id));

        UserEntity user = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: "+id);
        }

        for(RoomEntity room: hotel.getRooms()) {
           inventoryService.deleteAllInventories(room);
           roomRepository.deleteById(room.getId());
       }
        hotelRepository.deleteById(id);
    }
    @Override
    @Transactional
    public void activateHotel(Long hotelId) {
        log.info("Activating the hotel with ID: {}", hotelId);
        HotelEntity hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+hotelId));

        UserEntity user = getCurrentUser();
        if(!user.equals(hotel.getOwner())) {
            throw new UnAuthorisedException(
                    "This user does not own this hotel with id: "
                            + hotelId);
        }
        if(hotel.getActive()) {
            throw new IllegalStateException(
                    "Hotel is already active: " + hotelId);
        }

        hotel.setActive(true);
        hotelRepository.save(hotel);
        // assuming only do it once
        for(RoomEntity room: hotel.getRooms()) {
            inventoryService.initializeRoomForAYear(room);
        }
        log.info("Hotel activated successfully: {}", hotelId);
    }

    @Override
    public List<HotelDTO> getAllHotels() {
        UserEntity user = getCurrentUser();
        log.info("Getting all hotels for the admin user with ID: {}", user.getId());
        List<HotelEntity> hotels = hotelRepository.findByOwner(user);

        return hotels
                .stream()
                .map((element) -> modelMapper.map(element, HotelDTO.class))
                .collect(Collectors.toList());
    }
    @Override
    public HotelInfoDTO getHotelInfoById(Long hotelId) {
        HotelEntity hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+hotelId));

        List<RoomDTO> rooms = hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDTO.class))
                .toList();

        return new HotelInfoDTO(modelMapper.map(hotel, HotelDTO.class), rooms);
    }

}
