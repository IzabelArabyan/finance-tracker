package com.example.finance.tracker.category;

import com.example.finance.tracker.category.dto.CategoryRequest;
import com.example.finance.tracker.category.repository.CategoryRepository;
import com.example.finance.tracker.common.exception.NotFoundException;
import com.example.finance.tracker.common.exception.ValidationException;
import com.example.finance.tracker.transaction.repository.TransactionRepository;
import com.example.finance.tracker.budget.repository.BudgetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           TransactionRepository transactionRepository,
                           BudgetRepository budgetRepository) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category", id));
    }

    public Category create(CategoryRequest request) {
        String name = request.name().trim();

        if (categoryRepository.existsByName(name)) {
            throw new ValidationException("Category " + name + " already exists");
        }
        Category saved = categoryRepository.save(new Category(null, name));
        log.info("Created category id={} name={}", saved.getId(), saved.getName());
        return saved;
    }

    public Category update(Long id, CategoryRequest request) {
        Category category = findById(id);
        String name = request.name().trim();

        if (!name.equals(category.getName()) && categoryRepository.existsByName(name)) {
            throw new ValidationException("Category " + name + " already exists");
        }
        category.setName(name);
        Category saved = categoryRepository.save(category);
        log.info("Renamed category id={} to name={}", id, saved.getName());
        return saved;
    }

    public void delete(Long id) {
        findById(id);
        if (transactionRepository.existsByCategoryId(id)) {
            throw new ValidationException("Category " + id + " is used by transactions, delete them first");
        }
        if (budgetRepository.existsByCategoryId(id)) {
            throw new ValidationException("Category " + id + " has budgets, delete them first");
        }
        categoryRepository.deleteById(id);
        log.info("Deleted category id={}", id);
    }

}
