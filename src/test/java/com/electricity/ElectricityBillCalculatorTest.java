package com.electricity;

import com.electricity.model.BillResult;
import com.electricity.model.TariffSlab;
import com.electricity.service.ElectricityBillCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class ElectricityBillCalculatorTest {

    private ElectricityBillCalculator calculator;

    @BeforeEach
    public void setUp() {
        calculator = new ElectricityBillCalculator();
    }

    @Test
    @DisplayName("Test 0 Units: Expected Total = ₹0.00")
    public void testZeroUnits() {
        BillResult result = calculator.calculate("Rahul Sharma", "CONS-1001", "0");
        assertTrue(result.isSuccess());
        assertEquals(0.00, result.getTotalAmount(), 0.001);
        assertEquals("₹0.00", result.getFormattedTotalAmount());
    }

    @Test
    @DisplayName("Test 50 Units: 50 * 3.50 = ₹175.00")
    public void testFiftyUnits() {
        BillResult result = calculator.calculate("Rahul Sharma", "CONS-1001", "50");
        assertTrue(result.isSuccess());
        assertEquals(175.00, result.getTotalAmount(), 0.001);
        
        TariffSlab slab1 = result.getSlabs().get(0);
        assertEquals(50.0, slab1.getUnitsConsumed(), 0.001);
        assertEquals(175.00, slab1.getSlabAmount(), 0.001);
    }

    @Test
    @DisplayName("Test 51 Units: (50 * 3.50) + (1 * 4.00) = ₹179.00")
    public void testFiftyOneUnits() {
        BillResult result = calculator.calculate("Rahul Sharma", "CONS-1001", "51");
        assertTrue(result.isSuccess());
        assertEquals(179.00, result.getTotalAmount(), 0.001);
    }

    @Test
    @DisplayName("Test 150 Units: (50 * 3.50) + (100 * 4.00) = 175 + 400 = ₹575.00")
    public void testOneHundredFiftyUnits() {
        BillResult result = calculator.calculate("Rahul Sharma", "CONS-1001", "150");
        assertTrue(result.isSuccess());
        assertEquals(575.00, result.getTotalAmount(), 0.001);
    }

    @Test
    @DisplayName("Test 151 Units: 575 + (1 * 5.20) = ₹580.20")
    public void testOneHundredFiftyOneUnits() {
        BillResult result = calculator.calculate("Rahul Sharma", "CONS-1001", "151");
        assertTrue(result.isSuccess());
        assertEquals(580.20, result.getTotalAmount(), 0.001);
    }

    @Test
    @DisplayName("Test 250 Units: (50 * 3.50) + (100 * 4.00) + (100 * 5.20) = 175 + 400 + 520 = ₹1,095.00")
    public void testTwoHundredFiftyUnits() {
        BillResult result = calculator.calculate("Rahul Sharma", "CONS-1001", "250");
        assertTrue(result.isSuccess());
        assertEquals(1095.00, result.getTotalAmount(), 0.001);
    }

    @Test
    @DisplayName("Test 251 Units: 1095 + (1 * 6.50) = ₹1,101.50")
    public void testTwoHundredFiftyOneUnits() {
        BillResult result = calculator.calculate("Rahul Sharma", "CONS-1001", "251");
        assertTrue(result.isSuccess());
        assertEquals(1101.50, result.getTotalAmount(), 0.001);
    }

    @Test
    @DisplayName("Test 300 Units: 175 + 400 + 520 + (50 * 6.50 = 325) = ₹1,420.00")
    public void testThreeHundredUnits() {
        BillResult result = calculator.calculate("Rahul Sharma", "CONS-1001", "300");
        assertTrue(result.isSuccess());
        assertEquals(1420.00, result.getTotalAmount(), 0.001);

        // Verify all 4 slabs breakdown
        assertEquals(50.0, result.getSlabs().get(0).getUnitsConsumed(), 0.001);
        assertEquals(175.00, result.getSlabs().get(0).getSlabAmount(), 0.001);

        assertEquals(100.0, result.getSlabs().get(1).getUnitsConsumed(), 0.001);
        assertEquals(400.00, result.getSlabs().get(1).getSlabAmount(), 0.001);

        assertEquals(100.0, result.getSlabs().get(2).getUnitsConsumed(), 0.001);
        assertEquals(520.00, result.getSlabs().get(2).getSlabAmount(), 0.001);

        assertEquals(50.0, result.getSlabs().get(3).getUnitsConsumed(), 0.001);
        assertEquals(325.00, result.getSlabs().get(3).getSlabAmount(), 0.001);
    }

    @ParameterizedTest(name = "Units: {0} => Expected Total: {1}")
    @CsvSource({
        "0, 0.00",
        "50, 175.00",
        "51, 179.00",
        "150, 575.00",
        "151, 580.20",
        "250, 1095.00",
        "251, 1101.50",
        "300, 1420.00"
    })
    public void testAllRequiredTestCases(String units, double expectedTotal) {
        BillResult result = calculator.calculate("Ankush Kumar", "ELEC-2026-99", units);
        assertTrue(result.isSuccess());
        assertEquals(expectedTotal, result.getTotalAmount(), 0.001);
    }

    @Test
    @DisplayName("Test Validation: Negative Units")
    public void testNegativeUnits() {
        BillResult result = calculator.calculate("Rahul", "C-123", "-15");
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("cannot be negative"));
    }

    @Test
    @DisplayName("Test Validation: Non-Numeric Units")
    public void testNonNumericUnits() {
        BillResult result = calculator.calculate("Rahul", "C-123", "abc");
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("numeric value"));
    }

    @Test
    @DisplayName("Test Validation: Empty Consumer Name")
    public void testEmptyConsumerName() {
        BillResult result = calculator.calculate("  ", "C-123", "100");
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("Consumer Name is required"));
    }

    @Test
    @DisplayName("Test Validation: Empty Consumer Number")
    public void testEmptyConsumerNumber() {
        BillResult result = calculator.calculate("Rahul", "", "100");
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("Consumer Number is required"));
    }
}
