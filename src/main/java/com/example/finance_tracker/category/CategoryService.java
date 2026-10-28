package com.example.finance_tracker.category;

import com.example.finance_tracker.category.dto.CategoryRequest;
import com.example.finance_tracker.category.repository.InMemoryCategoryRepository;
import com.example.finance_tracker.transaction.repository.InMemoryTransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final InMemoryCategoryRepository categoryRepository;
    private final InMemoryTransactionRepository transactionRepository;

    public CategoryService(InMemoryCategoryRepository categoryRepository, InMemoryTransactionRepository transactionRepository) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<Category> findAll(){
        return categoryRepository.findAll();
    }
    public Category findById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category " + id + " not found");
        }
        return category.get();
    }
    public Category create(CategoryRequest request) {
        requireName(request.name());
        Category saved = categoryRepository.save(new Category(null, request.name().trim()));
        log.info("Created category id={} name={}", saved.getId(), saved.getName());
        return saved;
    }

    public Category update(Long id, CategoryRequest request) {
        requireName(request.name());
        Category category = findById(id);
        category.setName(request.name().trim());
        Category saved = categoryRepository.save(category);
        log.info("Renamed category id={} to name={}", id, saved.getName());
        return saved;
    }

    public void delete(Long id) {
        findById(id);
        if (transactionRepository.existsByCategoryId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Category " + id + " is used by transactions, delete them first");
        }
        categoryRepository.deleteById(id);
        log.info("Deleted category id={}", id);
    }

    private static void requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category name must not be blank");
        }
    }
}
