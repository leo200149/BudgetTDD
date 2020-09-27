package com.tdd.app;

import java.time.YearMonth;

import static java.time.format.DateTimeFormatter.ofPattern;

public class Budget {
    String yearMonth;
    Integer amount;

    public Budget(String yearMonth, Integer amount) {
        this.yearMonth = yearMonth;
        this.amount = amount;
    }

    public double periodAmount(Period period) {
        Integer days = period.getMonthDays().get(this.yearMonth);
        if (days != null) {
            return this.amount * ((double) days / yearMonth().lengthOfMonth());
        }
        return 0;
    }

    private YearMonth yearMonth() {
        return YearMonth.parse(this.yearMonth, ofPattern("yyyyMM"));
    }
}
