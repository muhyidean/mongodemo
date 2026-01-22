package edu.miu.mongodemo;

import edu.miu.mongodemo.model.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CounterTest {
    private Counter counter;

    @BeforeEach
    public void setUp() throws Exception {
        counter = new Counter();
    }

    @Test
    public void testIncrement() {
        assertEquals(1, counter.increment(), "Counter.increment does not work correctly");
        assertEquals(2, counter.increment(), "Counter.increment does not work correctly");
    }

    @Test
    public void testDecrement() {
        assertEquals(-1, counter.decrement(), "Counter.decrement does not work correctly");
        assertEquals(-2, counter.decrement(), "Counter.decrement does not work correctly");
    }
}

