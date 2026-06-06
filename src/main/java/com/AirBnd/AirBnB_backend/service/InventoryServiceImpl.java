package com.AirBnd.AirBnB_backend.service;

import com.AirBnd.AirBnB_backend.dto.HotelPriceDTO;
import com.AirBnd.AirBnB_backend.dto.HotelSearchRequest;
import com.AirBnd.AirBnB_backend.dto.InventoryDTO;
import com.AirBnd.AirBnB_backend.dto.UpdateInventoryRequestDTO;
import com.AirBnd.AirBnB_backend.entities.InventoryEntity;
import com.AirBnd.AirBnB_backend.entities.RoomEntity;
import com.AirBnd.AirBnB_backend.entities.UserEntity;
import com.AirBnd.AirBnB_backend.exceptions.ResourceNotFoundException;
import com.AirBnd.AirBnB_backend.repository.HotelMinPriceRepository;
import com.AirBnd.AirBnB_backend.repository.InventoryRepository;
import com.AirBnd.AirBnB_backend.repository.RoomRepository;
import com.AirBnd.AirBnB_backend.service.interfaces.InventoryService;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.AirBnd.AirBnB_backend.utils.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final RoomRepository roomRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;

    @Override
    public void deleteAllInventories(RoomEntity room) {
        log.info("Deleting the inventories of room with id: {}", room.getId());
        inventoryRepository.deleteByRoom(room);
    }
    @Override
    public void initializeRoomForAYear(RoomEntity room) {
        log.info("Initializing inventory for room: {}",
                room.getId());

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);


        List<InventoryEntity> inventoryList = new ArrayList<>();

        for (; !today.isAfter(endDate);
             today = today.plusDays(1)) {

            InventoryEntity inventory = InventoryEntity.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookCount(0)
                    .reservedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();

            inventoryList.add(inventory);
        }


        inventoryRepository.saveAll(inventoryList);

        log.info("Created {} inventory records for room: {}",
                inventoryList.size(), room.getId());
    }

    @Override
    @Transactional
    public void updateInventoryPrices(RoomEntity room,BigDecimal newPrice) {
        log.info("Updating inventory prices for room: {}",
                room.getId());

        inventoryRepository.updateFutureInventoryPrices(
                room.getId(),
                newPrice,
                LocalDate.now()
        );

        log.info("Updated future inventory prices for room: {}",
                room.getId());
    }

    @Override
    public List<InventoryDTO> getAllInventoryByRoom(Long roomId) {
        log.info("Getting All inventory by room for room with id: {}", roomId);
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: "+roomId));

        UserEntity user = getCurrentUser();
        if(!user.equals(room.getHotel().getOwner())) throw new AccessDeniedException("You are not the owner of room with id: "+roomId);

        return inventoryRepository.findByRoomOrderByDate(room).stream()
                .map((element) -> modelMapper.map(element,
                        InventoryDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDTO updateInventoryRequestDto) {
        log.info("Updating All inventory by room for room with id: {} between date range: {} - {}", roomId,
                updateInventoryRequestDto.getStartDate(), updateInventoryRequestDto.getEndDate());
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: "+roomId));

        UserEntity user = getCurrentUser();
//        if(!user.equals(room.getHotel().getOwner())) throw new AccessDeniedException("You are not the owner of room with id: "+roomId);
//        inventoryRepository.getInventoryAndLockBeforeUpdate(roomId, updateInventoryRequestDto.getStartDate(),
//                updateInventoryRequestDto.getEndDate());
//        log.info("LOCK ACQUIRED for room: {}", roomId);
//        try {
//            log.info("Sleeping 15 seconds... try booking now!");
//            Thread.sleep(15000); // 15 second sleep
//
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
        inventoryRepository.updateInventory(roomId, updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate(), updateInventoryRequestDto.getClosed(),
                updateInventoryRequestDto.getSurgeFactor());
        log.info(" UPDATE DONE, lock will release now");
    }
    @Override
    public Page<HotelPriceDTO> searchHotels(HotelSearchRequest hotelSearchRequest) {
        log.info("Searching hotels for {} city, from {} to {}", hotelSearchRequest.getCity(), hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate());
        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());
        long dateCount =
                ChronoUnit.DAYS.between(hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate()) + 1;

        Page<HotelPriceDTO> hotelPage = hotelMinPriceRepository.findHotelsWithAvailableInventory(hotelSearchRequest.getCity(),
                hotelSearchRequest.getStartDate(), hotelSearchRequest.getEndDate(),hotelSearchRequest.getRoomsCount(),dateCount,pageable);

        return hotelPage.map((element) -> modelMapper.map(element, HotelPriceDTO.class));
    }
}
