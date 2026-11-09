package com.example.finance.tracker.budget;
import com.example.finance.tracker.budget.dto.BudgetRequest;
import com.example.finance.tracker.budget.dto.BudgetResponse;
import com.example.finance.tracker.budget.dto.BudgetStatus;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PutMapping
    //giving only categoryId, "month": "2026-10", limit
    public BudgetResponse save(@Valid @RequestBody BudgetRequest request) {
        return budgetService.save(request);
    }

    // GET /api/budgets?month=2026-10
    @GetMapping
    public List<BudgetStatus> forMonth(@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {
        return budgetService.forMonth(month);
    }
}
