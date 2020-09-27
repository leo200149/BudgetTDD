package com.tdd.app;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
        Map<String, Integer> targets = calculateDaysOfEachMonth(start, end);

        // 計算預算總和
        return calculateAmount(targets, budgetList);
    }

    private Map<String, Integer> calculateDaysOfEachMonth(LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> daysOfEachMonth = new HashMap<>();

        int currentMonthStart = YearMonth.from(startDate).getMonthValue();
        int currentMonthEnd = YearMonth.from(endDate).getMonthValue();
        for (int currentYear = YearMonth.from(startDate).getYear(); currentYear <= YearMonth.from(endDate).getYear(); currentYear++) {
            if (YearMonth.from(startDate).getYear() != YearMonth.from(endDate).getYear()) {
                if (currentYear == YearMonth.from(startDate).getYear()) {
                    currentMonthEnd = 12;
                } else if (currentYear == YearMonth.from(endDate).getYear()) {
                    currentMonthStart = 1;
                } else {
                    currentMonthStart = 1;
                    currentMonthEnd = 12;
                }
            }
            for (int currentMonth = currentMonthStart; currentMonth <= currentMonthEnd; currentMonth++) {
                LocalDate overlappedEndDate = YearMonth.of(currentYear, currentMonth).atEndOfMonth();
                LocalDate overlappedStartDate = YearMonth.of(currentYear, currentMonth).atDay(1);
                if (currentMonthStart != currentMonthEnd) {
                    if (currentMonth == currentMonthStart) {
                        overlappedEndDate = YearMonth.from(startDate).atEndOfMonth();
                        overlappedStartDate = startDate;
                    } else if (currentMonth == YearMonth.from(endDate).getMonthValue()) {
                        overlappedEndDate = endDate;
                        overlappedStartDate = YearMonth.of(currentYear, currentMonth).atDay(1);
                    }
                } else {
                    overlappedEndDate = endDate;
                    overlappedStartDate = startDate;
                }
                int daysOfCurrentMonth = new Period(overlappedEndDate, overlappedStartDate).calculateDaysOfCurrentMonth();
                daysOfEachMonth.put(YearMonth.of(currentYear, currentMonth).format(FORMATTER), daysOfCurrentMonth);
            }
        }
        return daysOfEachMonth;
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
