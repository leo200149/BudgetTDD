package com.tdd.app;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class BudgetServiceTest extends TestCase {

    @Mock
    private final List<Budget> array = new ArrayList<>();

    private final BudgetService budgetService = new BudgetService(() -> array);

    @Test
    public void test_no_budget() {
        LocalDate startDate = LocalDate.of(2020, 2, 10);
        LocalDate endDate = LocalDate.of(2020, 3, 10);
        double budget = budgetService.query(startDate, endDate);
        Assert.assertEquals(0, budget, 0.0);
    }

    @Test
    public void test_start_date_later_then_end_date() {
        array.clear();
        array.add(new Budget("202002", 290));
        array.add(new Budget("202003", 310));

        LocalDate endDate = LocalDate.of(2020, 2, 10);
        LocalDate startDate = LocalDate.of(2020, 3, 10);

        double budget = budgetService.query(startDate, endDate);

        Assert.assertEquals(0, budget, 0.0);
    }

    @Test
    public void test_query_single_day() {
        array.clear();
        array.add(new Budget("202003", 310));

        LocalDate startDate = LocalDate.of(2020, 3, 1);
        LocalDate endDate = LocalDate.of(2020, 3, 1);

        double budget = budgetService.query(startDate, endDate);

        Assert.assertEquals(10, budget, 0.0);
    }

    @Test
    public void test_query_multiple_full_month() {
        array.clear();
        array.add(new Budget("202002", 2800));
        array.add(new Budget("202003", 310));

        LocalDate startDate = LocalDate.of(2020, 2, 1);
        LocalDate endDate = LocalDate.of(2020, 3, 31);

        double budget = budgetService.query(startDate, endDate);

        Assert.assertEquals(3110, budget, 0.0);
    }

    @Test
    public void test_query_cross_month_with_no_budget() {
        array.clear();
        array.add(new Budget("202002", 0));
        array.add(new Budget("202003", 310));

        LocalDate startDate = LocalDate.of(2020, 2, 1);
        LocalDate endDate = LocalDate.of(2020, 3, 31);

        double budget = budgetService.query(startDate, endDate);

        Assert.assertEquals(310, budget, 0.0);
    }

    @Test
    public void test_query_cross_month() {
        array.clear();
        array.add(new Budget("202002", 290));
        array.add(new Budget("202003", 3100));

        LocalDate startDate = LocalDate.of(2020, 2, 10);
        LocalDate endDate = LocalDate.of(2020, 3, 10);

        double budget = budgetService.query(startDate, endDate);

        Assert.assertEquals(1200, budget, 0.0);
    }

    @Test
    public void test_query_cross_multiple_month() {
        array.clear();
        array.add(new Budget("202002", 29));
        array.add(new Budget("202003", 310));
        array.add(new Budget("202004", 3000));

        LocalDate startDate = LocalDate.of(2020, 2, 10);
        LocalDate endDate = LocalDate.of(2020, 4, 10);

        double budget = budgetService.query(startDate, endDate);

        Assert.assertEquals(1330, budget, 0.0);
    }

    @Test
    public void test_query_cross_multiple_year() {
        array.clear();
        array.add(new Budget("202002", 29));
        array.add(new Budget("202003", 310));
        array.add(new Budget("202004", 3000));

        LocalDate startDate = LocalDate.of(2019, 12, 10);
        LocalDate endDate = LocalDate.of(2020, 4, 10);

        double budget = budgetService.query(startDate, endDate);

        Assert.assertEquals(1339, budget, 0.0);
    }
}