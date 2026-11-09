package com.example.finance.tracker.budget.dto;

import com.example.finance.tracker.budget.Budget;

import java.math.BigDecimal;
import java.time.YearMonth;

public record BudgetResponse(Long id, Long categoryId, YearMonth month, BigDecimal limit) {

    public static BudgetResponse from(Budget budget) {
        return new BudgetResponse(
                budget.getId(),
                budget.getCategory().getId(),
                YearMonth.from(budget.getMonth()),
                budget.getLimitAmount());
    }
}