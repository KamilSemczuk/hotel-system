package com.example.backend_spring.service;

import com.example.backend_spring.exception.NotFoundException;
import com.example.backend_spring.model.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Generyczny serwis CRUD dla dowolnej encji.
 *
 * @param <T>  typ encji (musi mieć id – BaseEntity)
 * @param <ID> typ identyfikatora
 */
@Transactional
public abstract class CrudService<T extends BaseEntity<ID>, ID> {

    protected final JpaRepository<T, ID> repository;
    private final String entityName;

    protected CrudService(JpaRepository<T, ID> repository, String entityName) {
        this.repository = repository;
        this.entityName = entityName;
    }

    @Transactional(readOnly = true)
    public List<T> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public T findById(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(entityName + " o id " + id + " nie istnieje"));
    }

    public T create(T entity) {
        entity.setId(null); // id nadaje baza
        return repository.save(entity);
    }

    public T update(ID id, T entity) {
        findById(id);       // 404, jeśli nie ma takiego rekordu
        entity.setId(id);
        return repository.save(entity);
    }

    public void delete(ID id) {
        T entity = findById(id);
        beforeDelete(entity);
        repository.delete(entity);
        repository.flush(); // błąd klucza obcego pojawi się tutaj (→ 409)
    }

    // Miejsce na dodatkową logikę w klasach pochodnych
    protected void beforeDelete(T entity) {
    }
}
