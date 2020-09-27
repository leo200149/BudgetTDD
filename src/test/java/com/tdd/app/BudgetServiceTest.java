package com.tdd.app;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Arrays;

@RunWith(JUnit4.class)
public class BudgetServiceTest extends TestCase {

    private final IBudgetRepo repo = Mockito.mock(IBudgetRepo.class);
    private final BudgetService budgetService = new BudgetService(repo);

    @Test
    public void no_budget() {
        mockBudgets();

        double budget = budgetService.query(
                LocalDate.of(2020, 2, 10),
                LocalDate.of(2020, 3, 10));

        Assert.assertEquals(0, budget, 0.0);
    }

    @Test
    public void test_start_date_later_then_end_date() {
        mockBudgets(
                new Budget("202002", 290),
                new Budget("202003", 310));

        double budget = budgetService.query(
                LocalDate.of(2020, 3, 10),
                LocalDate.of(2020, 2, 10));

        Assert.assertEquals(0, budget, 0.0);
    }

    @Test
    public void test_single_day_selected() {
        mockBudgets(
                new Budget("202002", 290),
                new Budget("202003", 310));

        double budget = budgetService.query(
                LocalDate.of(2020, 3, 1),
                LocalDate.of(2020, 3, 1));

        Assert.assertEquals(10, budget, 0.0);
    }

    @Test
    public void search_test_n_month() {
        mockBudgets(
                new Budget("202002", 2800),
                new Budget("202003", 310));

        double budget = budgetService.query(
                LocalDate.of(2020, 2, 1),
                LocalDate.of(2020, 3, 31));

        Assert.assertEquals(3110, budget, 0.0);
    }

    @Test
    public void search_test_cross_month_with_no_badget() {
        mockBudgets(
                new Budget("202002", 0),
                new Budget("202003", 310));

        double budget = budgetService.query(
                LocalDate.of(2020, 2, 1),
                LocalDate.of(2020, 3, 31));

        Assert.assertEquals(310, budget, 0.0);
    }

    @Test
    public void search_test_cross_month() {
        mockBudgets(
                new Budget("202002", 290),
                new Budget("202003", 3100));

        double budget = budgetService.query(
                LocalDate.of(2020, 2, 10),
                LocalDate.of(2020, 3, 10));

        Assert.assertEquals(1200, budget, 0.0);
    }

    @Test
    public void search_test_cross_multi_month() {
        mockBudgets(
                new Budget("202002", 29),
                new Budget("202003", 310),
                new Budget("202004", 3000));

        double budget = budgetService.query(
                LocalDate.of(2020, 2, 10),
                LocalDate.of(2020, 4, 10));

        Assert.assertEquals(1330, budget, 0.0);
    }

    @Test
    public void search_test_cross_multi_year() {
        mockBudgets(
                new Budget("202002", 29),
                new Budget("202003", 310),
                new Budget("202004", 3000));

        double budget = budgetService.query(
                LocalDate.of(2019, 12, 10),
                LocalDate.of(2020, 4, 10));

        Assert.assertEquals(1339, budget, 0.0);
    }

    private void mockBudgets(Budget... budgets) {
        Mockito.when(repo.getAll()).thenReturn(Arrays.asList(budgets));
    }
}