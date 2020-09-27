package com.tdd.app;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Period {

    private final LocalDate endDate;
    private final LocalDate startDate;

    public Period(LocalDate startDate, LocalDate endDate) {
        this.endDate = endDate;
        this.startDate = startDate;
    }

    public int overlappingDays(Period another) {
        LocalDate overlappingStart = startDate.isAfter(another.startDate) ? startDate : another.startDate;
        LocalDate overlappingEnd = endDate.isBefore(another.endDate) ? endDate : another.endDate;
        return (int) (ChronoUnit.DAYS.between(overlappingStart, overlappingEnd) + 1);
    }

    public int calculateDaysOfCurrentMonth() {
        return endDate.getDayOfMonth() - startDate.getDayOfMonth() + 1;
    }
}
