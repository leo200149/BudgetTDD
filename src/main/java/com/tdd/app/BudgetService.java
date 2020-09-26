package com.tdd.app;

import java.util.Date;

public class BudgetService {

    private IBudgetRepo repo;

    public BudgetService(IBudgetRepo repo){
        this.repo = repo;
    }

    public double query(Date start, Date end) {
        return 0;
    }
}
