package com.example.finance_tracker.category.repository;

import com.example.finance_tracker.category.Category;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;


@Repository
public class InMemoryCategoryRepository {

    private final Map<Long, Category> table = new ConcurrentSkipListMap<>();
    private final AtomicLong idSequence = new AtomicLong();

    public Category save(Category category) {
        if (category.getId() == null) {
            category.setId(idSequence.incrementAndGet());
        }
        table.put(category.getId(), copy(category));
        return copy(category);
    }

    public Optional<Category> findById(Long id) {
        Category row = table.get(id);
        if (row == null) {
            return Optional.empty();
        }
        return Optional.of(copy(row));
    }

    public List<Category> findAll() {
        List<Category> result = new ArrayList<>();
        for (Category row : table.values()) {
            result.add(copy(row));
        }
        return result;
    }

    public boolean existsById(Long id) {
        return table.containsKey(id);
    }

    public void deleteById(Long id) {
        table.remove(id);
    }

    private static Category copy(Category category) {
        return new Category(category.getId(), category.getName());
    }


}