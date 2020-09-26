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
        System.out.println(targets);

        // 過濾預算
        AtomicReference<Double> amount = new AtomicReference<>(0D);
        budgetList.forEach(budget -> {
            Integer days = targets.get(budget.yearMonth);
            if (days != null) {
                int year= Integer.parseInt(budget.yearMonth.substring(0, 4));
                System.out.println("year" + year);
                int month = Integer.parseInt(budget.yearMonth.substring(4));
                System.out.println("month" + month);
                YearMonth yearMonth = YearMonth.of(year, month);
                int daysOfMonth = yearMonth.lengthOfMonth();
                amount.updateAndGet(v -> v + budget.amount * ((double) days / daysOfMonth));
            }
        });

        // 加總
        return amount.get();
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
                    if (monthIndex == startMonth) {
                        int daysOfCurrentMonth = YearMonth.from(start).lengthOfMonth() - start.getDayOfMonth() + 1;
                        daysOfEachMonth.put(currentYearMonth, daysOfCurrentMonth);
                    } else if (monthIndex == endMonth) {
                        int daysOfCurrentMonth = end.getDayOfMonth();
                        daysOfEachMonth.put(currentYearMonth, daysOfCurrentMonth);
                    } else {
                        daysOfEachMonth.put(currentYearMonth, YearMonth.of(startYear, monthIndex).lengthOfMonth());
                    }
                }
            } else {
                for (int yearIndex = startYear; yearIndex <= endYear; yearIndex++) {
                    if (yearIndex == startYear) {
                        for (int monthIndex = startMonth; monthIndex <= 12; monthIndex++) {
                            String currentYearMonth = String.format("%04d", yearIndex) + String.format("%02d", monthIndex);
                            if (monthIndex == startMonth) {
                                int daysOfCurrentMonth = YearMonth.from(start).lengthOfMonth() - start.getDayOfMonth() + 1;
                                daysOfEachMonth.put(currentYearMonth, daysOfCurrentMonth);
                            } else {
                                daysOfEachMonth.put(currentYearMonth, YearMonth.of(yearIndex, monthIndex).lengthOfMonth());
                            }
                        }
                    } else if (yearIndex == endYear) {
                        for (int monthIndex = 1; monthIndex <= endMonth; monthIndex++) {
                            String currentYearMonth = String.format("%04d", yearIndex) + String.format("%02d", monthIndex);
                            if (monthIndex == endMonth) {
                                int daysOfCurrentMonth = end.getDayOfMonth();
                                daysOfEachMonth.put(currentYearMonth, daysOfCurrentMonth);
                            } else {
                                daysOfEachMonth.put(currentYearMonth, YearMonth.of(yearIndex, monthIndex).lengthOfMonth());
                            }
                        }
                    } else {
                        for (int monthIndex = 1; monthIndex <= endMonth; monthIndex++) {
                            String currentYearMonth = String.format("%04d", yearIndex) + String.format("%02d", monthIndex);
                            if (monthIndex == endMonth) {
                                int daysOfCurrentMonth = end.getDayOfMonth();
                                daysOfEachMonth.put(currentYearMonth, daysOfCurrentMonth);
                            } else {
                                daysOfEachMonth.put(currentYearMonth, YearMonth.of(yearIndex, monthIndex).lengthOfMonth());
                            }
                        }
                    }
                }
            }

        }


        return daysOfEachMonth;
    }
}
