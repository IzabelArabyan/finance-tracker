package com.example.finance.tracker.category.repository;

import com.example.finance.tracker.TestcontainersConfiguration;
import com.example.finance.tracker.category.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    //1
    @Test
    void flywayCreatesTheFiveDefaultCategories() {
        assertEquals(5, categoryRepository.findAll().size());
    }

    //2
    @Test
    void existsByNameFindsADefaultCategory() {
        assertTrue(categoryRepository.existsByName("Salary"));
    }

    //3
    @Test
    void existsByNameFindsANewCategory() {
        categoryRepository.save(new Category(null, "Coffee"));

        assertTrue(categoryRepository.existsByName("Coffee"));
    }

    //4
    @Test
    void existsByNameIsCaseSensitive() {

        assertFalse(categoryRepository.existsByName("salary"));
    }
}
