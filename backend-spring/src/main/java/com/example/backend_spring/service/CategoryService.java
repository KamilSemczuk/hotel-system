package com.example.backend_spring.service;

import com.example.backend_spring.model.Category;
import com.example.backend_spring.repository.CategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class CategoryService extends CrudService<Category, Long> {

    public CategoryService(CategoryRepository repository) {
        super(repository, "Kategoria");
    }
}
