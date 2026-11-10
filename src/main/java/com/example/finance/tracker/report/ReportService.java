package com.example.finance.tracker.report;
import com.example.finance.tracker.common.exception.ValidationException;
import com.example.finance.tracker.report.dto.MonthlyTotal;
import com.example.finance.tracker.transaction.repository.CategorySpending;
import com.example.finance.tracker.transaction.repository.MonthlySums;
import com.example.finance.tracker.transaction.repository.TopExpense;
import com.example.finance.tracker.transaction.repository.TransactionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    private static final int MAX_LIMIT = 100;

    private final TransactionRepository transactionRepository;

    public ReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    //reports/spending-by-category
    public List<CategorySpending> spendingByCategory(YearMonth month) {
        return transactionRepository.spendingByCategory(month.atDay(1), month.atEndOfMonth());
    }
    //reports/monthly-totals
    public List<MonthlyTotal> monthlyTotals(int year) {
        LocalDate from = LocalDate.of(year, 1, 1);
        LocalDate to = LocalDate.of(year, 12, 31);


        Map<Integer, MonthlySums> sumsByMonth = new HashMap<>();
        for (MonthlySums row : transactionRepository.monthlyTotals(from, to)) {
            sumsByMonth.put(row.month(), row);
        }


        List<MonthlyTotal> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            BigDecimal income = BigDecimal.ZERO;
            BigDecimal expense = BigDecimal.ZERO;

            MonthlySums sums = sumsByMonth.get(month);
            if (sums != null) {
                income = sums.income();
                expense = sums.expense();
            }

            BigDecimal net = income.subtract(expense);
            result.add(new MonthlyTotal(month, income, expense, net));
        }
        return result;
    }
    //reports/top-expenses first 10
    public List<TopExpense> topExpenses(LocalDate from, LocalDate to, int limit) {
        if (from.isAfter(to)) {
            throw new ValidationException("'from' must be on or before 'to'");
        }
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new ValidationException("limit must be between 1 and " + MAX_LIMIT);
        }
        return transactionRepository.topExpenses(from, to, PageRequest.of(0, limit));
    }
}
