package edu.miu.mongodemo;

import edu.miu.mongodemo.model.Calculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculatorTest {
    private Calculator calculator;

    @BeforeEach
    public void setup() {
        calculator = new Calculator();
    }

    @Test
    public void testInitialization() {
        assertEquals(0.0, calculator.getValue(), 0.0000001);
    }

    @Test
    public void testAddZero() {
        calculator.add(0.0);
        assertEquals(0.0, calculator.getValue(), 0.0000001);
    }

    @Test
    public void testAddPositive() {
        calculator.add(23.255);
        assertEquals(23.255, calculator.getValue(), 0.0000001);
    }

    @Test
    public void testAddNegative() {
        calculator.add(-23.255);
        assertEquals(-23.255, calculator.getValue(), 0.0000001);
    }

    @Test
    public void testMultipleAddPositive() {
        calculator.add(23.255);
        calculator.add(10.255);
        assertEquals(33.510, calculator.getValue(), 0.0000001);
    }

    @Test
    public void testMultipleAddNegative() {
        calculator.add(-23.255);
        calculator.add(-10.255);
        assertEquals(-33.510, calculator.getValue(), 0.0000001);
    }

    @Test
    public void testMultipleAddNegativeAndPositive() {
        calculator.add(-23.255);
        calculator.add(10.250);
        assertEquals(-13.005, calculator.getValue(), 0.0000001);
    }
}

