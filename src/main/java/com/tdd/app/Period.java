package com.tdd.app;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class Period {
    private final LocalDate start;
    private final LocalDate end;
    private Map<String, Integer> monthDays;


    public Period(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
        this.monthDays = monthDays();
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public Map<String, Integer> getMonthDays() {
        return monthDays;
    }

    Map<String, Integer> monthDays() {
        Map<String, Integer> daysOfEachMonth = new HashMap<>();
        long monthCount = ChronoUnit.MONTHS.between(getStart(), getEnd());
        for (int monthAdd = 0; monthAdd <= monthCount; monthAdd++) {
            int month = getStart().getMonthValue() + monthAdd;
            int realMonth = num2Month(month);
            int realYear = getStart().getYear() + (month - 1) / 12;
            YearMonth yearMonth = YearMonth.of(realYear, realMonth);
            int daysOfCurrentMonth = yearMonth.lengthOfMonth();
            if (monthCount == 0) {
                // 同年同月
                daysOfCurrentMonth = getEnd().getDayOfMonth() - getStart().getDayOfMonth() + 1;
            } else if (monthAdd == 0) {
                // 第一個月
                daysOfCurrentMonth = YearMonth.from(getStart()).lengthOfMonth() - getStart().getDayOfMonth() + 1;
            } else if (monthAdd == monthCount) {
                // 最後一個月
                daysOfCurrentMonth = getEnd().getDayOfMonth();
            }
            daysOfEachMonth.put(yearMonth.format(DateTimeFormatter.ofPattern("yyyyMM")), daysOfCurrentMonth);
        }
        return daysOfEachMonth;
    }

    private int num2Month(int month) {
        return month % 12 == 0 ? 12 : month % 12;
    }
}
