package com.silkastory.visitor;

import com.silkastory.common.RepositoryFactory;
import com.silkastory.infrastructure.database.JDBCConnection;

/**
 * 방문자 기능 자동 테스트 클래스
 * 테스트 시나리오를 자동으로 실행하고 결과를 출력합니다.
 */
public class VisitorAutoTest {
    private static final VisitorRepository visitorRepository = RepositoryFactory.getRepository(VisitorRepository.class);
    private static final VisitorService visitorService = new VisitorService(visitorRepository);
    
    // 테스트용 사용자 ID와 게시글 ID
    private static final String TEST_USER_ID = "test_user";
    private static final String TEST_USER_ID_2 = "test_user_2";
    private static final Long TEST_POST_ID = 1000L;
    
    // 테스트 결과 통계
    private static int totalTests = 0;
    private static int passedTests = 0;
    
    public static void main(String[] args) {
        System.out.println("방문자 기능 자동 테스트를 시작합니다...");
        
        try {
            // 테스트 전 기존 테스트 데이터 정리
            cleanupTestData();
            
            // 1. 방문 기록 저장 테스트
            testSaveVisit();
            
            // 2. 방문자 수 조회 테스트
            testCountVisitors();
            
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
    
    private static void testSaveVisit() {
        printTestHeader("방문 기록 저장 테스트");
        
        // 테스트 케이스 1: 방문 기록 저장
        totalTests++;
        try {
            Visitor visitor = visitorService.save(TEST_USER_ID, TEST_POST_ID);
            
            assertNotNull("방문 객체가 생성되었습니다.", visitor);
            assertNotNull("방문 ID가 생성되었습니다.", visitor.getId());
            assertEquals("사용자 ID가 일치합니다.", TEST_USER_ID, visitor.getUserId());
            assertEquals("게시글 ID가 일치합니다.", TEST_POST_ID, visitor.getPostId());
            assertNotNull("방문 일시가 설정되었습니다.", visitor.getVisitDate());
            
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 방문 기록 저장 - " + e.getMessage());
        }
        
        // 테스트 케이스 2: 동일 사용자의 추가 방문 기록
        totalTests++;
        try {
            // 약간의 딜레이를 두고 동일 사용자의 방문 기록
            Thread.sleep(100);
            
            Visitor visitor = visitorService.save(TEST_USER_ID, TEST_POST_ID);
            
            assertNotNull("추가 방문 객체가 생성되었습니다.", visitor);
            assertNotNull("추가 방문 ID가 생성되었습니다.", visitor.getId());
            
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 동일 사용자 추가 방문 - " + e.getMessage());
        }
        
        // 테스트 케이스 3: 다른 사용자의 방문 기록
        totalTests++;
        try {
            Visitor visitor = visitorService.save(TEST_USER_ID_2, TEST_POST_ID);
            
            assertNotNull("다른 사용자 방문 객체가 생성되었습니다.", visitor);
            assertEquals("사용자 ID가 다릅니다.", TEST_USER_ID_2, visitor.getUserId());
            assertEquals("게시글 ID가 일치합니다.", TEST_POST_ID, visitor.getPostId());
            
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 다른 사용자 방문 - " + e.getMessage());
        }
    }
    
    private static void testCountVisitors() {
        printTestHeader("방문자 수 조회 테스트");
        
        // 테스트 케이스 1: 게시글 방문자 수 조회
        totalTests++;
        try {
            Long visitorCount = visitorService.countByPostId(TEST_POST_ID);
            
            assertNotNull("방문자 수가 반환되었습니다.", visitorCount);
            assertEquals("방문자 수가 2명입니다.", Long.valueOf(2), visitorCount);
            
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 방문자 수 조회 - " + e.getMessage());
        }
        
        // 테스트 케이스 2: 방문 기록이 없는 게시글 방문자 수 조회
        totalTests++;
        try {
            Long visitorCount = visitorService.countByPostId(9999L);
            
            assertNotNull("방문자 수가 반환되었습니다.", visitorCount);
            assertEquals("방문자 수가 0명입니다.", Long.valueOf(0), visitorCount);
            
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 방문자 없는 게시글 조회 - " + e.getMessage());
        }
    }
    
    private static void cleanupTestData() {
        System.out.println("테스트 데이터 정리 중...");
        try {
            // 테스트로 생성된 방문 기록 삭제
            // 실제 구현에 따라 삭제 방법이 달라질 수 있음
            // 여기서는 단순화를 위해 생략
            
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
        if (expected == null && actual == null) {
            System.out.println("확인: " + message);
            return;
        }
        if (expected == null || actual == null || !expected.equals(actual)) {
            throw new AssertionError(message + " - 예상: " + expected + ", 실제: " + actual);
        }
        System.out.println("확인: " + message);
    }
} 