package com.silkastory.common;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * 제네릭 JPA 리포지토리 
 * @param <T> 엔티티 타입
 * @param <ID> 엔티티 ID 타입
 */
public interface JPARepository<T, ID extends Serializable> {
    
    /**
     * 엔티티 저장
     */
    T save(T entity);
    
    /**
     * ID로 엔티티 조회
     */
    Optional<T> findById(ID id);
    
    /**
     * 모든 엔티티 조회
     */
    List<T> findAll();
    
    /**
     * 조건에 맞는 엔티티 조회
     */
    List<T> findByQuery(String jpql, Object... params);
    
    /**
     * ID로 엔티티 삭제
     */
    void deleteById(ID id);
    
    /**
     * 엔티티 삭제
     */
    void delete(T entity);
} 