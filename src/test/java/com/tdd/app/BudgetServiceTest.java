package com.tdd.app;

import junit.framework.TestCase;
import org.mockito.Mock;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

public class BudgetServiceTest extends TestCase {


    @Mock
    private IBudgetRepo repo;

    private BudgetService budgetService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        budgetService = new BudgetService(repo);
    }

    @Test
    public void no_budget() {

        LocalDate startDate = LocalDate.of(2020, 2, 10);
        LocalDate endDate = LocalDate.of(2020, 3, 10);
        double budget = budgetService.query(startDate, endDate);


        Assert.assertTrue(budget == 0);
    }

    @Test
    public void test_start_date_later_then_end_date() {
        LocalDate endDate = LocalDate.of(2020, 2, 10);
        LocalDate startDate = LocalDate.of(2020, 3, 10);
        double budget = budgetService.query(startDate, endDate);

        Assert.assertTrue(budget == 0);
    }

    @Test
    public void single_day_selected() {

        LocalDate startDate = LocalDate.of(2020, 3, 1);
        LocalDate endDate = LocalDate.of(2020, 3, 1);
        double budget = budgetService.query(startDate, endDate);


        Assert.assertTrue(budget == 10);
    }
}