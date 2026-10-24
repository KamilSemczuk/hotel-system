package com.example.backend_spring.controller;

import com.example.backend_spring.model.Category;
import com.example.backend_spring.service.CategoryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
public class CategoryController extends CrudController<Category, Long> {

    public CategoryController(CategoryService service) {
        super(service);
    }
}
