package com.AirBnd.AirBnB_backend.strategy;

import com.AirBnd.AirBnB_backend.entities.InventoryEntity;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy{
    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatePrice(InventoryEntity inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        boolean isTodayHoliday = true; // call an API or check with local data
        if (isTodayHoliday) {
            price = price.multiply(BigDecimal.valueOf(1.25));
        }
        return price;
    }
}
