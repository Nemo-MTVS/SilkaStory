package com.silkastory.category;

import com.silkastory.common.AbstractJPARepository;
import java.util.List;

public class CategoryRepositoryImpl extends AbstractJPARepository<Category, Long> implements CategoryRepository {
    
    @Override
    public boolean existsByTargetCategoryId(Long categoryId) {
        Long count = (Long) findSingleResultByQuery(
            "SELECT COUNT(c) FROM com.silkastory.category.Category c WHERE c.targetCategoryId = ?1", 
            categoryId
        );
        return count != null && count > 0;
    }

    @Override
    public List<Category> findByTargetCategoryId(Long parentCategoryId) {
        return findByQuery(
            "SELECT c FROM com.silkastory.category.Category c WHERE c.targetCategoryId = ?1", 
            parentCategoryId
        );
    }

    @Override
    public List<Category> findByTargetCategoryIdIsNullAndUserId(String userId) {
        return findByQuery(
            "SELECT c FROM com.silkastory.category.Category c WHERE c.targetCategoryId IS NULL AND c.userId = ?1", 
            userId
        );
    }
} 