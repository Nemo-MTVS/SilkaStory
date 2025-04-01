package com.silkastory.category;

import com.silkastory.common.RepositoryFactory;
import com.silkastory.infrastructure.database.JDBCConnection;

import java.util.List;

/**
 * 카테고리 기능 자동 테스트 클래스
 * 테스트 시나리오를 자동으로 실행하고 결과를 출력합니다.
 */
public class CategoryAutoTest {
    private static final CategoryRepository categoryRepository = RepositoryFactory.getRepository(CategoryRepository.class);
    private static final CategoryService categoryService = new CategoryService(categoryRepository);
    
    // 테스트용 사용자 ID
    private static final String TEST_USER_ID = "test_user";
    
    // 테스트 결과 통계
    private static int totalTests = 0;
    private static int passedTests = 0;
    
    public static void main(String[] args) {
        System.out.println("카테고리 기능 자동 테스트를 시작합니다...");
        
        try {
            // 테스트 전 기존 테스트 데이터 정리
            cleanupTestData();
            
            // 1. 카테고리 생성 테스트
            testCreateCategory();
            
            // 2. 카테고리 조회 테스트
            testFindCategory();
            
            // 3. 최상위 카테고리 조회 테스트
            testFindRootCategories();
            
            // 4. 하위 카테고리 조회 테스트
            testFindSubCategories();
            
            // 5. 카테고리 수정 테스트
            testUpdateCategory();
            
            // 6. 카테고리 삭제 테스트
            testDeleteCategory();
            
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
    
    private static void testCreateCategory() {
        printTestHeader("카테고리 생성 테스트");
        
        // 테스트 케이스 1: 루트 카테고리 생성
        totalTests++;
        try {
            String categoryName = "테스트 루트 카테고리";
            Category category = categoryService.create(categoryName, TEST_USER_ID, null);
            
            assertNotNull("카테고리 객체가 생성되었습니다.", category);
            assertNotNull("카테고리 ID가 생성되었습니다.", category.getId());
            assertEquals("카테고리 이름이 일치합니다.", categoryName, category.getName());
            assertEquals("사용자 ID가 일치합니다.", TEST_USER_ID, category.getUserId());
            assertEquals("깊이가 1입니다.", Integer.valueOf(1), category.getDepth());
            assertNull("상위 카테고리가 없습니다.", category.getTargetCategoryId());
            
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 루트 카테고리 생성 - " + e.getMessage());
        }
        
        // 테스트 케이스 2: 하위 카테고리 생성
        totalTests++;
        try {
            // 먼저 상위 카테고리 조회
            List<Category> categories = categoryRepository.findAll();
            if (categories.isEmpty()) {
                throw new IllegalStateException("테스트할 상위 카테고리가 없습니다.");
            }
            
            Category parentCategory = categories.get(0);
            String subCategoryName = "테스트 하위 카테고리";
            Category subCategory = categoryService.create(subCategoryName, TEST_USER_ID, parentCategory.getId());
            
            assertNotNull("하위 카테고리 객체가 생성되었습니다.", subCategory);
            assertEquals("하위 카테고리 이름이 일치합니다.", subCategoryName, subCategory.getName());
            assertEquals("사용자 ID가 일치합니다.", TEST_USER_ID, subCategory.getUserId());
            assertEquals("깊이가 상위 카테고리 + 1입니다.", Integer.valueOf(parentCategory.getDepth() + 1), subCategory.getDepth());
            assertEquals("상위 카테고리 ID가 일치합니다.", parentCategory.getId(), subCategory.getTargetCategoryId());
            
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 하위 카테고리 생성 - " + e.getMessage());
        }
        
        // 테스트 케이스 3: 다른 사용자의 카테고리를 상위로 지정할 때 예외 발생
        totalTests++;
        try {
            // 다른 사용자의 카테고리 생성
            Category otherUserCategory = new Category("다른 사용자의 카테고리", 1, "other_user", null);
            otherUserCategory = categoryRepository.save(otherUserCategory);
            
            // 다른 사용자의 카테고리를 상위로 지정하여 생성 시도
            categoryService.create("실패할 카테고리", TEST_USER_ID, otherUserCategory.getId());
            System.err.println("테스트 실패: 다른 사용자 카테고리 접근 - 예외가 발생하지 않았습니다.");
        } catch (IllegalArgumentException e) {
            // 예상된 예외이므로 테스트 통과
            System.out.println("테스트 통과: 다른 사용자 카테고리 접근 시 예외 발생 - " + e.getMessage());
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 다른 사용자 카테고리 접근 - 예상치 않은 예외 발생 - " + e.getMessage());
        }
    }
    
    private static void testFindCategory() {
        printTestHeader("카테고리 조회 테스트");
        
        // 테스트 케이스 1: ID로 카테고리 조회
        totalTests++;
        try {
            // 조회할 카테고리 생성
            String categoryName = "조회 테스트 카테고리";
            Category createdCategory = categoryService.create(categoryName, TEST_USER_ID, null);
            
            // ID로 조회
            Category foundCategory = categoryService.findById(createdCategory.getId());
            
            assertNotNull("카테고리를 찾았습니다.", foundCategory);
            assertEquals("카테고리 ID가 일치합니다.", createdCategory.getId(), foundCategory.getId());
            assertEquals("카테고리 이름이 일치합니다.", categoryName, foundCategory.getName());
            
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: ID로 카테고리 조회 - " + e.getMessage());
        }
        
        // 테스트 케이스 2: 존재하지 않는 ID로 조회 시 예외 발생
        totalTests++;
        try {
            categoryService.findById(-1L);
            System.err.println("테스트 실패: 존재하지 않는 카테고리 조회 - 예외가 발생하지 않았습니다.");
        } catch (IllegalArgumentException e) {
            // 예상된 예외이므로 테스트 통과
            System.out.println("테스트 통과: 존재하지 않는 카테고리 조회 시 예외 발생 - " + e.getMessage());
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 존재하지 않는 카테고리 조회 - 예상치 않은 예외 발생 - " + e.getMessage());
        }
    }
    
    private static void testFindRootCategories() {
        printTestHeader("최상위 카테고리 조회 테스트");
        
        // 테스트 케이스 1: 사용자의 최상위 카테고리 조회
        totalTests++;
        try {
            // 추가 최상위 카테고리 생성
            categoryService.create("추가 최상위 카테고리", TEST_USER_ID, null);
            
            // 최상위 카테고리 조회
            List<Category> rootCategories = categoryService.findByRootCategory(TEST_USER_ID);
            
            assertNotNull("최상위 카테고리 목록이 반환되었습니다.", rootCategories);
            assertFalse("최상위 카테고리가 1개 이상 있습니다.", rootCategories.isEmpty());
            
            // 모든 카테고리가 최상위인지 확인
            boolean allRoot = true;
            for (Category category : rootCategories) {
                if (category.getTargetCategoryId() != null) {
                    allRoot = false;
                    break;
                }
            }
            assertTrue("모든 카테고리가 최상위입니다.", allRoot);
            
            // 모든 카테고리가 해당 사용자의 것인지 확인
            boolean allBelongToUser = true;
            for (Category category : rootCategories) {
                if (!TEST_USER_ID.equals(category.getUserId())) {
                    allBelongToUser = false;
                    break;
                }
            }
            assertTrue("모든 카테고리가 해당 사용자의 것입니다.", allBelongToUser);
            
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 최상위 카테고리 조회 - " + e.getMessage());
        }
    }
    
    private static void testFindSubCategories() {
        printTestHeader("하위 카테고리 조회 테스트");
        
        // 테스트 케이스 1: 특정 카테고리의 하위 카테고리 조회
        totalTests++;
        try {
            // 상위 카테고리 생성
            Category parentCategory = categoryService.create("하위 카테고리 테스트용 상위 카테고리", TEST_USER_ID, null);
            
            // 하위 카테고리 여러 개 생성
            categoryService.create("하위 카테고리 1", TEST_USER_ID, parentCategory.getId());
            categoryService.create("하위 카테고리 2", TEST_USER_ID, parentCategory.getId());
            
            // 하위 카테고리 조회
            List<Category> subCategories = categoryService.findByChild(parentCategory.getId());
            
            assertNotNull("하위 카테고리 목록이 반환되었습니다.", subCategories);
            assertFalse("하위 카테고리가 있습니다.", subCategories.isEmpty());
            assertTrue("하위 카테고리가 2개 있습니다.", subCategories.size() >= 2);
            
            // 모든 카테고리가 해당 상위 카테고리의 하위인지 확인
            boolean allSubcategories = true;
            for (Category category : subCategories) {
                if (!parentCategory.getId().equals(category.getTargetCategoryId())) {
                    allSubcategories = false;
                    break;
                }
            }
            assertTrue("모든 카테고리가 하위 카테고리입니다.", allSubcategories);
            
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 하위 카테고리 조회 - " + e.getMessage());
        }
    }
    
    private static void testUpdateCategory() {
        printTestHeader("카테고리 수정 테스트");
        
        // 테스트 케이스 1: 카테고리 이름 수정
        totalTests++;
        try {
            // 수정할 카테고리 생성
            Category category = categoryService.create("수정 전 카테고리", TEST_USER_ID, null);
            
            String newName = "수정 후 카테고리";
            Category updatedCategory = categoryService.update(newName, category.getId(), TEST_USER_ID);
            
            assertNotNull("수정된 카테고리 객체가 반환되었습니다.", updatedCategory);
            assertEquals("카테고리 이름이 수정되었습니다.", newName, updatedCategory.getName());
            assertEquals("카테고리 ID는 동일합니다.", category.getId(), updatedCategory.getId());
            
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 카테고리 이름 수정 - " + e.getMessage());
        }
        
        // 테스트 케이스 2: 다른 사용자의 카테고리 수정 시도 시 예외 발생
        totalTests++;
        try {
            // 다른 사용자의 카테고리 생성
            Category otherUserCategory = new Category("다른 사용자의 수정 테스트 카테고리", 1, "other_user", null);
            otherUserCategory = categoryRepository.save(otherUserCategory);
            
            // 다른 사용자의 카테고리 수정 시도
            categoryService.update("수정 시도", otherUserCategory.getId(), TEST_USER_ID);
            System.err.println("테스트 실패: 다른 사용자 카테고리 수정 - 예외가 발생하지 않았습니다.");
        } catch (IllegalArgumentException e) {
            // 예상된 예외이므로 테스트 통과
            System.out.println("테스트 통과: 다른 사용자 카테고리 수정 시 예외 발생 - " + e.getMessage());
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 다른 사용자 카테고리 수정 - 예상치 않은 예외 발생 - " + e.getMessage());
        }
    }
    
    private static void testDeleteCategory() {
        printTestHeader("카테고리 삭제 테스트");
        
        // 테스트 케이스 1: 하위 카테고리가 없는 카테고리 삭제
        totalTests++;
        try {
            // 삭제할 카테고리 생성
            Category category = categoryService.create("삭제할 카테고리", TEST_USER_ID, null);
            
            // 카테고리 삭제
            categoryService.delete(category.getId(), TEST_USER_ID);
            
            // 삭제 확인 - findById 시 예외 발생해야 함
            try {
                categoryService.findById(category.getId());
                System.err.println("테스트 실패: 카테고리 삭제 - 삭제 후에도 카테고리가 존재합니다.");
            } catch (IllegalArgumentException e) {
                // 예상된 예외이므로 삭제 성공
                passedTests++;
            }
        } catch (Exception e) {
            System.err.println("테스트 실패: 카테고리 삭제 - " + e.getMessage());
        }
        
        // 테스트 케이스 2: 하위 카테고리가 있는 카테고리 삭제 시도 시 예외 발생
        totalTests++;
        try {
            // 상위 카테고리 생성
            Category parentCategory = categoryService.create("삭제 실패 테스트용 상위 카테고리", TEST_USER_ID, null);
            
            // 하위 카테고리 생성
            categoryService.create("삭제 실패 테스트용 하위 카테고리", TEST_USER_ID, parentCategory.getId());
            
            // 상위 카테고리 삭제 시도
            categoryService.delete(parentCategory.getId(), TEST_USER_ID);
            System.err.println("테스트 실패: 하위 카테고리 있는 카테고리 삭제 - 예외가 발생하지 않았습니다.");
        } catch (IllegalArgumentException e) {
            // 예상된 예외이므로 테스트 통과
            System.out.println("테스트 통과: 하위 카테고리 있는 카테고리 삭제 시 예외 발생 - " + e.getMessage());
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 하위 카테고리 있는 카테고리 삭제 - 예상치 않은 예외 발생 - " + e.getMessage());
        }
        
        // 테스트 케이스 3: 다른 사용자의 카테고리 삭제 시도 시 예외 발생
        totalTests++;
        try {
            // 다른 사용자의 카테고리 생성
            Category otherUserCategory = new Category("다른 사용자의 삭제 테스트 카테고리", 1, "other_user", null);
            otherUserCategory = categoryRepository.save(otherUserCategory);
            
            // 다른 사용자의 카테고리 삭제 시도
            categoryService.delete(otherUserCategory.getId(), TEST_USER_ID);
            System.err.println("테스트 실패: 다른 사용자 카테고리 삭제 - 예외가 발생하지 않았습니다.");
        } catch (IllegalArgumentException e) {
            // 예상된 예외이므로 테스트 통과
            System.out.println("테스트 통과: 다른 사용자 카테고리 삭제 시 예외 발생 - " + e.getMessage());
            passedTests++;
        } catch (Exception e) {
            System.err.println("테스트 실패: 다른 사용자 카테고리 삭제 - 예상치 않은 예외 발생 - " + e.getMessage());
        }
    }
    
    private static void cleanupTestData() {
        System.out.println("테스트 데이터 정리 중...");
        try {
            // 생성된 모든 테스트 카테고리 삭제
            List<Category> categories = categoryRepository.findAll();
            
            // 먼저 하위 카테고리부터 삭제
            for (Category category : categories) {
                if (category.getTargetCategoryId() != null && 
                    TEST_USER_ID.equals(category.getUserId())) {
                    try {
                        categoryRepository.deleteById(category.getId());
                    } catch (Exception e) {
                        // 무시
                    }
                }
            }
            
            // 그 다음 최상위 카테고리 삭제
            categories = categoryRepository.findAll();
            for (Category category : categories) {
                if (TEST_USER_ID.equals(category.getUserId())) {
                    try {
                        categoryRepository.deleteById(category.getId());
                    } catch (Exception e) {
                        // 무시
                    }
                }
            }
            
            // 다른 사용자의 테스트 카테고리 삭제
            categories = categoryRepository.findAll();
            for (Category category : categories) {
                if ("other_user".equals(category.getUserId())) {
                    try {
                        categoryRepository.deleteById(category.getId());
                    } catch (Exception e) {
                        // 무시
                    }
                }
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
        if (expected == null && actual == null) {
            System.out.println("확인: " + message);
            return;
        }
        if (expected == null || actual == null || !expected.equals(actual)) {
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
    
    private static void assertNull(String message, Object obj) {
        if (obj != null) {
            throw new AssertionError(message + " - 객체가 null이 아닙니다.: " + obj);
        }
        System.out.println("확인: " + message);
    }
} 