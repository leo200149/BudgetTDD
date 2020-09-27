package com.tdd.app;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class BudgetService {

    private final IBudgetRepo repo;

    public BudgetService(IBudgetRepo repo) {
        this.repo = repo;
    }

    public double query(LocalDate start, LocalDate end) {
        if (isInvalid(start, end)) {
            return 0;
        }
        Period period = new Period(start, end);
        return repo.getAll()
                .stream()
                .mapToDouble(budget -> budget.periodAmount(period))
                .sum();
    }

    private boolean isInvalid(LocalDate start, LocalDate end) {
        return start == null || end == null || end.isBefore(start);
    }

}
