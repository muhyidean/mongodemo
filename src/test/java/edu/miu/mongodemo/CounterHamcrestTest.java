package edu.miu.mongodemo;

import edu.miu.mongodemo.model.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CounterHamcrestTest {
    private Counter counter;

    @BeforeEach
    public void setUp() {
        counter = new Counter();
    }

    @Test
    public void testInitialValue() {
        assertThat("Initial counter value should be 0", counter.getCounterValue(), is(equalTo(0)));
        assertThat(counter.getCounterValue(), is(0));
    }

    @Test
    public void testIncrement() {
        int result1 = counter.increment();
        assertThat("First increment should return 1", result1, is(1));
        assertThat("Counter value should be 1", counter.getCounterValue(), equalTo(1));

        int result2 = counter.increment();
        assertThat("Second increment should return 2", result2, is(2));
        assertThat("Counter value should be 2", counter.getCounterValue(), is(equalTo(2)));
    }

    @Test
    public void testDecrement() {
        int result1 = counter.decrement();
        assertThat("First decrement should return -1", result1, is(-1));
        assertThat("Counter value should be -1", counter.getCounterValue(), equalTo(-1));

        int result2 = counter.decrement();
        assertThat("Second decrement should return -2", result2, is(-2));
        assertThat("Counter value should be -2", counter.getCounterValue(), is(equalTo(-2)));
    }

    @Test
    public void testIncrementIsGreaterThan() {
        counter.increment();
        assertThat("Counter should be greater than 0", counter.getCounterValue(), greaterThan(0));
        assertThat("Counter should be greater than or equal to 1", counter.getCounterValue(), greaterThanOrEqualTo(1));
    }

    @Test
    public void testDecrementIsLessThan() {
        counter.decrement();
        assertThat("Counter should be less than 0", counter.getCounterValue(), lessThan(0));
        assertThat("Counter should be less than or equal to -1", counter.getCounterValue(), lessThanOrEqualTo(-1));
    }

    @Test
    public void testIncrementAndDecrement() {
        counter.increment();
        counter.increment();
        counter.decrement();
        assertThat("After increment twice and decrement once, value should be 1", 
                   counter.getCounterValue(), is(1));
    }
}

