package com.silkastory.subscriptions;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SubscriptionsService {
    private final SubscriptionsRepository subscriptionsRepository;

    public SubscriptionsService(SubscriptionsRepository subscriptionsRepository) {
        this.subscriptionsRepository = subscriptionsRepository;
    }

    /**
     * 구독 생성
     * - 사용자가 다른 사용자를 구독
     * - 기본적으로 알림은 활성화 상태로 설정
     */
    public Subscriptions subscribe(String userId, String targetId) {
        // 자기 자신을 구독할 수 없음
        if (userId.equals(targetId)) {
            throw new IllegalArgumentException("자기 자신을 구독할 수 없습니다.");
        }
        
        // 이미 구독 중인지 확인
        Optional<Subscriptions> existingSubscription = subscriptionsRepository.findByUserIdAndTargetId(userId, targetId);
        if (existingSubscription.isPresent()) {
            throw new IllegalArgumentException("이미 구독 중인 사용자입니다.");
        }
        
        // 구독 생성
        Subscriptions newSubscription = new Subscriptions(userId, targetId);
        return subscriptionsRepository.save(newSubscription);
    }

    /**
     * 구독 취소
     * - 사용자가 다른 사용자 구독을 취소
     */
    public void unsubscribe(String userId, String targetId) {
        // 구독 정보 확인
        Optional<Subscriptions> existingSubscription = subscriptionsRepository.findByUserIdAndTargetId(userId, targetId);
        if (existingSubscription.isEmpty()) {
            throw new IllegalArgumentException("구독 정보가 존재하지 않습니다.");
        }
        
        // 구독 삭제
        subscriptionsRepository.deleteByUserIdAndTargetId(userId, targetId);
    }

    /**
     * 알림 설정 변경
     * - 구독은 유지하되 알림만 설정/해제
     */
    public Subscriptions updateNotificationSetting(String userId, String targetId, boolean isAlram) {
        // 구독 정보 확인
        Subscriptions subscription = subscriptionsRepository.findByUserIdAndTargetId(userId, targetId)
                .orElseThrow(() -> new IllegalArgumentException("구독 정보가 존재하지 않습니다."));
        
        // 알림 설정 변경
        subscription.updateIsAlram(isAlram);
        return subscriptionsRepository.save(subscription);
    }

    /**
     * 구독 중인지 확인
     */
    public boolean isSubscribed(String userId, String targetId) {
        return subscriptionsRepository.findByUserIdAndTargetId(userId, targetId).isPresent();
    }

    /**
     * 사용자가 구독 중인 모든 대상 목록 조회
     */
    public List<Subscriptions> getSubscriptions(String userId) {
        return subscriptionsRepository.findByUserId(userId);
    }
    
    /**
     * 구독자의 구독 목록 조회 (getSubscriptions와 동일)
     */
    public List<Subscriptions> getSubscriptionsBySubscriber(String userId) {
        return subscriptionsRepository.findByUserId(userId);
    }
    
    /**
     * 구독자가 구독한 크리에이터의 ID 목록 조회
     */
    public List<String> getSubscribedCreatorIds(String userId) {
        List<Subscriptions> subscriptions = subscriptionsRepository.findByUserId(userId);
        return subscriptions.stream()
                .map(Subscriptions::getTargetId)
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자를 구독 중인 모든 사용자 목록 조회
     */
    public List<Subscriptions> getSubscribers(String targetId) {
        return subscriptionsRepository.findByTargetId(targetId);
    }
    
    /**
     * 특정 크리에이터의 구독자 목록 조회 (getSubscribers와 동일)
     */
    public List<Subscriptions> getSubscribersByCreator(String targetId) {
        return subscriptionsRepository.findByTargetId(targetId);
    }

    /**
     * 알림을 받는 구독자 목록 조회
     */
    public List<Subscriptions> getNotifiableSubscribers(String targetId) {
        return subscriptionsRepository.findByTargetIdAndIsAlramTrue(targetId);
    }

    /**
     * 알림을 받는 구독자의 ID 목록 조회
     */
    public List<String> getNotifiableSubscriberIds(String userId) {
        return getSubscribers(userId).stream()
                .filter(Subscriptions::isAlram)
                .map(Subscriptions::getTargetId)
                .collect(Collectors.toList());
    }
} 