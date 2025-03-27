package com.silkastory.common;

import java.util.List;

public interface MiniJPARepository<T, ID> {
    T save(T entity);

    void update(T entity);

    void insert(T entity);

    T findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
}
