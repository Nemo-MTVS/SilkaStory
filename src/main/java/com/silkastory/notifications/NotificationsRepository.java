package com.silkastory.notifications;

import java.util.List;
import java.util.Optional;

public interface NotificationsRepository {
    
    /**
     * 엔티티 저장
     */
    Notifications save(Notifications entity);
    
    /**
     * ID로 엔티티 조회
     */
    Optional<Notifications> findById(Long id);
    
    /**
     * 모든 엔티티 조회
     */
    List<Notifications> findAll();
    
    /**
     * 특정 사용자의 모든 알림 조회
     */
    List<Notifications> findByUserId(String userId);
    
    /**
     * 특정 사용자의 읽지 않은 알림 조회
     */
    List<Notifications> findByUserIdAndStateFalse(String userId);
    
    /**
     * 특정 사용자의 모든 알림 개수 조회
     */
    int countByUserId(String userId);
    
    /**
     * 특정 사용자의 읽지 않은 알림 개수 조회
     */
    int countByUserIdAndStateFalse(String userId);
    
    /**
     * ID로 엔티티 삭제
     */
    void deleteById(Long id);
} 