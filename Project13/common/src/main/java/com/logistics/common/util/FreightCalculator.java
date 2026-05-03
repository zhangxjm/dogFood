package com.logistics.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FreightCalculator {

    private static final BigDecimal BASE_PRICE = new BigDecimal("10.00");
    private static final BigDecimal PER_KG_PRICE = new BigDecimal("3.00");
    private static final BigDecimal PER_M3_PRICE = new BigDecimal("50.00");
    private static final int SCALE = 2;

    public static BigDecimal calculate(Double weight, Double volume, Double distance) {
        if (weight == null || weight <= 0) {
            weight = 1.0;
        }
        if (volume == null || volume <= 0) {
            volume = 0.01;
        }
        if (distance == null || distance <= 0) {
            distance = 1.0;
        }

        BigDecimal weightCharge = new BigDecimal(weight).multiply(PER_KG_PRICE);
        BigDecimal volumeCharge = new BigDecimal(volume).multiply(PER_M3_PRICE);

        BigDecimal chargeByWeight = weightCharge.multiply(new BigDecimal(distance / 100.0));
        BigDecimal chargeByVolume = volumeCharge.multiply(new BigDecimal(distance / 100.0));

        BigDecimal maxCharge = chargeByWeight.max(chargeByVolume);

        BigDecimal totalPrice = BASE_PRICE.add(maxCharge);

        return totalPrice.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static BigDecimal simpleCalculate(Double weight, Double volume) {
        if (weight == null || weight <= 0) {
            weight = 1.0;
        }
        if (volume == null || volume <= 0) {
            volume = 0.01;
        }

        BigDecimal weightCharge = new BigDecimal(weight).multiply(PER_KG_PRICE);
        BigDecimal volumeCharge = new BigDecimal(volume).multiply(PER_M3_PRICE);

        BigDecimal maxCharge = weightCharge.max(volumeCharge);
        BigDecimal totalPrice = BASE_PRICE.add(maxCharge);

        return totalPrice.setScale(SCALE, RoundingMode.HALF_UP);
    }
}
