package com.tdd.app;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
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
            return 0;
        }

        // 判斷時間
        if (end.isBefore(start)) {
            return 0;
        }
        // 計算各月份日數
        Map<String, Integer> targets = calculateDaysOfEachMonth(start, end);
        // 計算預算總和
        return calculateAmount(targets);
    }

    private Map<String, Integer> calculateDaysOfEachMonth(LocalDate start, LocalDate end) {
        Map<String, Integer> daysOfEachMonth = new HashMap<>();
        long monthCount = ChronoUnit.MONTHS.between(start, end);
        for (int monthAdd = 0; monthAdd <= monthCount; monthAdd++) {
            int month = start.getMonthValue() + monthAdd;
            int realMonth = month % 12 == 0 ? 12 : month % 12;
            int realYear = start.getYear() + (month - 1) / 12;
            int daysOfCurrentMonth = MonthDays(realYear, realMonth);
            if (monthCount == 0) {
                // 同年同月
                daysOfCurrentMonth = end.getDayOfMonth() - start.getDayOfMonth() + 1;
            } else if (monthAdd == 0) {
                // 第一個月
                daysOfCurrentMonth = LastDays(start);
            } else if (monthAdd == monthCount) {
                // 最後一個月
                daysOfCurrentMonth = end.getDayOfMonth();
            }
            daysOfEachMonth.put(YearMonthString(realYear, realMonth), daysOfCurrentMonth);
        }
        return daysOfEachMonth;
    }

    private int MonthDays(int year, int month) {
        return YearMonth.of(year, month).lengthOfMonth();
    }

    private int LastDays(LocalDate date) {
        return YearMonth.from(date).lengthOfMonth() - date.getDayOfMonth() + 1;
    }

    private String YearMonthString(int yearIndex, int monthIndex) {
        return String.format("%04d", yearIndex) + String.format("%02d", monthIndex);
    }

    private double calculateAmount(Map<String, Integer> targets) {
        // 取得預算列表
        List<Budget> budgetList = repo.getAll();
        // 過濾預算
        AtomicReference<Double> amount = new AtomicReference<>((double) 0D);
        budgetList.forEach(budget -> {
            Integer days = targets.get(budget.yearMonth);
            if (days != null) {
                YearMonth yearMonth = YearMonth.parse(budget.yearMonth, DateTimeFormatter.ofPattern("yyyyMM"));
                amount.updateAndGet(v -> v + budget.amount * ((double) days / yearMonth.lengthOfMonth()));
            }
        });
        // 加總
        return amount.get();
    }
}
