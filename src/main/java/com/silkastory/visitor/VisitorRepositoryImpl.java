package com.silkastory.visitor;

import com.silkastory.common.AbstractJPARepository;
import java.util.List;

public class VisitorRepositoryImpl extends AbstractJPARepository<Visitor, Long> implements VisitorRepository {
    
    @Override
    public List<Visitor> findByUserId(String userId) {
        String jpql = "SELECT v FROM com.silkastory.visitor.Visitor v WHERE v.userId = ?1";
        return findByQuery(jpql, userId);
    }
    
    @Override
    public List<Visitor> findByPostId(Long postId) {
        String jpql = "SELECT v FROM com.silkastory.visitor.Visitor v WHERE v.postId = ?1";
        return findByQuery(jpql, postId);
    }
    
    @Override
    public Long countDistinctUserIdByPostId(Long postId) {
        String jpql = "SELECT COUNT(DISTINCT v.userId) FROM com.silkastory.visitor.Visitor v WHERE v.postId = ?1";
        Long result = (Long) findSingleResultByQuery(jpql, postId);
        return result != null ? result : 0L;
    }
} 