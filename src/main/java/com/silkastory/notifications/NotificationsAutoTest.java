package com.silkastory.notifications;

import com.silkastory.common.RepositoryFactory;
import com.silkastory.infrastructure.database.JDBCConnection;
import com.silkastory.subscriptions.SubscriptionsRepository;
import com.silkastory.subscriptions.SubscriptionsService;
import com.silkastory.subscriptions.Subscriptions;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 알림 기능 자동 테스트 클래스
 * 테스트 시나리오를 자동으로 실행하고 결과를 출력합니다.
 */
public class NotificationsAutoTest {
    private static final NotificationsRepository notificationsRepository = RepositoryFactory.getRepository(NotificationsRepository.class);
    private static final SubscriptionsRepository subscriptionsRepository = RepositoryFactory.getRepository(SubscriptionsRepository.class);
    private static final SubscriptionsService subscriptionsService = new SubscriptionsService(subscriptionsRepository);
    private static final NotificationsService notificationsService = new NotificationsService(notificationsRepository, subscriptionsService);
    
    // 테스트용 사용자 ID
    private static final String TEST_USER_1 = "test_user_1";
    private static final String TEST_USER_2 = "test_user_2";
    private static final String TEST_USER_3 = "test_user_3";
    
    // 테스트 결과 통계
    private static int totalTests = 0;
    private static int passedTests = 0;
    
    public static void main(String[] args) {
        System.out.println("알림 기능 자동 테스트를 시작합니다...");
        
        try {
            // 테스트 전 기존 테스트 데이터 정리
            cleanupTestData();
            
            // 테스트를 위한 구독 관계 설정
            setupSubscriptions();
            
            // 1. 알림 생성 테스트
            testCreateNotification();
            
            // 2. 구독자들에게 알림 전송 테스트
            testSendNotificationToSubscribers();
            
            // 3. 사용자 알림 목록 조회 테스트
            testGetUserNotifications();
            
            // 4. 읽지 않은 알림 목록 조회 테스트
            testGetUnreadNotifications();
            
            // 5. 알림 읽음 처리 테스트
            testMarkNotificationAsRead();
            
            // 6. 모든 알림 읽음 처리 테스트
            testMarkAllNotificationsAsRead();
            
            // 7. 알림 개수 조회 테스트
            testCountNotifications();
            
            // 테스트 결과 출력
            printTestSummary();
            
            // 테스트 후 데이터 정리
            cleanupTestData();
            
        } catch (Exception e) {
            System.err.println("테스트 실행 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 데이터베이스 연결 종료
            JDBCConnection.close();
        }
    }
    
    private static void setupSubscriptions() {
        System.out.println("테스트를 위한 구독 관계 설정 중...");
        try {
            // 사용자2와 사용자3이 사용자1을 구독
            subscriptionsService.subscribe(TEST_USER_2, TEST_USER_1);
            subscriptionsService.subscribe(TEST_USER_3, TEST_USER_1);
            
            // 사용자3은 알림을 받지 않도록 설정
            subscriptionsService.updateNotificationSetting(TEST_USER_3, TEST_USER_1, false);
            
            System.out.println("구독 관계 설정 완료");
        } catch (Exception e) {
            System.err.println("구독 관계 설정 중 오류 발생: " + e.getMessage());
        }
    }
    
    private static void testCreateNotification() {
        printTestHeader("알림 생성 테스트");
        
        // 테스트 케이스 1: 정상 알림 생성
        totalTests++;
        try {
            String message = "테스트 알림 메시지";
            Notifications notification = notificationsService.createNotification(message, TEST_USER_1);
            
            assertNotNull("알림 객체가 생성되었습니다.", notification);
            assertEquals("알림 메시지가 일치합니다.", message, notification.getMessage());
            assertEquals("수신자 ID가 일치합니다.", TEST_USER_1, notification.getUserId());
            assertFalse("알림은 읽지 않은 상태입니다.", notification.isState());
            assertNotNull("발송 시간이 설정되었습니다.", notification.getSendDate());
            
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 알림 생성 - " + e.getMessage());
        }
    }
    
    private static void testSendNotificationToSubscribers() {
        printTestHeader("구독자들에게 알림 전송 테스트");
        
        // 테스트 케이스 1: 구독자들에게 알림 전송
        totalTests++;
        try {
            String message = "구독자들에게 전송되는 테스트 알림";
            notificationsService.sendNotificationToSubscribers(message, TEST_USER_1);
            
            // 알림을 받기로 설정한 구독자(사용자2)에게 알림이 전송되었는지 확인
            List<Notifications> user2Notifications = notificationsService.getUserNotifications(TEST_USER_2);
            assertNotNull("사용자2의 알림 목록이 반환되었습니다.", user2Notifications);
            assertFalse("사용자2의 알림 목록이 비어있지 않습니다.", user2Notifications.isEmpty());
            boolean found = false;
            for (Notifications notification : user2Notifications) {
                if (message.equals(notification.getMessage())) {
                    found = true;
                    break;
                }
            }
            assertTrue("사용자2에게 알림이 전송되었습니다.", found);
            
            // 알림을 받지 않기로 설정한 구독자(사용자3)에게는 알림이 전송되지 않았는지 확인
            List<Notifications> user3Notifications = notificationsService.getUserNotifications(TEST_USER_3);
            boolean user3Received = false;
            for (Notifications notification : user3Notifications) {
                if (message.equals(notification.getMessage())) {
                    user3Received = true;
                    break;
                }
            }
            assertFalse("사용자3에게는 알림이 전송되지 않았습니다.", user3Received);
            
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 구독자들에게 알림 전송 - " + e.getMessage());
        }
    }
    
    private static void testGetUserNotifications() {
        printTestHeader("사용자 알림 목록 조회 테스트");
        
        // 테스트 케이스 1: 추가 알림 생성 후 사용자 알림 목록 조회
        totalTests++;
        try {
            // 사용자1에게 추가 알림 생성
            String message1 = "추가 알림 1";
            String message2 = "추가 알림 2";
            notificationsService.createNotification(message1, TEST_USER_1);
            notificationsService.createNotification(message2, TEST_USER_1);
            
            // 사용자1의 알림 목록 조회
            List<Notifications> notifications = notificationsService.getUserNotifications(TEST_USER_1);
            assertNotNull("알림 목록이 반환되었습니다.", notifications);
            assertFalse("알림 목록이 비어있지 않습니다.", notifications.isEmpty());
            assertTrue("알림이 2개 이상 있습니다.", notifications.size() >= 2);
            
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 사용자 알림 목록 조회 - " + e.getMessage());
        }
        
        // 테스트 케이스 2: 알림이 없는 사용자 목록 조회
        totalTests++;
        try {
            List<Notifications> notifications = notificationsService.getUserNotifications("non_existing_user");
            assertNotNull("알림 목록이 반환되었습니다.", notifications);
            assertTrue("알림 목록이 비어있습니다.", notifications.isEmpty());
            
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 알림 없는 사용자 목록 조회 - " + e.getMessage());
        }
    }
    
    private static void testGetUnreadNotifications() {
        printTestHeader("읽지 않은 알림 목록 조회 테스트");
        
        // 테스트 케이스 1: 읽지 않은 알림 목록 조회
        totalTests++;
        try {
            List<Notifications> unreadNotifications = notificationsService.getUnreadNotifications(TEST_USER_1);
            assertNotNull("읽지 않은 알림 목록이 반환되었습니다.", unreadNotifications);
            assertFalse("읽지 않은 알림 목록이 비어있지 않습니다.", unreadNotifications.isEmpty());
            
            // 모든 알림이 읽지 않은 상태인지 확인
            boolean allUnread = true;
            for (Notifications notification : unreadNotifications) {
                if (notification.isState()) {
                    allUnread = false;
                    break;
                }
            }
            assertTrue("모든 알림이 읽지 않은 상태입니다.", allUnread);
            
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 읽지 않은 알림 목록 조회 - " + e.getMessage());
        }
    }
    
    private static void testMarkNotificationAsRead() {
        printTestHeader("알림 읽음 처리 테스트");
        
        // 테스트 케이스 1: 알림 읽음 처리
        totalTests++;
        try {
            // 사용자1의 첫 번째 알림을 가져옴
            List<Notifications> notifications = notificationsService.getUserNotifications(TEST_USER_1);
            if (notifications.isEmpty()) {
                throw new IllegalStateException("테스트할 알림이 없습니다.");
            }
            
            Notifications notification = notifications.get(0);
            Long notificationId = notification.getId();
            
            // 알림 읽음 처리
            Notifications readNotification = notificationsService.markNotificationAsRead(notificationId);
            assertNotNull("알림 객체가 반환되었습니다.", readNotification);
            assertTrue("알림이 읽음 상태로 변경되었습니다.", readNotification.isState());
            
            // 다시 조회하여 확인
            List<Notifications> updatedNotifications = notificationsService.getUserNotifications(TEST_USER_1);
            boolean found = false;
            for (Notifications n : updatedNotifications) {
                if (n.getId().equals(notificationId)) {
                    assertTrue("알림이 읽음 상태입니다.", n.isState());
                    found = true;
                    break;
                }
            }
            assertTrue("읽음 처리된 알림을 찾았습니다.", found);
            
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 알림 읽음 처리 - " + e.getMessage());
        }
        
        // 테스트 케이스 2: 존재하지 않는 알림 읽음 처리 시도 (예외 발생 예상)
        totalTests++;
        try {
            notificationsService.markNotificationAsRead(-1L);
            System.err.println("테스트 실패: 존재하지 않는 알림 읽음 처리 - 예외가 발생하지 않았습니다.");
        } catch (IllegalArgumentException e) {
            // 예상된 예외이므로 테스트 통과
            System.out.println("테스트 통과: 존재하지 않는 알림 읽음 처리 시 예외 발생 - " + e.getMessage());
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 존재하지 않는 알림 읽음 처리 - 예상치 않은 예외 발생 - " + e.getMessage());
        }
    }
    
    private static void testMarkAllNotificationsAsRead() {
        printTestHeader("모든 알림 읽음 처리 테스트");
        
        // 테스트 케이스 1: 모든 알림 읽음 처리
        totalTests++;
        try {
            // 추가 알림 생성
            notificationsService.createNotification("추가 읽지 않은 알림", TEST_USER_1);
            
            // 모든 알림 읽음 처리
            notificationsService.markAllNotificationsAsRead(TEST_USER_1);
            
            // 읽지 않은 알림 목록 조회
            List<Notifications> unreadNotifications = notificationsService.getUnreadNotifications(TEST_USER_1);
            assertTrue("모든 알림이 읽음 처리되어 읽지 않은 알림이 없습니다.", unreadNotifications.isEmpty());
            
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 모든 알림 읽음 처리 - " + e.getMessage());
        }
    }
    
    private static void testCountNotifications() {
        printTestHeader("알림 개수 조회 테스트");
        
        // 테스트 케이스 1: 전체 알림 개수 조회
        totalTests++;
        try {
            // 모든 알림이 이미 읽음 처리된 상태이므로 몇 개의 추가 알림 생성
            notificationsService.createNotification("개수 테스트 알림 1", TEST_USER_1);
            notificationsService.createNotification("개수 테스트 알림 2", TEST_USER_1);
            
            int totalCount = notificationsService.countNotifications(TEST_USER_1);
            int unreadCount = notificationsService.countUnreadNotifications(TEST_USER_1);
            
            assertTrue("전체 알림 개수가 2 이상입니다.", totalCount >= 2);
            assertEquals("읽지 않은 알림 개수가 2입니다.", 2, unreadCount);
            
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 알림 개수 조회 - " + e.getMessage());
        }
    }
    
    private static void cleanupTestData() {
        System.out.println("테스트 데이터 정리 중...");
        try {
            // 모든 테스트 사용자의 알림 데이터 제거 (실제 환경에서는 조심해서 사용)
            // 이 부분은 실제 구현에 따라 달라질 수 있음
            
            // 테스트 구독 관계 제거
            try {
                subscriptionsService.unsubscribe(TEST_USER_2, TEST_USER_1);
            } catch (Exception ignored) {}
            
            try {
                subscriptionsService.unsubscribe(TEST_USER_3, TEST_USER_1);
            } catch (Exception ignored) {}
            
            System.out.println("테스트 데이터 정리 완료");
        } catch (Exception e) {
            System.err.println("테스트 데이터 정리 중 오류 발생: " + e.getMessage());
        }
    }
    
    private static void printTestHeader(String testName) {
        System.out.println("\n========== " + testName + " ==========");
    }
    
    private static void printTestSummary() {
        System.out.println("\n========== 테스트 결과 요약 ==========");
        System.out.println("총 테스트 수: " + totalTests);
        System.out.println("통과한 테스트 수: " + passedTests);
        System.out.println("실패한 테스트 수: " + (totalTests - passedTests));
        System.out.println("통과율: " + (totalTests > 0 ? (passedTests * 100 / totalTests) : 0) + "%");
    }
    
    // 테스트 유틸리티 메서드
    private static void assertNotNull(String message, Object obj) {
        if (obj == null) {
            throw new AssertionError(message + " - 객체가 null입니다.");
        }
        System.out.println("확인: " + message);
    }
    
    private static void assertEquals(String message, Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " - 예상: " + expected + ", 실제: " + actual);
        }
        System.out.println("확인: " + message);
    }
    
    private static void assertTrue(String message, boolean condition) {
        if (!condition) {
            throw new AssertionError(message + " - 조건이 false입니다.");
        }
        System.out.println("확인: " + message);
    }
    
    private static void assertFalse(String message, boolean condition) {
        if (condition) {
            throw new AssertionError(message + " - 조건이 true입니다.");
        }
        System.out.println("확인: " + message);
    }
} 