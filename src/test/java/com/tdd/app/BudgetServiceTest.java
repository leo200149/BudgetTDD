package com.tdd.app;

import junit.framework.TestCase;
import org.mockito.Mock;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

public class BudgetServiceTest extends TestCase {


    @Mock
    private IBudgetRepo repo;

    private BudgetService budgetService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        repo = new BudgetRepo();
        budgetService = new BudgetService(repo);
    }

    @Test
    public void no_budget() {

        LocalDate startDate = LocalDate.of(2020, 2, 10);
        LocalDate endDate = LocalDate.of(2020, 3, 10);
        double budget = budgetService.query(startDate, endDate);


        Assert.assertEquals(0, budget, 0.0);
    }

    @Test
    public void test_start_date_later_then_end_date() {
        LocalDate endDate = LocalDate.of(2020, 2, 10);
        LocalDate startDate = LocalDate.of(2020, 3, 10);
        double budget = budgetService.query(startDate, endDate);

        Assert.assertEquals(0, budget, 0.0);
    }

    @Test
    public void test_single_day_selected() {

        LocalDate startDate = LocalDate.of(2020, 3, 1);
        LocalDate endDate = LocalDate.of(2020, 3, 1);
        double budget = budgetService.query(startDate, endDate);


        Assert.assertEquals(10, budget, 0.0);
    }

    @Test
    public void search_test_n_month() {
        LocalDate startDate = LocalDate.of(2020, 2, 1);
        LocalDate endDate = LocalDate.of(2020, 3, 31);
        double budget = budgetService.query(startDate, endDate);


        Assert.assertEquals(3110, budget, 0.0);
    }

    @Test
    public void search_test_cross_month_with_no_badget() {
        LocalDate startDate = LocalDate.of(2020, 2, 1);
        LocalDate endDate = LocalDate.of(2020, 3, 31);
        double budget = budgetService.query(startDate, endDate);


        Assert.assertEquals(100, budget, 0.0);
    }

    @Test
    public void search_test_cross_month() {
        LocalDate startDate = LocalDate.of(2020, 2, 10);
        LocalDate endDate = LocalDate.of(2020, 3, 10);
        double budget = budgetService.query(startDate, endDate);


        Assert.assertEquals(1200, budget, 0.0);
    }

    @Test
    public void search_test_cross_multi_month() {
        LocalDate startDate = LocalDate.of(2020, 2, 10);
        LocalDate endDate = LocalDate.of(2020, 4, 10);
        double budget = budgetService.query(startDate, endDate);


        Assert.assertEquals(1330, budget, 0.0);
    }
}