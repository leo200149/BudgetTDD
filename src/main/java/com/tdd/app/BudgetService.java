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

        YearMonth startDateYearMonth = YearMonth.from(startDate);
        YearMonth endDateYearMonth = YearMonth.from(endDate);

        int startDateYear = startDateYearMonth.getYear();
        int endDateYear = endDateYearMonth.getYear();
        int startDateMonth = startDateYearMonth.getMonthValue();
        int endDateMonth = endDateYearMonth.getMonthValue();

        if (startDateYear == endDateYear && startDateMonth == endDateMonth) {
            int daysOfCurrentMonth = endDate.getDayOfMonth() - startDate.getDayOfMonth() + 1;
            daysOfEachMonth.put(YearMonth.of(startDateYear, startDateMonth).format(FORMATTER), daysOfCurrentMonth);
        } else {
            int currentMonthStart = startDateMonth;
            int currentMonthEnd = endDateMonth;
            for (int currentYear = startDateYear; currentYear <= endDateYear; currentYear++) {
                if (startDateYear != endDateYear) {
                    if (currentYear == startDateYear) {
                        currentMonthEnd = 12;
                    } else if (currentYear == endDateYear) {
                        currentMonthStart = 1;
                    } else {
                        currentMonthStart = 1;
                        currentMonthEnd = 12;
                    }
                }
                for (int currentMonth = currentMonthStart; currentMonth <= currentMonthEnd; currentMonth++) {
                    int daysOfCurrentMonth = YearMonth.of(currentYear, currentMonth).lengthOfMonth();
                    if (currentMonth == currentMonthStart) {
                        daysOfCurrentMonth = YearMonth.from(startDate).lengthOfMonth() - startDate.getDayOfMonth() + 1;
                    } else if (currentMonth == endDateMonth) {
                        daysOfCurrentMonth = endDate.getDayOfMonth();
                    }
                    daysOfEachMonth.put(YearMonth.of(currentYear, currentMonth).format(FORMATTER), daysOfCurrentMonth);
                }
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
