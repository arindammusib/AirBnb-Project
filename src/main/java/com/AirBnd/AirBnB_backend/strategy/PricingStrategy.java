package com.AirBnd.AirBnB_backend.strategy;

import com.AirBnd.AirBnB_backend.entities.InventoryEntity;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePrice(InventoryEntity inventory);
}
