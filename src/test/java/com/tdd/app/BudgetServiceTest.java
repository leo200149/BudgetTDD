package com.tdd.app;

import junit.framework.TestCase;
import org.mockito.Mock;
import org.junit.Assert;
import org.junit.Test;

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
    public void single_month_selected() {
        IBudgetRepo repo = new BudgetRepo();

        BudgetService service = new BudgetService(repo);

        List<Budget> budgetList = service.query(Calendar );


        Assert.assertTrue();

    }

}