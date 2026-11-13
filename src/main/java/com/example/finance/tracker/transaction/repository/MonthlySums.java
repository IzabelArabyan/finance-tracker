package com.example.finance.tracker.transaction.repository;

import java.math.BigDecimal;

public record MonthlySums(Integer month, BigDecimal income, BigDecimal expense) {
}
