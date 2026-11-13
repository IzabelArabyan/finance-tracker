package com.example.finance.tracker.category;

import com.example.finance.tracker.category.dto.CategoryRequest;
import com.example.finance.tracker.category.repository.CategoryRepository;
import com.example.finance.tracker.common.exception.NotFoundException;
import com.example.finance.tracker.common.exception.ValidationException;
import com.example.finance.tracker.security.CurrentUser;
import com.example.finance.tracker.transaction.repository.TransactionRepository;
import com.example.finance.tracker.budget.repository.BudgetRepository;
import com.example.finance.tracker.user.User;
import com.example.finance.tracker.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final CurrentUser currentUser;

    public CategoryService(CategoryRepository categoryRepository,
                           TransactionRepository transactionRepository,
                           BudgetRepository budgetRepository,
                           UserRepository userRepository,
                           CurrentUser currentUser) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.currentUser = currentUser;
    }

    public List<Category> findAll() {
        return categoryRepository.findAllVisible(currentUser.id());
    }

    public Category findById(Long id) {
        return categoryRepository.findVisibleById(id, currentUser.id())
                .orElseThrow(() -> new NotFoundException("Category", id));
    }

    public Category create(CategoryRequest request) {
        Long userId = currentUser.id();
        String name = request.name().trim();

        requireNameIsFree(name,userId);

        if (categoryRepository.existsByName(name)) {
            throw new ValidationException("Category " + name + " already exists");
        }
        User owner = userRepository.getReferenceById(userId);
        Category saved = categoryRepository.save(new Category(null, name, owner));
        log.info("Created category id={} name={}", saved.getId(), saved.getName());
        return saved;
    }

    public Category update(Long id, CategoryRequest request) {
        Category category = findOwnCategory(id);
        String name = request.name().trim();

        if (!name.equals(category.getName())) {
            requireNameIsFree(name, currentUser.id());
        }

        category.setName(name);
        Category saved = categoryRepository.save(category);
        log.info("Renamed category id={} to name={}", id, saved.getName());
        return saved;
    }

    public void delete(Long id) {
        findOwnCategory(id);
        if (transactionRepository.existsByCategoryId(id)) {
            throw new ValidationException("Category " + id + " is used by transactions, delete them first");
        }
        if (budgetRepository.existsByCategoryId(id)) {
            throw new ValidationException("Category " + id + " has budgets, delete them first");
        }
        categoryRepository.deleteById(id);
        log.info("Deleted category id={}", id);
    }
    private Category findOwnCategory(Long id) {
        return categoryRepository.findByIdAndOwnerId(id, currentUser.id())
                .orElseThrow(() -> new NotFoundException("Category", id));
    }

    private void requireNameIsFree(String name, Long userId) {
        boolean globalExists = categoryRepository.existsByNameAndOwnerIsNull(name);
        boolean ownExists = categoryRepository.existsByNameAndOwnerId(name, userId);
        if (globalExists || ownExists) {
            throw new ValidationException("Category " + name + " already exists");
        }
    }

    }
