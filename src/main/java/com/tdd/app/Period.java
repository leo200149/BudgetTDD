package com.tdd.app;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Period {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuuMM");

    private final LocalDate endDate;
    private final LocalDate startDate;

    public Period(LocalDate startDate, LocalDate endDate) {
        this.endDate = endDate;
        this.startDate = startDate;
    }

    Map<String, Integer> calculateDaysOfEachMonth() {
        Map<String, Integer> daysOfEachMonth = new HashMap<>();

        int currentMonthStart = YearMonth.from(startDate).getMonthValue();
        int currentMonthEnd = YearMonth.from(endDate).getMonthValue();
        for (int currentYear = YearMonth.from(startDate).getYear(); currentYear <= YearMonth.from(endDate).getYear(); currentYear++) {
            if (YearMonth.from(startDate).getYear() != YearMonth.from(endDate).getYear()) {
                if (currentYear == YearMonth.from(startDate).getYear()) {
                    currentMonthEnd = 12;
                } else if (currentYear == YearMonth.from(endDate).getYear()) {
                    currentMonthStart = 1;
                } else {
                    currentMonthStart = 1;
                    currentMonthEnd = 12;
                }
            }
            for (int currentMonth = currentMonthStart; currentMonth <= currentMonthEnd; currentMonth++) {
                LocalDate overlappedEndDate = YearMonth.of(currentYear, currentMonth).atEndOfMonth();
                LocalDate overlappedStartDate = YearMonth.of(currentYear, currentMonth).atDay(1);
                if (currentMonthStart != currentMonthEnd) {
                    if (currentMonth == currentMonthStart) {
                        overlappedEndDate = YearMonth.from(startDate).atEndOfMonth();
                        overlappedStartDate = startDate;
                    } else if (currentMonth == YearMonth.from(endDate).getMonthValue()) {
                        overlappedEndDate = endDate;
                        overlappedStartDate = YearMonth.of(currentYear, currentMonth).atDay(1);
                    }
                } else {
                    overlappedEndDate = endDate;
                    overlappedStartDate = startDate;
                }
                int daysOfCurrentMonth = new Period(overlappedStartDate, overlappedEndDate).calculateDaysOfCurrentMonth();
                daysOfEachMonth.put(YearMonth.of(currentYear, currentMonth).format(FORMATTER), daysOfCurrentMonth);
            }
        }
        return daysOfEachMonth;
    }

    private int calculateDaysOfCurrentMonth() {
        return endDate.getDayOfMonth() - startDate.getDayOfMonth() + 1;
    }
}
