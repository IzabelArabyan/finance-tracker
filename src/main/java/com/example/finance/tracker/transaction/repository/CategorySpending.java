package com.example.finance.tracker.transaction.repository;

import java.math.BigDecimal;

//how much spent for category saving results of query here
public record CategorySpending(Long categoryId, String categoryName, BigDecimal total) {
}