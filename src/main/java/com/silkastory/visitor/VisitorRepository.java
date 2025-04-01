package com.silkastory.visitor;

import java.util.List;
import java.util.Optional;

public interface VisitorRepository {
    
    /**
     * 엔티티 저장
     */
    Visitor save(Visitor entity);
    
    /**
     * ID로 엔티티 조회
     */
    Optional<Visitor> findById(Long id);
    
    /**
     * 모든 엔티티 조회
     */
    List<Visitor> findAll();
    
    /**
     * 특정 사용자의 방문 기록 조회
     */
    List<Visitor> findByUserId(String userId);
    
    /**
     * 특정 게시글의 방문 기록 조회
     */
    List<Visitor> findByPostId(Long postId);
    
    /**
     * 게시글별 고유 방문자 수 조회
     */
    Long countDistinctUserIdByPostId(Long postId);
    
    /**
     * ID로 엔티티 삭제
     */
    void deleteById(Long id);
}
