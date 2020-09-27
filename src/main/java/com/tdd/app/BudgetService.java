package com.tdd.app;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class BudgetService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuuMM");

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

        // 取得預算列表
        List<Budget> budgetList = repo.getAll();

        // 計算各月份日數
        Map<String, Integer> targets = new Period(start, end).calculateDaysOfEachMonth();

        // 計算預算總和
        return calculateAmount(targets, budgetList);
    }

    private double calculateAmount(Map<String, Integer> overlappingDaysEachYearMonth, List<Budget> budgetList) {
        // 過濾預算
        AtomicReference<Double> amount = new AtomicReference<>(0D);
        budgetList.stream()
                .filter(budget -> overlappingDaysEachYearMonth.containsKey(budget.yearMonth))
                .forEach(budget -> {
                    Integer overlappingDays = overlappingDaysEachYearMonth.get(budget.yearMonth);
                    int daysOfCurrentYearMonth = YearMonth.parse(budget.yearMonth, FORMATTER).lengthOfMonth();
                    amount.updateAndGet(totalAmount -> totalAmount + budget.amount * ((double) overlappingDays / daysOfCurrentYearMonth));
                });

        // 加總
        return amount.get();
    }
}
