package edu.miu.mongodemo;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Comprehensive examples of Hamcrest matchers for testing.
 * This class demonstrates various Hamcrest matcher capabilities.
 */
public class HamcrestExamplesTest {

    @Test
    public void testCoreMatchers() {
        // EqualTo - checks equality
        assertThat(5, is(equalTo(5)));
        assertThat("hello", equalTo("hello"));

        // Is - syntactic sugar, can be used with other matchers
        assertThat(10, is(10));
        assertThat("test", is("test"));
    }

    @Test
    public void testLogicalMatchers() {
        // Not - negates a matcher
        assertThat(5, is(not(10)));
        assertThat("hello", is(not(equalTo("world"))));

        // Both/And - combines multiple matchers with AND
        assertThat(5, is(both(greaterThan(0)).and(lessThan(10))));

        // Either/Or - combines multiple matchers with OR
        assertThat(5, is(either(equalTo(5)).or(equalTo(10))));

        // AllOf - all matchers must pass
        assertThat(7, allOf(greaterThan(5), lessThan(10), not(equalTo(8))));

        // AnyOf - at least one matcher must pass
        assertThat(7, anyOf(equalTo(5), equalTo(7), equalTo(10)));
    }

    @Test
    public void testNumericMatchers() {
        int value = 5;

        // Greater than / Less than
        assertThat(value, is(greaterThan(0)));
        assertThat(value, is(lessThan(10)));
        assertThat(value, is(greaterThanOrEqualTo(5)));
        assertThat(value, is(lessThanOrEqualTo(5)));

        // CloseTo - for floating point comparison
        double pi = 3.14159;
        assertThat(pi, closeTo(3.14, 0.01));
        assertThat(pi, is(closeTo(3.1416, 0.0001)));
    }

    @Test
    public void testStringMatchers() {
        String text = "Hello World";

        // Contains string
        assertThat(text, containsString("Hello"));
        assertThat(text, containsStringIgnoringCase("hello"));

        // Starts with / Ends with
        assertThat(text, startsWith("Hello"));
        assertThat(text, endsWith("World"));

        // Pattern matching
        assertThat(text, matchesPattern("^Hello.*"));

        // Empty string
        assertThat("", is(emptyString()));
        assertThat("not empty", is(not(emptyString())));

        // Blank string (empty or whitespace)
        assertThat("   ", is(blankString()));
    }

    @Test
    public void testCollectionMatchers() {
        List<String> list = Arrays.asList("apple", "banana", "cherry");
        List<String> emptyList = new ArrayList<>();
        List<String> nullList = null;

        // Empty collection
        assertThat(emptyList, is(empty()));
        assertThat(list, is(not(empty())));

        // Null collection
        assertThat(nullList, is(nullValue()));
        assertThat(list, is(not(nullValue())));

        // Collection size
        assertThat(list, hasSize(3));
        assertThat(list, hasSize(greaterThan(2)));

        // Contains items
        assertThat(list, hasItem("banana"));
        assertThat(list, hasItems("apple", "cherry"));
        assertThat(list, contains("apple", "banana", "cherry")); // exact order
        assertThat(list, containsInAnyOrder("cherry", "apple", "banana")); // any order

        // Every item matches
        List<Integer> numbers = Arrays.asList(2, 4, 6, 8);
        assertThat(numbers, everyItem(is(greaterThan(0))));
        assertThat(numbers, everyItem(anyOf(equalTo(2), equalTo(4), equalTo(6), equalTo(8))));

        // Has item matching a matcher
        assertThat(list, hasItem(startsWith("app")));
        assertThat(list, hasItem(equalTo("banana")));
    }

    @Test
    public void testMapMatchers() {
        Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);

        // Has key / Has value
        assertThat(map, hasKey("one"));
        assertThat(map, hasValue(1));
        assertThat(map, hasEntry("two", 2));

        // Map size - check through keySet size
        assertThat(map.keySet(), hasSize(3));
    }

    @Test
    public void testObjectMatchers() {
        String str = "test";
        String nullStr = null;

        // Null / Not null
        assertThat(nullStr, is(nullValue()));
        assertThat(str, is(notNullValue()));

        // Same instance
        String str2 = str;
        assertThat(str, is(sameInstance(str2)));

        // Instance of
        assertThat(str, is(instanceOf(String.class)));
        assertThat(str, isA(String.class));
    }

    @Test
    public void testDescriptiveAssertions() {
        // Custom message for better test failure reporting
        int result = 15;
        assertThat("Result should be greater than 10", result, is(greaterThan(10)));
        assertThat("Result should be less than 20", result, is(lessThan(20)));
        // Custom messages help identify which assertion failed
    }

    @Test
    public void testArrayMatchers() {
        int[] array = {1, 2, 3, 4, 5};
        String[] stringArray = {"a", "b", "c"};

        // Array equality
        int[] expected = {1, 2, 3, 4, 5};
        assertThat(array, is(equalTo(expected)));

        // Array length - use array.length directly or convert to list
        assertThat(array.length, is(5));
        assertThat(array.length, is(greaterThan(3)));

        // Array contains - convert to list or use array matcher
        List<Integer> arrayAsList = Arrays.asList(Arrays.stream(array).boxed().toArray(Integer[]::new));
        assertThat(arrayAsList, hasItem(3));
        List<String> stringArrayAsList = Arrays.asList(stringArray);
        assertThat(stringArrayAsList, hasItem("b"));
    }

    @Test
    public void testTypeMatchers() {
        Object obj = "test";
        Number num = 42;

        // Type checking
        assertThat(obj, is(instanceOf(String.class)));
        assertThat(num, is(instanceOf(Integer.class)));
        assertThat(obj, isA(Object.class));
    }

    @Test
    public void testComposedMatchers() {
        // Complex matcher combinations
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        assertThat(numbers, hasSize(5));
        assertThat(numbers, hasItem(3));
        assertThat(numbers, everyItem(is(greaterThan(0))));
        assertThat(numbers, everyItem(is(lessThan(10))));

        String text = "The quick brown fox";
        assertThat(text, allOf(
            is(not(emptyString())),
            containsString("quick"),
            startsWith("The"),
            matchesPattern(".*fox$")
        ));
    }
}

