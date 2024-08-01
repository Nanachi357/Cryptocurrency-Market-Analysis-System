package com.example.CryptocurrencyMarketAnalysisSystem.util;

import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class DateUtilsTest {
    private static final long MIN_MILLIS = -31557014167219200L; // Approx equivalent to Instant.MIN
    private static final long MAX_MILLIS = 31556889864403199L; // Approx equivalent to Instant.MAX

    //Positive Tests

    @Test
    public void testConvertDateToMillis() {
        String date = "2024-07-08T12:34:56.789";
        ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.parse(date), ZoneId.of("UTC"));
        Long expectedMillis = zdt.toInstant().toEpochMilli();
        Long actualMillis = DateUtils.convertDateToMillis(date, ZoneId.of("UTC"));
        assertEquals(expectedMillis, actualMillis);
    }

    @Test
    public void testConvertMillisToLocalDateTime() {
        long millis = 1730697296789L;
        LocalDateTime expectedDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
        LocalDateTime actualDateTime = DateUtils.convertMillisToLocalDateTime(millis);
        assertEquals(expectedDateTime, actualDateTime);
    }

    @Test
    public void testConvertMillisToDate() {

        long millis = 1730697296789L;
        String expectedDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        String actualDate = DateUtils.convertMillisToDate(millis);
        assertEquals(expectedDate, actualDate);
    }

    @Test
    public void testConvertToTimestamp() {
        String date = "2024-07-08";
        String time = "12:34:56";
        ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.parse(date + "T" + time), ZoneId.systemDefault());
        Long expectedTimestamp = zdt.toInstant().toEpochMilli();
        Long actualTimestamp = DateUtils.convertToTimestamp(date, time);
        assertEquals(expectedTimestamp, actualTimestamp);
    }

    @Test
    public void testConvertMillisToUtcLocalDateTime() {
        long millis = 1730697296789L;
        ZonedDateTime expectedZdt = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC"));
        LocalDateTime expectedDateTime = expectedZdt.toLocalDateTime();
        LocalDateTime actualDateTime = DateUtils.convertMillisToUtcLocalDateTime(millis);
        assertEquals(expectedDateTime, actualDateTime);
    }

    @Test
    public void testConvertLocalDateTimeToMillis() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 7, 8, 12, 34, 56, 789000000);
        ZonedDateTime zdt = ZonedDateTime.of(dateTime, ZoneId.of("UTC"));
        Long expectedMillis = zdt.toInstant().toEpochMilli();
        Long actualMillis = DateUtils.convertLocalDateTimeToMillis(dateTime);
        assertEquals(expectedMillis, actualMillis);
    }

    //Negative Tests

    @Test
    public void testConvertMillisToLocalDateTimeWithNull() {
        assertThrows(IllegalArgumentException.class, () -> DateUtils.convertMillisToLocalDateTime(null));
    }

    @Test
    public void testConvertDateToMillisWithInvalidDateFormat() {
        assertThrows(java.time.format.DateTimeParseException.class, () -> DateUtils.convertDateToMillis("invalid-date-format", ZoneId.of("UTC")));
    }

    @Test
    public void testConvertToTimestampWithInvalidDateFormat() {
        assertThrows(java.time.format.DateTimeParseException.class, () -> DateUtils.convertToTimestamp("invalid-date-format", "12:00:00"));
    }

    @Test
    public void testConvertToTimestampWithInvalidTimeFormat() {
        assertThrows(java.time.format.DateTimeParseException.class, () -> DateUtils.convertToTimestamp("2024-07-08", "invalid-time-format"));
    }

    @Test
    public void testConvertDateToMillisWithNull() {
        assertThrows(NullPointerException.class, () -> DateUtils.convertDateToMillis(null, ZoneId.of("UTC")));
    }

    @Test
    public void testConvertToTimestampWithNullDate() {
        assertThrows(IllegalArgumentException.class, () -> DateUtils.convertToTimestamp(null, "12:00:00"));
    }

    @Test
    public void testConvertToTimestampWithNullTime() {
        String date = "2024-07-08";
        Long expectedTimestamp = LocalDateTime.of(LocalDate.parse(date), LocalTime.MIDNIGHT)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        assertEquals(expectedTimestamp, DateUtils.convertToTimestamp(date, null));
    }

    @Test
    public void testConvertDateToMillisWithEmptyString() {
        assertThrows(java.time.format.DateTimeParseException.class, () -> DateUtils.convertDateToMillis("", ZoneId.of("UTC")));
    }

    @Test
    public void testConvertToTimestampWithEmptyDate() {
        assertThrows(IllegalArgumentException.class, () -> DateUtils.convertToTimestamp("", "12:00:00"));
    }


    @Test
    public void testConvertToTimestampWithEmptyTime() {
        String date = "2024-07-08";
        Long expectedTimestamp = LocalDateTime.of(LocalDate.parse(date), LocalTime.MIDNIGHT)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        assertEquals(expectedTimestamp, DateUtils.convertToTimestamp(date, ""));
    }

    @Test
    public void testConvertMillisToLocalDateTimeWithOutOfRangeValue() {
        long outOfRangeMillis = Instant.MAX.getEpochSecond() * 1000 + 1; 
        assertThrows(DateTimeException.class, () -> DateUtils.convertMillisToLocalDateTime(outOfRangeMillis));
    }

    @Test
    public void testConvertMillisToUtcLocalDateTimeWithOutOfRangeValue() {
        long outOfRangeMillis = Instant.MAX.getEpochSecond() * 1000 + 1; 
        assertThrows(DateTimeException.class, () -> DateUtils.convertMillisToUtcLocalDateTime(outOfRangeMillis));
    }

    //Boundary Tests

    @Test
    public void testConvertMillisToLocalDateTimeWithMinValue() {
        LocalDateTime expectedDateTime = ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(MIN_MILLIS), ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime actualDateTime = DateUtils.convertMillisToLocalDateTime(MIN_MILLIS);
        assertEquals(expectedDateTime, actualDateTime);
    }

    @Test
    public void testConvertMillisToLocalDateTimeWithMaxValue() {
        LocalDateTime expectedDateTime = ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(MAX_MILLIS), ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime actualDateTime = DateUtils.convertMillisToLocalDateTime(MAX_MILLIS);
        assertEquals(expectedDateTime, actualDateTime);
    }

    @Test
    public void testConvertLocalDateTimeToMillisWithMinValue() {
        LocalDateTime minDateTime = LocalDateTime.ofEpochSecond(MIN_MILLIS / 1000, 0, ZoneOffset.UTC);
        long actualMillis = DateUtils.convertLocalDateTimeToMillis(minDateTime);
        assertEquals(MIN_MILLIS, actualMillis, 200, "Comparison of milliseconds for the minimum value");
    }

    @Test
    public void testConvertLocalDateTimeToMillisWithMaxValue() {
        LocalDateTime maxDateTime = LocalDateTime.ofEpochSecond(MAX_MILLIS / 1000, 0, ZoneOffset.UTC);
        long actualMillis = DateUtils.convertLocalDateTimeToMillis(maxDateTime);
        assertEquals(MAX_MILLIS, actualMillis, 200, "Comparison of milliseconds for the maximum value");
    }

    @Test
    public void testConvertToTimestampWithMinValue() {
        String minDate = "0000-01-01";
        String minTime = "00:00:00";
        long expectedTimestamp = LocalDateTime.of(LocalDate.of(0, 1, 1), LocalTime.MIDNIGHT)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long actualTimestamp = DateUtils.convertToTimestamp(minDate, minTime);
        assertEquals(expectedTimestamp, actualTimestamp);
    }

    @Test
    public void testConvertToTimestampWithMaxValue() {
        String maxDate = "9999-12-31";
        String maxTime = "23:59:59";
        long expectedTimestamp = LocalDateTime.of(LocalDate.of(9999, 12, 31), LocalTime.of(23, 59, 59))
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Long actualTimestamp = DateUtils.convertToTimestamp(maxDate, maxTime);
        assertEquals(expectedTimestamp, actualTimestamp);
    }

    //Output Verification Tests

    @Test
    public void testConvertMillisToDateFormat() {
        long millis = 1627845600000L; // 2021-08-01T12:00:00.000Z
        String dateString = DateUtils.convertMillisToDate(millis);
        assertTrue(Pattern.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}", dateString), "Date format is incorrect");
    }

    @Test
    public void testConvertMillisToUtcLocalDateTimeCorrectness() {
        long millis = 1627845600000L; // 2021-08-01T19:20:00.000Z
        LocalDateTime expectedDateTime = LocalDateTime.of(2021, 8, 1, 19, 20, 0);
        LocalDateTime actualDateTime = DateUtils.convertMillisToUtcLocalDateTime(millis);

        assertThat(actualDateTime)
                .as("UTC LocalDateTime conversion is incorrect")
                .isEqualToIgnoringNanos(expectedDateTime);
    }

    @Test
    public void testConvertDateToMillisOutputFormat() {
        String date = "2024-07-08T12:34:56.789";
        long millis = DateUtils.convertDateToMillis(date, ZoneId.of("UTC"));
        String expectedDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        assertEquals(date, expectedDate, "Output format for convertDateToMillis is incorrect");
    }

    @Test
    public void testConvertMillisToLocalDateTimeOutputFormat() {
        long millis = 1730697296789L;
        LocalDateTime localDateTime = DateUtils.convertMillisToLocalDateTime(millis);
        String expectedDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        assertEquals(localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")), expectedDate, "Output format for convertMillisToLocalDateTime is incorrect");
    }

    @Test
    public void testConvertMillisToDateOutputFormat() {
        long millis = 1730697296789L;
        String date = DateUtils.convertMillisToDate(millis);
        assertTrue(Pattern.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}", date), "Output format for convertMillisToDate is incorrect");
    }

    @Test
    public void testConvertToTimestampOutputFormat() {
        String date = "2024-07-08";
        String time = "12:34:56";
        long timestamp = DateUtils.convertToTimestamp(date, time);
        LocalDateTime actualDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        LocalDateTime expectedDateTime = LocalDateTime.of(LocalDate.parse(date), LocalTime.parse(time));

        assertEquals(expectedDateTime, actualDateTime, "Output format for convertToTimestamp is incorrect");
    }

    @Test
    public void testConvertMillisToUtcLocalDateTimeOutputFormat() {
        long millis = 1730697296789L;
        LocalDateTime utcLocalDateTime = DateUtils.convertMillisToUtcLocalDateTime(millis);
        String expectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        assertEquals(utcLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")), expectedDate, "Output format for convertMillisToUtcLocalDateTime is incorrect");
    }

    @Test
    public void testConvertLocalDateTimeToMillisOutputFormat() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 7, 8, 12, 34, 56, 789000000);
        long millis = DateUtils.convertLocalDateTimeToMillis(dateTime);
        String expectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        assertEquals(dateTime.atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")), expectedDate, "Output format for convertLocalDateTimeToMillis is incorrect");
    }
}
