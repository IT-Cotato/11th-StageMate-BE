package com.example.stagemate.global.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class DateFormat {

    private DateFormat() {}

    private static final DateTimeFormatter TIME_ONLY  = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_TIME  = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    private static final DateTimeFormatter DATE_ONLY  = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    // 오늘 작성: HH:mm
    // 오늘 아님(과거): yyyy/MM/dd HH:mm
    public static String formatTimeIfTodayElseDateTime(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime, "dateTime");
        LocalDate target = dateTime.toLocalDate();
        LocalDate today  = LocalDate.now(ZoneId.of("Asia/Seoul"));

        if (target.isEqual(today)) {
            return dateTime.format(TIME_ONLY);
        } else {
            return dateTime.format(DATE_TIME);
        }
    }

    // 오늘 작성: HH:mm
    // 오늘 아님(과거): yyyy/MM/dd
    // 날짜만 표기(yyyy/MM/dd)
    public static String formatTimeIfTodayElseDate(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime, "dateTime");
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        if (dateTime.toLocalDate().isEqual(today)) {
            return dateTime.format(TIME_ONLY);
        } else {
            return dateTime.format(DATE_ONLY);
        }
    }


    // 날짜만 표기(yyyy/MM/dd)
    public static String formateOnlyDate(LocalDate dateTime) {
        Objects.requireNonNull(dateTime, "dateTime");
        return dateTime.format(DATE_ONLY);
    }


}

