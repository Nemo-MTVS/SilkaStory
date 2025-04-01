package com.silkastory.subscriptions;

import com.silkastory.common.RepositoryFactory;
import com.silkastory.infrastructure.database.JDBCConnection;

import java.util.List;
import java.util.Optional;

/**
 * 구독 기능 자동 테스트 클래스
 * 테스트 시나리오를 자동으로 실행하고 결과를 출력합니다.
 */
public class SubscriptionsAutoTest {
    private static final SubscriptionsRepository subscriptionsRepository = RepositoryFactory.getRepository(SubscriptionsRepository.class);
    private static final SubscriptionsService subscriptionsService = new SubscriptionsService(subscriptionsRepository);
    
    // 테스트용 사용자 ID
    private static final String TEST_USER_1 = "test_user_1";
    private static final String TEST_USER_2 = "test_user_2";
    private static final String TEST_USER_3 = "test_user_3";
    
    // 테스트 결과 통계
    private static int totalTests = 0;
    private static int passedTests = 0;
    
    public static void main(String[] args) {
        System.out.println("구독 기능 자동 테스트를 시작합니다...");
        
        try {
            // 테스트 전 기존 테스트 데이터 정리
            cleanupTestData();
            
            // 1. 구독 생성 테스트
            testSubscribe();
            
            // 2. 구독 확인 테스트
            testIsSubscribed();
            
            // 3. 알림 설정 변경 테스트
            testUpdateNotificationSetting();
            
            // 4. 구독 목록 조회 테스트
            testGetSubscriptions();
            
            // 5. 구독자 목록 조회 테스트
            testGetSubscribers();
            
            // 6. 알림 구독자 목록 조회 테스트
            testGetNotifiableSubscribers();
            
            // 7. 구독 취소 테스트
            testUnsubscribe();
            
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
    
    private static void testSubscribe() {
        printTestHeader("구독 생성 테스트");
        
        // 테스트 케이스 1: 정상 구독
        totalTests++;
        try {
            Subscriptions subscription = subscriptionsService.subscribe(TEST_USER_1, TEST_USER_2);
            assertNotNull("구독 객체가 생성되었습니다.", subscription);
            assertEquals("구독자 ID가 일치합니다.", TEST_USER_1, subscription.getUserId());
            assertEquals("대상 사용자 ID가 일치합니다.", TEST_USER_2, subscription.getTargetId());
            assertTrue("기본 알림이 활성화되어 있습니다.", subscription.isAlram());
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 정상 구독 생성 - " + e.getMessage());
        }
        
        // 테스트 케이스 2: 자기 자신 구독 시도 (예외 발생 예상)
        totalTests++;
        try {
            subscriptionsService.subscribe(TEST_USER_1, TEST_USER_1);
            System.err.println("테스트 실패: 자기 자신 구독 - 예외가 발생하지 않았습니다.");
        } catch (IllegalArgumentException e) {
            // 예상된 예외이므로 테스트 통과
            System.out.println("테스트 통과: 자기 자신 구독 시도 시 예외 발생 - " + e.getMessage());
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 자기 자신 구독 - 예상치 않은 예외 발생 - " + e.getMessage());
        }
        
        // 테스트 케이스 3: 이미 구독 중인 사용자 재구독 시도 (예외 발생 예상)
        totalTests++;
        try {
            subscriptionsService.subscribe(TEST_USER_1, TEST_USER_2);
            System.err.println("테스트 실패: 중복 구독 - 예외가 발생하지 않았습니다.");
        } catch (IllegalArgumentException e) {
            // 예상된 예외이므로 테스트 통과
            System.out.println("테스트 통과: 중복 구독 시도 시 예외 발생 - " + e.getMessage());
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 중복 구독 - 예상치 않은 예외 발생 - " + e.getMessage());
        }
        
        // 추가 구독 생성 (다른 테스트를 위한 데이터)
        try {
            subscriptionsService.subscribe(TEST_USER_3, TEST_USER_1);
            subscriptionsService.subscribe(TEST_USER_2, TEST_USER_1);
        } catch (Exception e) {
            System.err.println("테스트 데이터 준비 실패: " + e.getMessage());
        }
    }
    
    private static void testIsSubscribed() {
        printTestHeader("구독 확인 테스트");
        
        // 테스트 케이스 1: 구독 중인 관계 확인
        totalTests++;
        try {
            boolean isSubscribed = subscriptionsService.isSubscribed(TEST_USER_1, TEST_USER_2);
            assertTrue("사용자1이 사용자2를 구독 중입니다.", isSubscribed);
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 구독 확인 - " + e.getMessage());
        }
        
        // 테스트 케이스 2: 구독하지 않은 관계 확인
        totalTests++;
        try {
            boolean isSubscribed = subscriptionsService.isSubscribed(TEST_USER_1, TEST_USER_3);
            assertFalse("사용자1이 사용자3을 구독하지 않았습니다.", !isSubscribed);
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 비구독 확인 - " + e.getMessage());
        }
    }
    
    private static void testUpdateNotificationSetting() {
        printTestHeader("알림 설정 변경 테스트");
        
        // 테스트 케이스 1: 알림 비활성화
        totalTests++;
        try {
            Subscriptions subscription = subscriptionsService.updateNotificationSetting(TEST_USER_1, TEST_USER_2, false);
            assertNotNull("구독 객체가 반환되었습니다.", subscription);
            assertFalse("알림이 비활성화되었습니다.", subscription.isAlram());
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 알림 비활성화 - " + e.getMessage());
        }
        
        // 테스트 케이스 2: 알림 다시 활성화
        totalTests++;
        try {
            Subscriptions subscription = subscriptionsService.updateNotificationSetting(TEST_USER_1, TEST_USER_2, true);
            assertNotNull("구독 객체가 반환되었습니다.", subscription);
            assertTrue("알림이 활성화되었습니다.", subscription.isAlram());
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 알림 활성화 - " + e.getMessage());
        }
        
        // 테스트 케이스 3: 존재하지 않는 구독 관계 알림 설정 변경 시도 (예외 발생 예상)
        totalTests++;
        try {
            subscriptionsService.updateNotificationSetting(TEST_USER_1, "non_existing_user", false);
            System.err.println("테스트 실패: 존재하지 않는 구독 관계 - 예외가 발생하지 않았습니다.");
        } catch (IllegalArgumentException e) {
            // 예상된 예외이므로 테스트 통과
            System.out.println("테스트 통과: 존재하지 않는 구독 관계 알림 설정 변경 시 예외 발생 - " + e.getMessage());
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 존재하지 않는 구독 관계 - 예상치 않은 예외 발생 - " + e.getMessage());
        }
    }
    
    private static void testGetSubscriptions() {
        printTestHeader("구독 목록 조회 테스트");
        
        // 테스트 케이스 1: 사용자1의 구독 목록 조회
        totalTests++;
        try {
            List<Subscriptions> subscriptions = subscriptionsService.getSubscriptions(TEST_USER_1);
            assertNotNull("구독 목록이 반환되었습니다.", subscriptions);
            assertFalse("구독 목록이 비어있지 않습니다.", subscriptions.isEmpty());
            assertEquals("구독 목록 크기가 일치합니다.", 1, subscriptions.size());
            assertEquals("구독 대상이 일치합니다.", TEST_USER_2, subscriptions.get(0).getTargetId());
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 구독 목록 조회 - " + e.getMessage());
        }
        
        // 테스트 케이스 2: 구독하지 않은 사용자 목록 조회
        totalTests++;
        try {
            List<Subscriptions> subscriptions = subscriptionsService.getSubscriptions("non_existing_user");
            assertNotNull("구독 목록이 반환되었습니다.", subscriptions);
            assertTrue("구독 목록이 비어있습니다.", subscriptions.isEmpty());
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 비구독 사용자 목록 조회 - " + e.getMessage());
        }
    }
    
    private static void testGetSubscribers() {
        printTestHeader("구독자 목록 조회 테스트");
        
        // 테스트 케이스 1: 사용자1의 구독자 목록 조회
        totalTests++;
        try {
            List<Subscriptions> subscribers = subscriptionsService.getSubscribers(TEST_USER_1);
            assertNotNull("구독자 목록이 반환되었습니다.", subscribers);
            assertFalse("구독자 목록이 비어있지 않습니다.", subscribers.isEmpty());
            assertEquals("구독자 목록 크기가 일치합니다.", 2, subscribers.size());
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 구독자 목록 조회 - " + e.getMessage());
        }
        
        // 테스트 케이스 2: 구독자가 없는 사용자 목록 조회
        totalTests++;
        try {
            List<Subscriptions> subscribers = subscriptionsService.getSubscribers("non_existing_user");
            assertNotNull("구독자 목록이 반환되었습니다.", subscribers);
            assertTrue("구독자 목록이 비어있습니다.", subscribers.isEmpty());
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 구독자 없는 사용자 목록 조회 - " + e.getMessage());
        }
    }
    
    private static void testGetNotifiableSubscribers() {
        printTestHeader("알림 구독자 목록 조회 테스트");
        
        // 테스트 케이스 1: 사용자1의 알림 받는 구독자 목록 조회
        totalTests++;
        try {
            // 먼저 사용자3의 알림 설정을 비활성화
            subscriptionsService.updateNotificationSetting(TEST_USER_3, TEST_USER_1, false);
            
            List<Subscriptions> notifiableSubscribers = subscriptionsService.getNotifiableSubscribers(TEST_USER_1);
            assertNotNull("알림 구독자 목록이 반환되었습니다.", notifiableSubscribers);
            assertFalse("알림 구독자 목록이 비어있지 않습니다.", notifiableSubscribers.isEmpty());
            assertEquals("알림 구독자 목록 크기가 일치합니다.", 1, notifiableSubscribers.size());
            assertEquals("알림 구독자가 일치합니다.", TEST_USER_2, notifiableSubscribers.get(0).getUserId());
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 알림 구독자 목록 조회 - " + e.getMessage());
        }
    }
    
    private static void testUnsubscribe() {
        printTestHeader("구독 취소 테스트");
        
        // 테스트 케이스 1: 정상 구독 취소
        totalTests++;
        try {
            subscriptionsService.unsubscribe(TEST_USER_1, TEST_USER_2);
            boolean isSubscribed = subscriptionsService.isSubscribed(TEST_USER_1, TEST_USER_2);
            assertFalse("구독이 취소되었습니다.", isSubscribed);
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 구독 취소 - " + e.getMessage());
        }
        
        // 테스트 케이스 2: 존재하지 않는 구독 관계 취소 시도 (예외 발생 예상)
        totalTests++;
        try {
            subscriptionsService.unsubscribe(TEST_USER_1, TEST_USER_2);
            System.err.println("테스트 실패: 존재하지 않는 구독 취소 - 예외가 발생하지 않았습니다.");
        } catch (IllegalArgumentException e) {
            // 예상된 예외이므로 테스트 통과
            System.out.println("테스트 통과: 존재하지 않는 구독 취소 시 예외 발생 - " + e.getMessage());
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 존재하지 않는 구독 취소 - 예상치 않은 예외 발생 - " + e.getMessage());
        }
    }
    
    private static void cleanupTestData() {
        System.out.println("테스트 데이터 정리 중...");
        try {
            // 테스트 사용자 간의 구독 관계 모두 삭제
            Optional<Subscriptions> sub1 = subscriptionsRepository.findByUserIdAndTargetId(TEST_USER_1, TEST_USER_2);
            if (sub1.isPresent()) {
                subscriptionsRepository.deleteByUserIdAndTargetId(TEST_USER_1, TEST_USER_2);
            }
            
            Optional<Subscriptions> sub2 = subscriptionsRepository.findByUserIdAndTargetId(TEST_USER_2, TEST_USER_1);
            if (sub2.isPresent()) {
                subscriptionsRepository.deleteByUserIdAndTargetId(TEST_USER_2, TEST_USER_1);
            }
            
            Optional<Subscriptions> sub3 = subscriptionsRepository.findByUserIdAndTargetId(TEST_USER_3, TEST_USER_1);
            if (sub3.isPresent()) {
                subscriptionsRepository.deleteByUserIdAndTargetId(TEST_USER_3, TEST_USER_1);
            }
            
            Optional<Subscriptions> sub4 = subscriptionsRepository.findByUserIdAndTargetId(TEST_USER_1, TEST_USER_3);
            if (sub4.isPresent()) {
                subscriptionsRepository.deleteByUserIdAndTargetId(TEST_USER_1, TEST_USER_3);
            }
            
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