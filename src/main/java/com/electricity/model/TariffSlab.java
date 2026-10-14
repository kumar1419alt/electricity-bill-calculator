package com.electricity.model;

import java.io.Serializable;

/**
 * Represents a single tariff slab breakdown in the progressive billing calculation.
 */
public class TariffSlab implements Serializable {
    private static final long serialVersionUID = 1L;

    private String slabTitle;
    private String slabRange;
    private double ratePerUnit;
    private double unitsConsumed;
    private double slabAmount;
    private String formattedRate;
    private String formattedUnits;
    private String formattedAmount;

    public TariffSlab() {
    }

    public TariffSlab(String slabTitle, String slabRange, double ratePerUnit, double unitsConsumed, double slabAmount) {
        this.slabTitle = slabTitle;
        this.slabRange = slabRange;
        this.ratePerUnit = ratePerUnit;
        this.unitsConsumed = unitsConsumed;
        this.slabAmount = slabAmount;
        this.formattedRate = String.format("₹%.2f", ratePerUnit);
        this.formattedUnits = String.format("%.2f", unitsConsumed);
        this.formattedAmount = String.format("₹%.2f", slabAmount);
    }

    public String getSlabTitle() {
        return slabTitle;
    }

    public void setSlabTitle(String slabTitle) {
        this.slabTitle = slabTitle;
    }

    public String getSlabRange() {
        return slabRange;
    }

    public void setSlabRange(String slabRange) {
        this.slabRange = slabRange;
    }

    public double getRatePerUnit() {
        return ratePerUnit;
    }

    public void setRatePerUnit(double ratePerUnit) {
        this.ratePerUnit = ratePerUnit;
    }

    public double getUnitsConsumed() {
        return unitsConsumed;
    }

    public void setUnitsConsumed(double unitsConsumed) {
        this.unitsConsumed = unitsConsumed;
    }

    public double getSlabAmount() {
        return slabAmount;
    }

    public void setSlabAmount(double slabAmount) {
        this.slabAmount = slabAmount;
    }

    public String getFormattedRate() {
        return formattedRate;
    }

    public String getFormattedUnits() {
        return formattedUnits;
    }

    public String getFormattedAmount() {
        return formattedAmount;
    }
}
