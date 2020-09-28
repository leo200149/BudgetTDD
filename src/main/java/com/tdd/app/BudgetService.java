package com.tdd.app;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BudgetService {

    private final IBudgetRepo repo;

    public BudgetService(IBudgetRepo repo) {
        this.repo = repo;
    }

    public double query(LocalDate start, LocalDate end) {
        Period period = new Period(start, end);

        List<Budget> budgetList = repo.getAll();

        return calculateAmount(period, budgetList);
    }

    private double calculateAmount(Period period, List<Budget> budgetList) {
        // 過濾預算
        AtomicReference<Double> amount = new AtomicReference<>(0D);
        budgetList.forEach(budget -> {
                    Period budgetPeriod = new Period(budget.firstDay(), budget.lastDay());
                    int overlappingDays = period.overlappingDays(budgetPeriod);
                    int daysOfCurrentYearMonth = budgetPeriod.calculateDaysOfCurrentMonth();
                    amount.updateAndGet(totalAmount -> totalAmount + budget.amount * ((double) overlappingDays / daysOfCurrentYearMonth));
                });

        // 加總
        return amount.get();
    }
}
