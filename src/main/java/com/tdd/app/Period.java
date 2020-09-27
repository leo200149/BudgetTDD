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

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    int calculateDaysOfCurrentMonth() {
        return getEndDate().getDayOfMonth() - getStartDate().getDayOfMonth() + 1;
    }

    Map<String, Integer> calculateDaysOfEachMonth() {
        Map<String, Integer> daysOfEachMonth = new HashMap<>();

        int currentMonthStart = YearMonth.from(getStartDate()).getMonthValue();
        int currentMonthEnd = YearMonth.from(getEndDate()).getMonthValue();
        for (int currentYear = YearMonth.from(getStartDate()).getYear(); currentYear <= YearMonth.from(getEndDate()).getYear(); currentYear++) {
            if (YearMonth.from(getStartDate()).getYear() != YearMonth.from(getEndDate()).getYear()) {
                if (currentYear == YearMonth.from(getStartDate()).getYear()) {
                    currentMonthEnd = 12;
                } else if (currentYear == YearMonth.from(getEndDate()).getYear()) {
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
                        overlappedEndDate = YearMonth.from(getStartDate()).atEndOfMonth();
                        overlappedStartDate = getStartDate();
                    } else if (currentMonth == YearMonth.from(getEndDate()).getMonthValue()) {
                        overlappedEndDate = getEndDate();
                        overlappedStartDate = YearMonth.of(currentYear, currentMonth).atDay(1);
                    }
                } else {
                    overlappedEndDate = getEndDate();
                    overlappedStartDate = getStartDate();
                }
                int daysOfCurrentMonth = new Period(overlappedStartDate, overlappedEndDate).calculateDaysOfCurrentMonth();
                daysOfEachMonth.put(YearMonth.of(currentYear, currentMonth).format(FORMATTER), daysOfCurrentMonth);
            }
        }
        return daysOfEachMonth;
    }
}
