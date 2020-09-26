package com.tdd.app;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class BudgetServiceTest extends TestCase {

    @Test
    public void single_month_selected() {
        IBudgetRepo repo = new BudgetRepo();

        BudgetService service = new BudgetService(repo);

        List<Budget> budgetList = service.query(Calendar );


        Assert.assertTrue();

    }

}