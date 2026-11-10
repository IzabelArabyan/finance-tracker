package com.example.finance.tracker.report;

import com.example.finance.tracker.report.dto.MonthlyTotal;
import com.example.finance.tracker.transaction.repository.CategorySpending;
import com.example.finance.tracker.transaction.repository.TopExpense;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    
    @GetMapping("/spending-by-category")
    public List<CategorySpending> spendingByCategory(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return reportService.spendingByCategory(month);
    }


    @GetMapping("/monthly-totals")
    public List<MonthlyTotal> monthlyTotals(@RequestParam int year) {
        return reportService.monthlyTotals(year);
    }


    @GetMapping("/top-expenses")
    public List<TopExpense> topExpenses(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "10") int limit) {
        return reportService.topExpenses(from, to, limit);
    }
}