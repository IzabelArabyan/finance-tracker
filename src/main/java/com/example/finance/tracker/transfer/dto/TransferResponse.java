package com.example.finance.tracker.transfer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


public record TransferResponse(
        UUID transferId,
        Long fromAccountId,
        Long toAccountId,
        BigDecimal amount,
        LocalDate date,
        BigDecimal fromAccountBalance, //new balances
        BigDecimal toAccountBalance) {
}