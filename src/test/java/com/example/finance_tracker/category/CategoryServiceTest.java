package com.example.finance_tracker.category;

import com.example.finance_tracker.category.dto.CategoryRequest;
import com.example.finance_tracker.category.repository.InMemoryCategoryRepository;
import com.example.finance_tracker.transaction.repository.InMemoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private InMemoryCategoryRepository categoryRepository;

    @Mock
    private InMemoryTransactionRepository transactionRepository;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryRepository, transactionRepository);
    }

    //create
    //1
    @Test
    void createSavesTheName() {
        when(categoryRepository.save(any(Category.class))).then(returnsFirstArg());

        Category result = categoryService.create(new CategoryRequest("Salary"));

        assertEquals("Salary", result.getName());
    }

    //2
    @Test
    void createRemovesSpacesAroundTheName() {
        when(categoryRepository.save(any(Category.class))).then(returnsFirstArg());

        Category result = categoryService.create(new CategoryRequest("  Salary  "));

        assertEquals("Salary", result.getName());
    }

    //3
    @Test
    void createReturnsTheCategoryWithIdFromRepository() {
        when(categoryRepository.save(any(Category.class))).thenReturn(new Category(1L, "Salary"));

        Category result = categoryService.create(new CategoryRequest("Salary"));

        assertEquals(1L, result.getId());
        assertEquals("Salary", result.getName());
    }

    //4
    @Test
    void createWithNullNameGives400() {
        expectCreateBadRequest(new CategoryRequest(null));
    }

    //5
    @Test
    void createWithEmptyNameGives400() {
        expectCreateBadRequest(new CategoryRequest(""));
    }

    //6
    @Test
    void createWithOnlySpacesInNameGives400() {
        expectCreateBadRequest(new CategoryRequest("   "));
    }

    private void expectCreateBadRequest(CategoryRequest request) {
        try {
            categoryService.create(request);
            fail("Expected a 400 error");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        }
        verify(categoryRepository, never()).save(any(Category.class));
    }

    //update
    //1
    @Test
    void updateChangesTheName() {
        Category category = new Category(1L, "Food");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).then(returnsFirstArg());

        Category result = categoryService.update(1L, new CategoryRequest("Groceries"));

        assertEquals("Groceries", result.getName());
        verify(categoryRepository).save(category);
    }

    //2
    @Test
    void updateKeepsTheSameId() {
        Category category = new Category(1L, "Food");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).then(returnsFirstArg());

        Category result = categoryService.update(1L, new CategoryRequest("Groceries"));

        assertEquals(1L, result.getId());
    }


    //3
    @Test
    void updateWithNullNameGives400() {
        expectUpdateBadRequest(new CategoryRequest(null));
    }

    //4
    @Test
    void updateWithEmptyNameGives400() {
        expectUpdateBadRequest(new CategoryRequest(""));
    }

    //5
    @Test
    void updateWithOnlySpacesInNameGives400() {
        expectUpdateBadRequest(new CategoryRequest("   "));
    }

    //6
    @Test
    void updateGives404WhenCategoryIsMissing() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        try {
            categoryService.update(99L, new CategoryRequest("Groceries"));
            fail("Expected a 404 error");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
        verify(categoryRepository, never()).save(any(Category.class));
    }


    private void expectUpdateBadRequest(CategoryRequest request) {
        try {
            categoryService.update(1L, request);
            fail("Expected a 400 error");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        }
        verify(categoryRepository, never()).save(any(Category.class));
    }

    //delete
    //1
    @Test
    void deleteRemovesTheCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category(1L, "Food")));
        when(transactionRepository.existsByCategoryId(1L)).thenReturn(false);

        categoryService.delete(1L);

        verify(categoryRepository).deleteById(1L);
    }

    //2
    @Test
    void deleteGives404WhenNoCategory() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        try {
            categoryService.delete(99L);
            fail("Expected a 404 error");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
        }
        verify(transactionRepository, never()).existsByCategoryId(99L);
        verify(categoryRepository, never()).deleteById(99L);
    }

    //3
    @Test
    void deleteGives409WhenCategoryIsUsed() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category(1L, "Food")));
        when(transactionRepository.existsByCategoryId(1L)).thenReturn(true);

        try {
            categoryService.delete(1L);
            fail("Expected a 409 error");
        } catch (ResponseStatusException e) {
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
        }
        verify(categoryRepository, never()).deleteById(1L);
    }
}