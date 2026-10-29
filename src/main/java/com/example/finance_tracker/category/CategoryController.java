package com.example.finance_tracker.category;

import com.example.finance_tracker.category.dto.CategoryRequest;
import com.example.finance_tracker.category.dto.CategoryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryResponse> findAll(){
        List<CategoryResponse> res = new ArrayList<>();
        for(Category category: categoryService.findAll()){
            res.add(CategoryResponse.from(category));
        }
        return res;
    }

    @GetMapping("/{id}")
    public CategoryResponse findById(@PathVariable Long id){
        return CategoryResponse.from(categoryService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@RequestBody CategoryRequest request){
        return CategoryResponse.from(categoryService.create(request));
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @RequestBody CategoryRequest request){
        return CategoryResponse.from(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        categoryService.delete(id);
    }

}
