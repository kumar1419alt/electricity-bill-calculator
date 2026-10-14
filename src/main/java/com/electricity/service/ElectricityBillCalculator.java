package com.electricity.service;

import com.electricity.model.BillResult;
import com.electricity.model.TariffSlab;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service responsible for validating input and calculating electricity bills
 * strictly adhering to progressive slab-based tariff rules.
 *
 * Tariff rules:
 * - First 50 units: ₹3.50 per unit
 * - Next 100 units (51–150): ₹4.00 per unit
 * - Next 100 units (151–250): ₹5.20 per unit
 * - Above 250 units: ₹6.50 per unit
 */
public class ElectricityBillCalculator {

    public static final double RATE_SLAB_1 = 3.50; // 0 to 50
    public static final double RATE_SLAB_2 = 4.00; // 51 to 150
    public static final double RATE_SLAB_3 = 5.20; // 151 to 250
    public static final double RATE_SLAB_4 = 6.50; // Above 250

    public static final double LIMIT_SLAB_1 = 50.0;
    public static final double LIMIT_SLAB_2 = 100.0;
    public static final double LIMIT_SLAB_3 = 100.0;

    /**
     * Calculates the electricity bill progressively for given consumer details and units.
     *
     * @param consumerName   Name of consumer
     * @param consumerNumber Consumer account/meter number
     * @param unitsStr       Units consumed as String
     * @return BillResult containing breakdown or error message
     */
    public BillResult calculate(String consumerName, String consumerNumber, String unitsStr) {
        // 1. Validation: Consumer Name
        if (consumerName == null || consumerName.trim().isEmpty()) {
            return BillResult.error("Consumer Name is required and cannot be blank.");
        }
        String trimmedName = consumerName.trim();
        if (trimmedName.length() < 2 || trimmedName.length() > 60) {
            return BillResult.error("Consumer Name must be between 2 and 60 characters.");
        }

        // 2. Validation: Consumer Number
        if (consumerNumber == null || consumerNumber.trim().isEmpty()) {
            return BillResult.error("Consumer Number is required and cannot be blank.");
        }
        String trimmedNumber = consumerNumber.trim();
        if (!trimmedNumber.matches("^[a-zA-Z0-9\\-_/]{3,30}$")) {
            return BillResult.error("Consumer Number must be 3-30 characters long and contain only letters, numbers, hyphens, slashes, or underscores.");
        }

        // 3. Validation: Units Consumed
        if (unitsStr == null || unitsStr.trim().isEmpty()) {
            return BillResult.error("Units Consumed is required.");
        }

        double units;
        try {
            units = Double.parseDouble(unitsStr.trim());
        } catch (NumberFormatException e) {
            return BillResult.error("Invalid units: Please enter a valid non-negative numeric value.");
        }

        if (Double.isNaN(units) || Double.isInfinite(units)) {
            return BillResult.error("Invalid units: Numeric value out of acceptable range.");
        }

        if (units < 0) {
            return BillResult.error("Units consumed cannot be negative. Please enter a value of 0 or greater.");
        }

        if (units > 1_000_000) {
            return BillResult.error("Units consumed exceeds maximum allowed limit (1,000,000 units).");
        }

        return computeBill(trimmedName, trimmedNumber, units);
    }

    /**
     * Performs pure progressive slab calculation.
     */
    public BillResult computeBill(String consumerName, String consumerNumber, double units) {
        BillResult result = new BillResult();
        result.setSuccess(true);
        result.setConsumerName(consumerName);
        result.setConsumerNumber(consumerNumber);
        result.setTotalUnits(units);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");
        result.setBillDate(LocalDateTime.now().format(dtf));

        double remainingUnits = units;

        // Slab 1: First 50 units (0–50) @ ₹3.50/unit
        double slab1Units = 0.0;
        if (remainingUnits > 0) {
            slab1Units = Math.min(remainingUnits, LIMIT_SLAB_1);
            remainingUnits -= slab1Units;
        }
        double slab1Amount = slab1Units * RATE_SLAB_1;
        result.addSlab(new TariffSlab("Slab 1 (First 50 units)", "0 – 50 units", RATE_SLAB_1, slab1Units, slab1Amount));

        // Slab 2: Next 100 units (51–150) @ ₹4.00/unit
        double slab2Units = 0.0;
        if (remainingUnits > 0) {
            slab2Units = Math.min(remainingUnits, LIMIT_SLAB_2);
            remainingUnits -= slab2Units;
        }
        double slab2Amount = slab2Units * RATE_SLAB_2;
        result.addSlab(new TariffSlab("Slab 2 (Next 100 units)", "51 – 150 units", RATE_SLAB_2, slab2Units, slab2Amount));

        // Slab 3: Next 100 units (151–250) @ ₹5.20/unit
        double slab3Units = 0.0;
        if (remainingUnits > 0) {
            slab3Units = Math.min(remainingUnits, LIMIT_SLAB_3);
            remainingUnits -= slab3Units;
        }
        double slab3Amount = slab3Units * RATE_SLAB_3;
        result.addSlab(new TariffSlab("Slab 3 (Next 100 units)", "151 – 250 units", RATE_SLAB_3, slab3Units, slab3Amount));

        // Slab 4: Above 250 units (> 250) @ ₹6.50/unit
        double slab4Units = 0.0;
        if (remainingUnits > 0) {
            slab4Units = remainingUnits;
            remainingUnits = 0.0;
        }
        double slab4Amount = slab4Units * RATE_SLAB_4;
        result.addSlab(new TariffSlab("Slab 4 (Above 250 units)", "> 250 units", RATE_SLAB_4, slab4Units, slab4Amount));

        double totalAmount = slab1Amount + slab2Amount + slab3Amount + slab4Amount;
        // Round to 2 decimal places strictly
        totalAmount = Math.round(totalAmount * 100.0) / 100.0;
        result.setTotalAmount(totalAmount);

        return result;
    }
}
