package com.example.finance.tracker.budget.dto;

import com.example.finance.tracker.budget.Budget;

import java.math.BigDecimal;
import java.time.YearMonth;


// remaining = limit - spent, minus when spending over limit
 //exceeded  = spent > limit

public record BudgetStatus(Long budgetId,
                           Long categoryId,
                           String categoryName,
                           YearMonth month,
                           BigDecimal limit,
                           BigDecimal spent,
                           BigDecimal remaining,
                           boolean exceeded) {


    public static BudgetStatus of(Budget budget, BigDecimal spent) {
        BigDecimal limit = budget.getLimitAmount();
        BigDecimal remaining = limit.subtract(spent);
        boolean exceeded = spent.compareTo(limit) > 0;

        return new BudgetStatus(
                budget.getId(),
                budget.getCategory().getId(),
                budget.getCategory().getName(),
                YearMonth.from(budget.getMonth()),
                limit,
                spent,
                remaining,
                exceeded);
    }
}