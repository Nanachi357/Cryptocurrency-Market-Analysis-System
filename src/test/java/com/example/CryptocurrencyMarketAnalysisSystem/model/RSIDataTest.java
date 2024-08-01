package com.example.CryptocurrencyMarketAnalysisSystem.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RSIDataTest {
    //Positive Tests

    @Test
    public void testConstructorWithValidData() { 
        List<String> dates = List.of("2024-07-07");
        List<Double> rsiValues = List.of(70.0);
        RSIData rsiData = new RSIData(dates, rsiValues);

        assertEquals(1, rsiData.dates().size());
        assertEquals(1, rsiData.rsiValues().size());
        assertEquals("2024-07-07", rsiData.dates().get(0));
        assertEquals(70.0, rsiData.rsiValues().get(0));
    }

    //Negative Tests

    @Test
    public void testConstructorWithNullDates() { 
        List<Double> rsiValues = List.of(70.0);
        assertThrows(NullPointerException.class, () -> new RSIData(null, rsiValues));
    }

    @Test
    public void testConstructorWithNullRsiValues() { 
        List<String> dates = List.of("2024-07-07");
        assertThrows(NullPointerException.class, () -> new RSIData(dates, null));
    }

    @Test
    public void testConstructorWithNullDateElement() { 
        List<String> dates = new ArrayList<>(Arrays.asList("2024-07-07", null));
        List<Double> rsiValues = List.of(70.0);
        assertThrows(NullPointerException.class, () -> new RSIData(dates, rsiValues));
    }

    @Test
    public void testConstructorWithNullRsiValueElement() { 
        List<String> dates = List.of("2024-07-07");
        List<Double> rsiValues = new ArrayList<>(Arrays.asList(70.0, null));
        assertThrows(NullPointerException.class, () -> new RSIData(dates, rsiValues));
    }

    @Test
    public void testConstructorWithNegativeRsiValue() { 
        List<String> dates = List.of("2024-07-07");
        List<Double> rsiValues = List.of(-1.0);
        assertThrows(IllegalArgumentException.class, () -> new RSIData(dates, rsiValues));
    }

    @Test
    public void testConstructorWithRsiValueAbove100() { 
        List<String> dates = List.of("2024-07-07");
        List<Double> rsiValues = List.of(101.0);
        assertThrows(IllegalArgumentException.class, () -> new RSIData(dates, rsiValues));
    }

    //Boundary Tests

    @Test
    public void testConstructorWithLargeRsiValuesList() { 
        List<String> dates = List.of("2024-07-07");
        List<Double> rsiValues = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            rsiValues.add(50.0);
        }
        RSIData rsiData = new RSIData(dates, rsiValues);
        assertEquals(10000, rsiData.rsiValues().size());
    }

    @Test
    public void testConstructorWithBoundaryRsiValues() { 
        List<String> dates = List.of("2024-07-07");
        List<Double> rsiValues = Arrays.asList(0.0, 100.0);
        RSIData rsiData = new RSIData(dates, rsiValues);
        assertEquals(2, rsiData.rsiValues().size());
        assertEquals(0.0, rsiData.rsiValues().get(0));
        assertEquals(100.0, rsiData.rsiValues().get(1));
    }

    @Test
    public void testConstructorWithLargeDatesList() { 
        List<String> dates = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            dates.add("2023-07-01");
        }
        List<Double> rsiValues = List.of(50.0);
        RSIData rsiData = new RSIData(dates, rsiValues);
        assertEquals(10000, rsiData.dates().size());
    }

    @Test
    public void testConstructorWithBoundaryDates() { 
        List<String> dates = Arrays.asList("1970-01-01", "9999-12-31");
        List<Double> rsiValues = List.of(50.0);
        RSIData rsiData = new RSIData(dates, rsiValues);
        assertEquals(2, rsiData.dates().size());
        assertEquals("1970-01-01", rsiData.dates().get(0));
        assertEquals("9999-12-31", rsiData.dates().get(1));
    }

    //Requirements Conformance Tests

    @Test
    public void testInitializationWithEmptyLists() { 
        List<String> dates = new ArrayList<>();
        List<Double> rsiValues = new ArrayList<>();
        RSIData rsiData = new RSIData(dates, rsiValues);
        assertNotNull(rsiData.dates());
        assertNotNull(rsiData.rsiValues());
        assertTrue(rsiData.dates().isEmpty());
        assertTrue(rsiData.rsiValues().isEmpty());
    }

    @Test
    public void testCorrectDataTypes() { 
        List<String> dates = Arrays.asList("2023-01-01", "2023-01-02");
        List<Double> rsiValues = Arrays.asList(30.5, 70.5);
        RSIData rsiData = new RSIData(dates, rsiValues);
        assertEquals(dates, rsiData.dates());
        assertEquals(rsiValues, rsiData.rsiValues());
    }

    @Test
    public void testDatesImmutability() { 
        List<String> dates = new ArrayList<>(Arrays.asList("2023-01-01", "2023-01-02"));
        List<Double> rsiValues = Arrays.asList(30.5, 70.5);
        RSIData rsiData = new RSIData(dates, rsiValues);
        dates.set(0, "2023-01-03");
        assertNotEquals("2023-01-03", rsiData.dates().get(0));
    }

    @Test
    public void testRsiValuesImmutability() { 
        List<String> dates = Arrays.asList("2023-01-01", "2023-01-02");
        List<Double> rsiValues = new ArrayList<>(Arrays.asList(30.5, 70.5));
        RSIData rsiData = new RSIData(dates, rsiValues);
        rsiValues.set(0, 50.0);
        assertNotEquals(50.0, rsiData.rsiValues().get(0));
    }
}
