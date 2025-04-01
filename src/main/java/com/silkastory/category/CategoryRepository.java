package com.silkastory.category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    
    /**
     * 엔티티 저장
     */
    Category save(Category entity);
    
    /**
     * ID로 엔티티 조회
     */
    Optional<Category> findById(Long id);
    
    /**
     * 모든 엔티티 조회
     */
    List<Category> findAll();
    
    /**
     * 해당 카테고리를 상위 카테고리로 가지는 카테고리가 있는지 확인
     */
    boolean existsByTargetCategoryId(Long categoryId);
    
    /**
     * 특정 부모 카테고리 ID로 카테고리 목록 조회
     */
    List<Category> findByTargetCategoryId(Long parentCategoryId);
    
    /**
     * 특정 사용자의 최상위 카테고리 목록 조회
     */
    List<Category> findByTargetCategoryIdIsNullAndUserId(String userId);
    
    /**
     * ID로 엔티티 삭제
     */
    void deleteById(Long id);
}
