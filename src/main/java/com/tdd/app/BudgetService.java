package com.tdd.app;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class BudgetService {

    private IBudgetRepo repo;

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
        Map<String, Integer> targets = calculateDaysOfEachMonth(start, end);

        // 過濾預算
        AtomicReference<Double> amount = new AtomicReference<>(0D);
        budgetList.forEach(budget -> {
            Integer days = targets.get(budget.yearMonth);
            if (days != null) {
                int year= Integer.parseInt(budget.yearMonth.substring(0, 4));
                int month = Integer.parseInt(budget.yearMonth.substring(4));
                YearMonth yearMonth = YearMonth.of(year, month);
                int daysOfMonth = yearMonth.lengthOfMonth();
                amount.updateAndGet(v -> v + budget.amount * ((double) days / daysOfMonth));
            }
        });

        // 加總
        return amount.get();
    }

    private Map<String, Integer> calculateDaysOfEachMonth(LocalDate start, LocalDate end) {
        return new HashMap<>();
    }
}
