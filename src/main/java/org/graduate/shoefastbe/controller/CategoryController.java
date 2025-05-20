package org.graduate.shoefastbe.controller;

import lombok.AllArgsConstructor;
import org.graduate.shoefastbe.dto.category.CategoryRequest;
import org.graduate.shoefastbe.dto.category.CategoryResponse;
import org.graduate.shoefastbe.service.category.CategoryService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/category")
@AllArgsConstructor
@CrossOrigin
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/list")
    public Page<CategoryResponse> getAllCategory(@ParameterObject Pageable pageable){
        return categoryService.getAllCategory(pageable);
    }
    @GetMapping("/list-admin")
    public Page<CategoryResponse> getAllCategoryAdmin(@ParameterObject Pageable pageable){
        return categoryService.getAllCategoryAdmin(pageable);
    }
    @PostMapping("/create")
    public CategoryResponse create(@RequestBody CategoryRequest categoryRequest){
        return categoryService.create(categoryRequest);
    }

    @PostMapping("/update")
    public CategoryResponse update(@RequestBody CategoryRequest categoryRequest){
        return categoryService.update(categoryRequest);
    }

    @GetMapping("/detail")
    public CategoryResponse getDetail(@RequestParam Long id){
        return categoryService.getDetail(id);
    }

}
