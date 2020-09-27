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

        int currentMonthStart = startDateMonth;
        int currentMonthEnd = endDateMonth;
        if (startDateYear == endDateYear && startDateMonth == endDateMonth) {
            for (int currentYear = startDateYear; currentYear <= endDateYear; currentYear++) {
                for (int currentMonth = currentMonthStart; currentMonth <= currentMonthEnd; currentMonth++) {
                    LocalDate overlappedEndDate = endDate;
                    LocalDate overlappedStartDate = startDate;
                    int daysOfCurrentMonth = calculateDaysOfCurrentMonth(overlappedEndDate, overlappedStartDate);
                    daysOfEachMonth.put(YearMonth.of(startDateYear, startDateMonth).format(FORMATTER), daysOfCurrentMonth);
                }
            }
        } else {
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
                    LocalDate overlappedEndDate = YearMonth.of(currentYear, currentMonth).atEndOfMonth();
                    LocalDate overlappedStartDate = YearMonth.of(currentYear, currentMonth).atDay(1);
                    if (currentMonth == currentMonthStart) {
                        overlappedEndDate = YearMonth.from(startDate).atEndOfMonth();
                        overlappedStartDate = startDate;
                    } else if (currentMonth == endDateMonth) {
                        overlappedEndDate = endDate;
                        overlappedStartDate = YearMonth.of(currentYear, currentMonth).atDay(1);
                    }
                    int daysOfCurrentMonth = calculateDaysOfCurrentMonth(overlappedEndDate, overlappedStartDate);
                    daysOfEachMonth.put(YearMonth.of(currentYear, currentMonth).format(FORMATTER), daysOfCurrentMonth);
                }
            }
        }
        return daysOfEachMonth;
    }

    private int calculateDaysOfCurrentMonth(LocalDate overlappedEndDate, LocalDate overlappedStartDate) {
        return overlappedEndDate.getDayOfMonth() - overlappedStartDate.getDayOfMonth() + 1;
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
