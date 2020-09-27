package com.tdd.app;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class Budget {

    String yearMonth;
    Integer amount;

    public Budget(String yearMonth, Integer amount) {
        this.yearMonth = yearMonth;
        this.amount = amount;
    }

    public LocalDate firstDay() {
        return LocalDate.parse(yearMonth + "01", DateTimeFormatter.ofPattern("uuuuMM"));
    }

    public LocalDate lastDay() {
        return YearMonth.from(firstDay()).atEndOfMonth();
    }
}
