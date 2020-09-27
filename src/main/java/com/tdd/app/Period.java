package com.tdd.app;

import java.time.LocalDate;

public class Period {
    private final LocalDate endDate;
    private final LocalDate startDate;

    public Period(LocalDate endDate, LocalDate startDate) {
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
}
