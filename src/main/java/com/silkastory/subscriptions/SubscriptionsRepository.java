package com.silkastory.subscriptions;

import java.util.List;
import java.util.Optional;

public interface SubscriptionsRepository {
    
    /**
     * 엔티티 저장
     */
    Subscriptions save(Subscriptions entity);
    
    /**
     * ID로 엔티티 조회
     */
    Optional<Subscriptions> findById(Long id);
    
    /**
     * 모든 엔티티 조회
     */
    List<Subscriptions> findAll();
    
    /**
     * 특정 사용자가 특정 대상을 구독하고 있는지 확인
     */
    Optional<Subscriptions> findByUserIdAndTargetId(String userId, String targetId);
    
    /**
     * 특정 사용자가 구독하고 있는 모든 대상 목록
     */
    List<Subscriptions> findByUserId(String userId);
    
    /**
     * 특정 대상을 구독하고 있는 모든 사용자 목록
     */
    List<Subscriptions> findByTargetId(String targetId);
    
    /**
     * 알림을 받는 구독자 목록 
     */
    List<Subscriptions> findByTargetIdAndIsAlramTrue(String targetId);
    
    /**
     * 특정 사용자의 구독 관계 삭제
     */
    void deleteByUserIdAndTargetId(String userId, String targetId);
    
    /**
     * ID로 엔티티 삭제
     */
    void deleteById(Long id);
} 