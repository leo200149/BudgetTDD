package com.tdd.app;

import java.time.LocalDate;
import java.util.Date;

public class BudgetService {

    private IBudgetRepo repo;

    public BudgetService(IBudgetRepo repo){
        this.repo = repo;
    }

    public double query(LocalDate start, LocalDate end) {
        return 0;
    }
}
