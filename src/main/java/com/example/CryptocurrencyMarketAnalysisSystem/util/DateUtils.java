package com.example.CryptocurrencyMarketAnalysisSystem.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final long MIN_MILLIS = -31557014167219200L; // Approx equivalent to Instant.MIN
    private static final long MAX_MILLIS = 31556889864403199L; // Approx equivalent to Instant.MAX

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public static Long convertDateToMillis(String date, ZoneId zoneId) {
        LocalDateTime localDateTime = LocalDateTime.parse(date, DATE_TIME_FORMATTER);
        return localDateTime.atZone(zoneId).toInstant().toEpochMilli();
    }

    public static LocalDateTime convertMillisToLocalDateTime(Long millis) {
        millisNotNullAndInRange(millis);
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }

    public static String convertMillisToDate(long millis) {
        LocalDateTime dateTime = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
    }

    public static Long convertToTimestamp(String date, String time) {
        if (date == null || date.isEmpty()) {
            throw new IllegalArgumentException("Date must not be null or empty");
        }

        LocalDate localDate = LocalDate.parse(date);
        LocalTime localTime = (time == null || time.isEmpty()) ? LocalTime.MIDNIGHT : LocalTime.parse(time);

        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

        ZoneId zoneId = ZoneId.systemDefault();
        return localDateTime.atZone(zoneId).toInstant().toEpochMilli();
    }

    public static LocalDateTime convertMillisToUtcLocalDateTime(long millis) {
        millisNotNullAndInRange(millis);
        return Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDateTime();
    }

    public static ZonedDateTime convertMillisToUtcZonedDateTime(long millis) {
        millisNotNullAndInRange(millis);
        return Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC"));
    }

    public static Long convertLocalDateTimeToMillis(LocalDateTime localDateTime) {
        return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public static Long convertZonedDateTimeToMillis(ZonedDateTime zonedDateTime){
        return zonedDateTime.toInstant().toEpochMilli();
    }

    private static void millisNotNullAndInRange(Long millis) {
        if (millis == null) {
            throw new IllegalArgumentException("Millis must not be null");
        }
        if (millis < MIN_MILLIS || millis > MAX_MILLIS) {
            throw new DateTimeException("Millis value is out of range");
        }
    }

    public static boolean isMillisInRange(long millis) {
        try {
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
            return false;
        } catch (DateTimeException e) {
            return true;
        }
    }
}