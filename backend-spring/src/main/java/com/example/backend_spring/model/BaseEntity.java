package com.example.backend_spring.model;

// Wspólny interfejs encji z identyfikatorem – wymagany przez generyczny CrudService
public interface BaseEntity<ID> {
    ID getId();

    void setId(ID id);
}
