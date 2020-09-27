package com.tdd.app;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BudgetService {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuuMM");

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
        if (budgetList.isEmpty()) {
            return 0D;
        }

        // 計算各月份預算
        List<MonthlyBudget> monthlyBudgets = generateMonthlyBudget(start, end, budgetList);

        // 計算預算總和
        return monthlyBudgets.stream().mapToDouble(MonthlyBudget::getBudget).sum();
    }

    private List<MonthlyBudget> generateMonthlyBudget(LocalDate start, LocalDate end, List<Budget> budgetList) {
        YearMonth startingYearMonth = YearMonth.from(start);
        YearMonth endingYearMonth = YearMonth.from(end);

        YearMonth currentYearMonth = YearMonth.from(start);
        List<MonthlyBudget> monthlyBudgets = new ArrayList<>();
        while (!currentYearMonth.isAfter(endingYearMonth)) {
            String currentYearMonthString = currentYearMonth.format(dtf);
            MonthlyBudget monthlyBudget = new MonthlyBudget();
            budgetList.stream()
                    .filter(budget -> budget.yearMonth.equals(currentYearMonthString))
                    .findFirst()
                    .ifPresentOrElse(
                            budget -> monthlyBudget.monthlyBudget = budget.amount,
                            () -> monthlyBudget.monthlyBudget = 0);
            if (currentYearMonth.equals(startingYearMonth)) {
                monthlyBudget.startDate = start;
            } else {
                monthlyBudget.startDate = currentYearMonth.atDay(1);
            }
            if (currentYearMonth.equals(endingYearMonth)) {
                monthlyBudget.endDate = end;
            } else {
                monthlyBudget.endDate = currentYearMonth.atEndOfMonth();
            }
            monthlyBudgets.add(monthlyBudget);
            currentYearMonth = currentYearMonth.plusMonths(1);
        }
        return monthlyBudgets;
    }

    private static class MonthlyBudget {
        LocalDate startDate;
        LocalDate endDate;
        int monthlyBudget;
        private double getBudget() {
            int daysOfMonth = YearMonth.from(startDate).lengthOfMonth();
            return monthlyBudget * ((double) (endDate.getDayOfMonth() - startDate.getDayOfMonth() + 1) / daysOfMonth);
        }
    }
}
