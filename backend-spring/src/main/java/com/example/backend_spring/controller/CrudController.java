package com.example.backend_spring.controller;

import com.example.backend_spring.model.BaseEntity;
import com.example.backend_spring.service.CrudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Generyczny kontroler REST: GET, GET/{id}, POST, PUT/{id}, DELETE/{id}.
 * Klasa pochodna podaje tylko @RequestMapping i serwis.
 */
public abstract class CrudController<T extends BaseEntity<ID>, ID> {

    protected final CrudService<T, ID> service;

    protected CrudController(CrudService<T, ID> service) {
        this.service = service;
    }

    @GetMapping
    public List<T> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public T getById(@PathVariable ID id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public T create(@Valid @RequestBody T entity) {
        return service.create(entity);
    }

    @PutMapping("/{id}")
    public T update(@PathVariable ID id, @Valid @RequestBody T entity) {
        return service.update(id, entity);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable ID id) {
        service.delete(id);
    }
}
