package com.AirBnd.AirBnB_backend.service.interfaces;

import com.AirBnd.AirBnB_backend.entities.BookingEntity;

public interface CheckoutService {

    String getCheckoutSession(BookingEntity booking, String successUrl, String failureUrl);
}
