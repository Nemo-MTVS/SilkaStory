package com.silkastory.notifications;

import com.silkastory.common.RepositoryFactory;
import com.silkastory.subscriptions.Subscriptions;
import com.silkastory.subscriptions.SubscriptionsRepository;
import com.silkastory.subscriptions.SubscriptionsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class NotificationsServiceTest {

    private NotificationsRepository notificationsRepository;
    private SubscriptionsRepository subscriptionsRepository;
    private SubscriptionsService subscriptionsService;
    private NotificationsService notificationsService;

    private final String USER_ID = "test_user";
    private final String TEST_MESSAGE = "테스트 알림 메시지";
    private final String PUBLISHER_ID = "test_publisher";
    private final String SUBSCRIBER_ID_1 = "test_subscriber_1";
    private final String SUBSCRIBER_ID_2 = "test_subscriber_2";
    
    // 테스트에서 생성한 데이터 ID를 저장하여 나중에 정리할 수 있게 함
    private final List<Long> testNotificationIds = new ArrayList<>();
    private final List<Long> testSubscriptionIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // 실제 데이터베이스 레포지토리 사용
        notificationsRepository = RepositoryFactory.getRepository(NotificationsRepository.class);
        subscriptionsRepository = RepositoryFactory.getRepository(SubscriptionsRepository.class);
        subscriptionsService = new SubscriptionsService(subscriptionsRepository);
        notificationsService = new NotificationsService(notificationsRepository, subscriptionsService);
        
        // 구독 관계 설정 - 알림 테스트를 위한 데이터 준비
        Subscriptions sub1 = subscriptionsService.subscribe(SUBSCRIBER_ID_1, PUBLISHER_ID);
        trackSubscription(sub1);
        Subscriptions sub2 = subscriptionsService.subscribe(SUBSCRIBER_ID_2, PUBLISHER_ID);
        trackSubscription(sub2);
        // 두 번째 구독자는 알림 비활성화
        subscriptionsService.updateNotificationSetting(SUBSCRIBER_ID_2, PUBLISHER_ID, false);
    }

    @AfterEach
    void tearDown() {
        // 테스트에서 생성한 알림 정리
        for (Long id : testNotificationIds) {
            try {
                notificationsRepository.deleteById(id);
            } catch (Exception e) {
                System.err.println("알림 데이터 삭제 실패: " + id + ", " + e.getMessage());
            }
        }
        testNotificationIds.clear();
        
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
    
    // 테스트에서 생성된 알림 ID를 추적하는 헬퍼 메소드
    private void trackNotification(Notifications notification) {
        if (notification != null && notification.getId() != null) {
            testNotificationIds.add(notification.getId());
        }
    }
    
    // 테스트에서 생성된 구독 ID를 추적하는 헬퍼 메소드
    private void trackSubscription(Subscriptions subscription) {
        if (subscription != null && subscription.getId() != null) {
            testSubscriptionIds.add(subscription.getId());
        }
    }

    @Test
    @DisplayName("알림 생성 테스트")
    void createNotification() {
        // when
        Notifications notification = notificationsService.createNotification(TEST_MESSAGE, USER_ID);
        trackNotification(notification);

        // then
        assertNotNull(notification);
        assertNotNull(notification.getId());
        assertEquals(TEST_MESSAGE, notification.getMessage());
        assertEquals(USER_ID, notification.getUserId());
        assertNotNull(notification.getSendDate());
        assertFalse(notification.isState());
        
        // 레포지토리에서 조회하여 확인
        List<Notifications> userNotifications = notificationsService.getUserNotifications(USER_ID);
        assertFalse(userNotifications.isEmpty());
        assertEquals(1, userNotifications.size());
        assertEquals(TEST_MESSAGE, userNotifications.get(0).getMessage());
    }

    @Test
    @DisplayName("사용자 알림 목록 조회 테스트")
    void getUserNotifications() {
        // given
        Notifications notification1 = notificationsService.createNotification("알림 1", USER_ID);
        trackNotification(notification1);
        Notifications notification2 = notificationsService.createNotification("알림 2", USER_ID);
        trackNotification(notification2);
        
        // when
        List<Notifications> notifications = notificationsService.getUserNotifications(USER_ID);
        
        // then
        assertNotNull(notifications);
        assertEquals(2, notifications.size());
        assertTrue(notifications.stream().allMatch(n -> n.getUserId().equals(USER_ID)));
    }

    @Test
    @DisplayName("읽지 않은 알림 목록 조회 테스트")
    void getUnreadNotifications() {
        // given
        Notifications notification1 = notificationsService.createNotification("첫 번째 알림", USER_ID);
        trackNotification(notification1);
        Notifications notification2 = notificationsService.createNotification("두 번째 알림", USER_ID);
        trackNotification(notification2);
        
        // 첫 번째 알림은 읽음 처리
        notificationsService.markAsRead(notification1.getId(), USER_ID);
        
        // when
        List<Notifications> unreadNotifications = notificationsService.getUnreadNotifications(USER_ID);
        
        // then
        assertNotNull(unreadNotifications);
        assertEquals(1, unreadNotifications.size());
        assertEquals("두 번째 알림", unreadNotifications.get(0).getMessage());
    }

    @Test
    @DisplayName("알림 읽음 처리 테스트")
    void markAsRead() {
        // given
        Notifications notification = notificationsService.createNotification(TEST_MESSAGE, USER_ID);
        trackNotification(notification);
        assertFalse(notification.isState());
        
        // when
        Notifications result = notificationsService.markAsRead(notification.getId(), USER_ID);
        
        // then
        assertNotNull(result);
        assertTrue(result.isState());
        
        // 레포지토리에서 조회하여 확인
        List<Notifications> unreadNotifications = notificationsService.getUnreadNotifications(USER_ID);
        assertTrue(unreadNotifications.isEmpty());
    }

    @Test
    @DisplayName("읽음 처리 시 알림이 없는 경우 예외 발생 테스트")
    void markAsReadNotFound() {
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            notificationsService.markAsRead(999L, USER_ID);
        });
        
        assertTrue(exception.getMessage().contains("알림을 찾을 수 없습니다"));
    }

    @Test
    @DisplayName("다른 사용자의 알림 읽음 처리 시도 시 예외 발생 테스트")
    void markAsReadWrongUser() {
        // given
        Notifications notification = notificationsService.createNotification(TEST_MESSAGE, USER_ID);
        trackNotification(notification);
        
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            notificationsService.markAsRead(notification.getId(), "other_user");
        });
        
        assertTrue(exception.getMessage().contains("자신의 알림만 읽음 처리할 수 있습니다"));
    }

    @Test
    @DisplayName("구독자에게 알림 전송 테스트")
    void notifySubscribers() {
        // when
        List<Notifications> notifications = notificationsService.notifySubscribers(TEST_MESSAGE, PUBLISHER_ID);
        
        // 생성된 알림 추적
        for (Notifications notification : notifications) {
            trackNotification(notification);
        }
        
        // then
        assertNotNull(notifications);
        assertEquals(1, notifications.size()); // 알림 활성화된 구독자만 받아야 함
        assertEquals(SUBSCRIBER_ID_1, notifications.get(0).getUserId());
        assertEquals(TEST_MESSAGE, notifications.get(0).getMessage());
        
        // 비활성화된 구독자는 알림을 받지 않아야 함
        List<Notifications> subscriber2Notifications = notificationsService.getUserNotifications(SUBSCRIBER_ID_2);
        assertTrue(subscriber2Notifications.isEmpty());
    }
} 