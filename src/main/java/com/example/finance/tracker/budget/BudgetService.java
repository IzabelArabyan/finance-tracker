package com.example.finance.tracker.budget;

import com.example.finance.tracker.budget.dto.BudgetRequest;
import com.example.finance.tracker.budget.dto.BudgetResponse;
import com.example.finance.tracker.budget.dto.BudgetStatus;
import com.example.finance.tracker.budget.repository.BudgetRepository;
import com.example.finance.tracker.category.Category;
import com.example.finance.tracker.category.repository.CategoryRepository;
import com.example.finance.tracker.common.exception.NotFoundException;
import com.example.finance.tracker.security.CurrentUser;
import com.example.finance.tracker.transaction.repository.CategorySpending;
import com.example.finance.tracker.transaction.repository.TransactionRepository;
import com.example.finance.tracker.user.User;
import com.example.finance.tracker.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;

    public BudgetService(BudgetRepository budgetRepository,
                         CategoryRepository categoryRepository,
                         TransactionRepository transactionRepository,
                         UserRepository userRepository,
                         CurrentUser currentUser) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.currentUser = currentUser;
    }

    public BudgetResponse save(BudgetRequest request) {
        Long userId = currentUser.id();
        Category category = categoryRepository.findVisibleById(request.categoryId(), userId)
                .orElseThrow(() -> new NotFoundException("Category", request.categoryId()));

        LocalDate month = request.month().atDay(1);

        Budget budget;
        Optional<Budget> existing = budgetRepository.findByOwnerIdAndCategoryIdAndMonth(userId, category.getId(), month);
        if (existing.isPresent()) {
            budget = existing.get();    // replace and set new limit
            budget.setLimitAmount(request.limit());
        } else {
            User owner = userRepository.getReferenceById(userId);
            budget = new Budget(null, category, month, request.limit(), owner);
        }

        return BudgetResponse.from(budgetRepository.save(budget));
    }

    @Transactional
    public List<BudgetStatus> forMonth(YearMonth month) {
        Long userId = currentUser.id();
        LocalDate from = month.atDay(1);
        LocalDate to = month.atEndOfMonth();

        // all budgets of month
        List<Budget> budgets = budgetRepository.findAllForMonth(from, userId);

        //how much was spent per category in month
        Map<Long, BigDecimal> spentByCategory = new HashMap<>();
        for (CategorySpending row : transactionRepository.spendingByCategory(userId, from, to)) {
            spentByCategory.put(row.categoryId(), row.total());
        }


        List<BudgetStatus> result = new ArrayList<>();
        for (Budget budget : budgets) {
            BigDecimal spent = spentByCategory.get(budget.getCategory().getId());
            if (spent == null) {
                spent = BigDecimal.ZERO;
            }
            result.add(BudgetStatus.of(budget, spent));
        }
        return result;
    }

}
