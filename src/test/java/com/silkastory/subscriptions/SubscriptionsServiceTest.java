package com.silkastory.subscriptions;

import com.silkastory.common.RepositoryFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SubscriptionsServiceTest {

    private SubscriptionsRepository subscriptionsRepository;
    private SubscriptionsService subscriptionsService;

    private final String SUBSCRIBER_ID = "test_subscriber";
    private final String CREATOR_ID = "test_creator";
    private final String OTHER_CREATOR_ID = "other_creator";
    
    // 테스트에서 생성한 구독 ID를 저장하여 나중에 정리할 수 있게 함
    private final List<Long> testSubscriptionIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // 실제 데이터베이스 레포지토리 사용
        subscriptionsRepository = RepositoryFactory.getRepository(SubscriptionsRepository.class);
        subscriptionsService = new SubscriptionsService(subscriptionsRepository);
    }

    @AfterEach
    void tearDown() {
        // 테스트에서 생성한 구독 정리
        for (Long id : testSubscriptionIds) {
            try {
                subscriptionsRepository.deleteById(id);
            } catch (Exception e) {
                System.err.println("구독 데이터 삭제 실패: " + id + ", " + e.getMessage());
            }
        }
        testSubscriptionIds.clear();
    }
    
    // 테스트에서 생성된 구독 ID를 추적하는 헬퍼 메소드
    private void trackSubscription(Subscriptions subscription) {
        if (subscription != null && subscription.getId() != null) {
            testSubscriptionIds.add(subscription.getId());
        }
    }

    @Test
    @DisplayName("구독 생성 테스트")
    void subscribe() {
        // when
        Subscriptions result = subscriptionsService.subscribe(SUBSCRIBER_ID, CREATOR_ID);
        trackSubscription(result);

        // then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(SUBSCRIBER_ID, result.getUserId());
        assertEquals(CREATOR_ID, result.getTargetId());
        assertTrue(result.isAlram());
        
        // 레포지토리에서 조회하여 확인
        assertTrue(subscriptionsService.isSubscribed(SUBSCRIBER_ID, CREATOR_ID));
    }

    @Test
    @DisplayName("이미 구독한 경우 예외 발생 테스트")
    void subscribeAlreadyExists() {
        // given
        Subscriptions subscription = subscriptionsService.subscribe(SUBSCRIBER_ID, CREATOR_ID);
        trackSubscription(subscription);

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            subscriptionsService.subscribe(SUBSCRIBER_ID, CREATOR_ID);
        });
        
        assertTrue(exception.getMessage().contains("이미 구독 중인 사용자입니다"));
    }

    @Test
    @DisplayName("자기 자신 구독 시도 시 예외 발생 테스트")
    void subscribeSelf() {
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            subscriptionsService.subscribe(SUBSCRIBER_ID, SUBSCRIBER_ID);
        });
        
        assertTrue(exception.getMessage().contains("자기 자신을 구독할 수 없습니다"));
    }

    @Test
    @DisplayName("구독 취소 테스트")
    void unsubscribe() {
        // given
        Subscriptions subscription = subscriptionsService.subscribe(SUBSCRIBER_ID, CREATOR_ID);
        Long subscriptionId = subscription.getId();
        assertTrue(subscriptionsService.isSubscribed(SUBSCRIBER_ID, CREATOR_ID));
        
        // when
        subscriptionsService.unsubscribe(SUBSCRIBER_ID, CREATOR_ID);
        
        // then
        assertFalse(subscriptionsService.isSubscribed(SUBSCRIBER_ID, CREATOR_ID));
        
        // 이미 삭제되었으므로 추적 목록에서 제거
        testSubscriptionIds.remove(subscriptionId);
    }

    @Test
    @DisplayName("구독하지 않은 사용자 구독 취소 시 예외 발생 테스트")
    void unsubscribeNotFound() {
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            subscriptionsService.unsubscribe(SUBSCRIBER_ID, CREATOR_ID);
        });
        
        assertTrue(exception.getMessage().contains("구독 정보가 존재하지 않습니다"));
    }

    @Test
    @DisplayName("구독 알림 설정 업데이트 테스트")
    void updateNotificationSetting() {
        // given
        Subscriptions subscription = subscriptionsService.subscribe(SUBSCRIBER_ID, CREATOR_ID);
        trackSubscription(subscription);
        assertTrue(subscription.isAlram());
        
        // when
        Subscriptions updated = subscriptionsService.updateNotificationSetting(SUBSCRIBER_ID, CREATOR_ID, false);
        
        // then
        assertNotNull(updated);
        assertEquals(subscription.getId(), updated.getId());
        assertFalse(updated.isAlram());
        
        // 저장소에서 조회
        List<Subscriptions> results = subscriptionsService.getSubscriptionsBySubscriber(SUBSCRIBER_ID);
        assertEquals(1, results.size());
        assertFalse(results.get(0).isAlram());
    }

    @Test
    @DisplayName("구독자의 구독 목록 조회 테스트")
    void getSubscriptionsBySubscriber() {
        // given
        Subscriptions subscription1 = subscriptionsService.subscribe(SUBSCRIBER_ID, CREATOR_ID);
        trackSubscription(subscription1);
        Subscriptions subscription2 = subscriptionsService.subscribe(SUBSCRIBER_ID, OTHER_CREATOR_ID);
        trackSubscription(subscription2);
        
        // when
        List<Subscriptions> subscriptions = subscriptionsService.getSubscriptionsBySubscriber(SUBSCRIBER_ID);
        
        // then
        assertNotNull(subscriptions);
        assertEquals(2, subscriptions.size());
        assertTrue(subscriptions.stream().allMatch(s -> s.getUserId().equals(SUBSCRIBER_ID)));
        assertTrue(subscriptions.stream().anyMatch(s -> s.getTargetId().equals(CREATOR_ID)));
        assertTrue(subscriptions.stream().anyMatch(s -> s.getTargetId().equals(OTHER_CREATOR_ID)));
    }

    @Test
    @DisplayName("구독자가 구독한 크리에이터의 ID 목록 조회 테스트")
    void getSubscribedCreatorIds() {
        // given
        Subscriptions subscription1 = subscriptionsService.subscribe(SUBSCRIBER_ID, CREATOR_ID);
        trackSubscription(subscription1);
        Subscriptions subscription2 = subscriptionsService.subscribe(SUBSCRIBER_ID, OTHER_CREATOR_ID);
        trackSubscription(subscription2);
        
        // when
        List<String> creatorIds = subscriptionsService.getSubscribedCreatorIds(SUBSCRIBER_ID);
        
        // then
        assertNotNull(creatorIds);
        assertEquals(2, creatorIds.size());
        assertTrue(creatorIds.contains(CREATOR_ID));
        assertTrue(creatorIds.contains(OTHER_CREATOR_ID));
    }

    @Test
    @DisplayName("특정 크리에이터의 구독자 목록 조회 테스트")
    void getSubscribersByCreator() {
        // given
        Subscriptions subscription1 = subscriptionsService.subscribe(SUBSCRIBER_ID, CREATOR_ID);
        trackSubscription(subscription1);
        Subscriptions subscription2 = subscriptionsService.subscribe("other_subscriber", CREATOR_ID);
        trackSubscription(subscription2);
        
        // when
        List<Subscriptions> subscribers = subscriptionsService.getSubscribersByCreator(CREATOR_ID);
        
        // then
        assertNotNull(subscribers);
        assertEquals(2, subscribers.size());
        assertTrue(subscribers.stream().allMatch(s -> s.getTargetId().equals(CREATOR_ID)));
    }
} 