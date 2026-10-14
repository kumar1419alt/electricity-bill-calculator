package com.electricity.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the entire bill calculation response, including consumer details,
 * progressive slabs, total amount, and validation status.
 */
public class BillResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String errorMessage;
    private String consumerName;
    private String consumerNumber;
    private double totalUnits;
    private double totalAmount;
    private String formattedTotalUnits;
    private String formattedTotalAmount;
    private String billDate;
    private List<TariffSlab> slabs = new ArrayList<>();

    public BillResult() {
    }

    public static BillResult error(String message) {
        BillResult result = new BillResult();
        result.setSuccess(false);
        result.setErrorMessage(message);
        return result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public String getConsumerNumber() {
        return consumerNumber;
    }

    public void setConsumerNumber(String consumerNumber) {
        this.consumerNumber = consumerNumber;
    }

    public double getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(double totalUnits) {
        this.totalUnits = totalUnits;
        this.formattedTotalUnits = String.format("%.2f", totalUnits);
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
        this.formattedTotalAmount = String.format("₹%,.2f", totalAmount);
    }

    public String getFormattedTotalUnits() {
        return formattedTotalUnits;
    }

    public String getFormattedTotalAmount() {
        return formattedTotalAmount;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public List<TariffSlab> getSlabs() {
        return slabs;
    }

    public void setSlabs(List<TariffSlab> slabs) {
        this.slabs = slabs;
    }

    public void addSlab(TariffSlab slab) {
        this.slabs.add(slab);
    }
}
