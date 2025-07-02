package com.metical_converter.infrasctruture.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {


    public static String formatDate(String rawDate, DateTimeFormatter inputFormatter, String outputPattern) {
        try {
            LocalDate date = LocalDate.parse(rawDate, inputFormatter);
            return date.format(DateTimeFormatter.ofPattern(outputPattern));
        } catch (Exception e) {
            return rawDate;
        }
    }

    public static String formatDateTime(String rawDateTime, DateTimeFormatter inputFormatter, String outputPattern) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(rawDateTime, inputFormatter);
            return dateTime.format(DateTimeFormatter.ofPattern(outputPattern));
        } catch (Exception e) {
            return rawDateTime;
        }
    }


}
