package com.AirBnd.AirBnB_backend.service.interfaces;

import com.AirBnd.AirBnB_backend.dto.BookingDTO;
import com.AirBnd.AirBnB_backend.dto.BookingRequest;
import com.AirBnd.AirBnB_backend.dto.GuestDTO;
import com.AirBnd.AirBnB_backend.dto.HotelReportDTO;
import com.AirBnd.AirBnB_backend.enums.BookingStatus;
import com.stripe.model.Event;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    BookingDTO initialiseBooking(BookingRequest bookingRequest);
    BookingDTO addGuests(Long bookingId, List<GuestDTO> guestDtoList);
    String initiatePayments(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    BookingStatus getBookingStatus(Long bookingId);

    List<BookingDTO> getAllBookingsByHotelId(Long hotelId);

    HotelReportDTO getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);

    List<BookingDTO> getMyBookings();
}
