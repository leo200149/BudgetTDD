package com.tdd.app;

import junit.framework.TestCase;
import org.mockito.Mock;

public class BudgetServiceTest extends TestCase {


    @Mock
    private IBudgetRepo repo;

    private Budget budgetService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        budgetService = new BudgetService(repo);
    }




}