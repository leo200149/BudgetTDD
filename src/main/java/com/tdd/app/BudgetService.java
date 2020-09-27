package com.tdd.app;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class BudgetService {

    private final IBudgetRepo repo;

    public BudgetService(IBudgetRepo repo) {
        this.repo = repo;
    }

    public double query(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return 0D;
        }

        // 判斷時間
        if (end.isBefore(start)) {
            return 0D;
        }

        // 建立 Period
        Period period = new Period(start, end);

        // 取得預算列表
        List<Budget> budgetList = repo.getAll();

        // 計算預算總和
        return calculateAmount(period, budgetList);
    }

    private double calculateAmount(Period period, List<Budget> budgetList) {
        Map<String, Integer> overlappingDaysEachYearMonth = period.calculateDaysOfEachMonth();
        // 過濾預算
        AtomicReference<Double> amount = new AtomicReference<>(0D);
        budgetList.stream()
                .filter(budget -> overlappingDaysEachYearMonth.containsKey(budget.yearMonth))
                .forEach(budget -> {
                    Integer overlappingDays = overlappingDaysEachYearMonth.get(budget.yearMonth);
                    int daysOfCurrentYearMonth = new Period(budget.firstDay(), budget.lastDay()).calculateDaysOfCurrentMonth();
                    amount.updateAndGet(totalAmount -> totalAmount + budget.amount * ((double) overlappingDays / daysOfCurrentYearMonth));
                });

        // 加總
        return amount.get();
    }
}
