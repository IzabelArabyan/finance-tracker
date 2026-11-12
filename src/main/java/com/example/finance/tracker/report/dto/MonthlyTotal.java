package com.example.finance.tracker.report.dto;

import java.math.BigDecimal;

// net = income - expense
public record MonthlyTotal(int month, BigDecimal income, BigDecimal expense, BigDecimal net) {
}