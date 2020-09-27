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

    private Map<String, Integer> calculateDaysOfEachMonth(LocalDate start, LocalDate end) {
        Map<String, Integer> daysOfEachMonth =new HashMap<>();

        YearMonth startYearMonth = YearMonth.from(start);
        YearMonth endYearMonth = YearMonth.from(end);

        int startYear = startYearMonth.getYear();
        int endYear = endYearMonth.getYear();
        int startMonth = startYearMonth.getMonthValue();
        int endMonth = endYearMonth.getMonthValue();

        if (startYear == endYear && startMonth == endMonth) {
            String currentYearMonth = String.format("%04d", startYear) + String.format("%02d", startMonth);
            int daysOfCurrentMonth = end.getDayOfMonth() - start.getDayOfMonth() + 1;
            daysOfEachMonth.put(currentYearMonth, daysOfCurrentMonth);
        } else {
            if (startYear == endYear) {
                for (int monthIndex = startMonth; monthIndex <= endMonth; monthIndex++) {
                    String currentYearMonth = String.format("%04d", startYear) + String.format("%02d", monthIndex);
                    int daysOfCurrentMonth = YearMonth.of(startYear, monthIndex).lengthOfMonth();
                    if (monthIndex == startMonth) {
                        daysOfCurrentMonth = YearMonth.from(start).lengthOfMonth() - start.getDayOfMonth() + 1;
                    } else if (monthIndex == endMonth) {
                        daysOfCurrentMonth = end.getDayOfMonth();
                    }
                    daysOfEachMonth.put(currentYearMonth, daysOfCurrentMonth);
                }
            } else {
                for (int yearIndex = startYear; yearIndex <= endYear; yearIndex++) {
                    if (yearIndex == startYear) {
                        for (int monthIndex = startMonth; monthIndex <= 12; monthIndex++) {
                            String currentYearMonth = String.format("%04d", yearIndex) + String.format("%02d", monthIndex);
                            int daysOfCurrentMonth = YearMonth.of(yearIndex, monthIndex).lengthOfMonth();
                            if (monthIndex == startMonth) {
                                daysOfCurrentMonth = YearMonth.from(start).lengthOfMonth() - start.getDayOfMonth() + 1;
                            }
                            daysOfEachMonth.put(currentYearMonth, daysOfCurrentMonth);
                        }
                    } else if (yearIndex == endYear) {
                        for (int monthIndex = 1; monthIndex <= endMonth; monthIndex++) {
                            String currentYearMonth = String.format("%04d", yearIndex) + String.format("%02d", monthIndex);
                            int daysOfCurrentMonth = YearMonth.of(yearIndex, monthIndex).lengthOfMonth();
                            if (monthIndex == endMonth) {
                                daysOfCurrentMonth = end.getDayOfMonth();
                            }
                            daysOfEachMonth.put(currentYearMonth, daysOfCurrentMonth);
                        }
                    } else {
                        for (int monthIndex = 1; monthIndex <= endMonth; monthIndex++) {
                            String currentYearMonth = String.format("%04d", yearIndex) + String.format("%02d", monthIndex);
                            int daysOfCurrentMonth = YearMonth.of(yearIndex, monthIndex).lengthOfMonth();
                            if (monthIndex == endMonth) {
                                daysOfCurrentMonth = end.getDayOfMonth();
                            }
                            daysOfEachMonth.put(currentYearMonth, daysOfCurrentMonth);
                        }
                    }
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
