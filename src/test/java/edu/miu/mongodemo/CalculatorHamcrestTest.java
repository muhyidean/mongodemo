package edu.miu.mongodemo;

import edu.miu.mongodemo.model.Calculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CalculatorHamcrestTest {
    private Calculator calculator;

    @BeforeEach
    public void setup() {
        calculator = new Calculator();
    }

    @Test
    public void testInitialization() {
        assertThat("Initial value should be 0.0", calculator.getValue(), is(0.0));
        assertThat("Initial value should be close to 0.0", calculator.getValue(), closeTo(0.0, 0.0000001));
    }

    @Test
    public void testAddZero() {
        calculator.add(0.0);
        assertThat(calculator.getValue(), is(0.0));
        assertThat(calculator.getValue(), closeTo(0.0, 0.0000001));
    }

    @Test
    public void testAddPositive() {
        calculator.add(23.255);
        assertThat("Value should be 23.255", calculator.getValue(), closeTo(23.255, 0.0000001));
        assertThat(calculator.getValue(), is(greaterThan(0.0)));
        assertThat(calculator.getValue(), is(greaterThanOrEqualTo(23.255)));
    }

    @Test
    public void testAddNegative() {
        calculator.add(-23.255);
        assertThat("Value should be -23.255", calculator.getValue(), closeTo(-23.255, 0.0000001));
        assertThat(calculator.getValue(), is(lessThan(0.0)));
        assertThat(calculator.getValue(), is(lessThanOrEqualTo(-23.255)));
    }

    @Test
    public void testMultipleAddPositive() {
        calculator.add(23.255);
        calculator.add(10.255);
        assertThat("Sum should be 33.510", calculator.getValue(), closeTo(33.510, 0.0000001));
        assertThat(calculator.getValue(), is(both(greaterThan(30.0)).and(lessThan(40.0))));
    }

    @Test
    public void testMultipleAddNegative() {
        calculator.add(-23.255);
        calculator.add(-10.255);
        assertThat("Sum should be -33.510", calculator.getValue(), closeTo(-33.510, 0.0000001));
        assertThat(calculator.getValue(), is(both(lessThan(-30.0)).and(greaterThan(-40.0))));
    }

    @Test
    public void testMultipleAddNegativeAndPositive() {
        calculator.add(-23.255);
        calculator.add(10.250);
        assertThat("Result should be -13.005", calculator.getValue(), closeTo(-13.005, 0.0000001));
        assertThat(calculator.getValue(), is(lessThan(0.0)));
    }

    @Test
    public void testValueIsNotZeroAfterAdding() {
        calculator.add(5.5);
        assertThat("Value should not be zero", calculator.getValue(), is(not(0.0)));
        assertThat("Value should not be close to zero", calculator.getValue(), is(not(closeTo(0.0, 0.1))));
    }
}

