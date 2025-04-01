package com.silkastory.category;

import com.silkastory.View;
import com.silkastory.common.RepositoryFactory;
import com.silkastory.infrastructure.database.JDBCConnection;

import java.util.List;
import java.util.Scanner;

public class CategoryView implements View {
    private static final Scanner scanner = new Scanner(System.in);
    private static final CategoryRepository categoryRepository = RepositoryFactory.getRepository(CategoryRepository.class);
    private static final CategoryService categoryService = new CategoryService(categoryRepository);
    private static final String TEST_USER_ID = "test_user";
    
    private boolean running = false;
    
    @Override
    public void start() {
        running = true;
        System.out.println("카테고리 시뮬레이터를 시작합니다.");
        
        while (running) {
            printMenu();
            int choice = getIntInput("메뉴를 선택하세요: ");
            
            try {
                switch (choice) {
                    case 1:
                        createCategory();
                        break;
                    case 2:
                        findCategoryById();
                        break;
                    case 3:
                        findCategoriesByParent();
                        break;
                    case 4:
                        findRootCategories();
                        break;
                    case 5:
                        updateCategory();
                        break;
                    case 6:
                        deleteCategory();
                        break;
                    case 7:
                        listAllCategories();
                        break;
                    case 0:
                        stop();
                        break;
                    default:
                        System.out.println("잘못된 메뉴 선택입니다. 다시 선택해주세요.");
                }
            } catch (Exception e) {
                System.out.println("오류 발생: " + e.getMessage());
            }
            
            System.out.println();
        }
    }
    
    @Override
    public void stop() {
        running = false;
        System.out.println("카테고리 시뮬레이터를 종료합니다.");
        JDBCConnection.close();
    }
    
    @Override
    public String getName() {
        return "카테고리 시뮬레이터";
    }
    
    private void printMenu() {
        System.out.println("\n=== 카테고리 시뮬레이터 메뉴 ===");
        System.out.println("1. 카테고리 생성");
        System.out.println("2. 카테고리 ID로 조회");
        System.out.println("3. 상위 카테고리 기준 하위 카테고리 조회");
        System.out.println("4. 최상위 카테고리 조회");
        System.out.println("5. 카테고리 수정");
        System.out.println("6. 카테고리 삭제");
        System.out.println("7. 모든 카테고리 목록 조회");
        System.out.println("0. 종료");
    }
    
    private void createCategory() {
        System.out.println("\n=== 카테고리 생성 ===");
        String name = getStringInput("카테고리 이름을 입력하세요: ");
        
        System.out.println("상위 카테고리를 설정하시겠습니까? (Y/N)");
        boolean hasParent = scanner.nextLine().trim().equalsIgnoreCase("Y");
        
        Long parentId = null;
        
        if (hasParent) {
            parentId = getLongInput("상위 카테고리 ID를 입력하세요: ");
            try {
                Category parentCategory = categoryService.findById(parentId);
                System.out.println("상위 카테고리: " + parentCategory.getName() + ", 깊이: " + parentCategory.getDepth());
            } catch (Exception e) {
                System.out.println("상위 카테고리를 찾을 수 없습니다. 루트 카테고리로 생성합니다.");
                parentId = null;
            }
        }
        
        try {
            Category newCategory = categoryService.create(name, TEST_USER_ID, parentId);
            System.out.println("카테고리가 성공적으로 생성되었습니다.");
            printCategory(newCategory);
        } catch (Exception e) {
            System.out.println("카테고리 생성 실패: " + e.getMessage());
        }
    }
    
    private void findCategoryById() {
        System.out.println("\n=== 카테고리 ID로 조회 ===");
        Long id = getLongInput("조회할 카테고리 ID를 입력하세요: ");
        
        try {
            Category category = categoryService.findById(id);
            System.out.println("카테고리를 찾았습니다:");
            printCategory(category);
        } catch (Exception e) {
            System.out.println("카테고리 조회 실패: " + e.getMessage());
        }
    }
    
    private void findCategoriesByParent() {
        System.out.println("\n=== 상위 카테고리 기준 하위 카테고리 조회 ===");
        Long parentId = getLongInput("상위 카테고리 ID를 입력하세요: ");
        
        try {
            List<Category> categories = categoryService.findByParent(parentId);
            System.out.println("하위 카테고리 " + categories.size() + "개를 찾았습니다:");
            
            for (Category category : categories) {
                printCategory(category);
            }
        } catch (Exception e) {
            System.out.println("카테고리 조회 실패: " + e.getMessage());
        }
    }
    
    private void findRootCategories() {
        System.out.println("\n=== 최상위 카테고리 조회 ===");
        
        try {
            List<Category> rootCategories = categoryService.findRootCategories();
            System.out.println("최상위 카테고리 " + rootCategories.size() + "개를 찾았습니다:");
            
            for (Category category : rootCategories) {
                printCategory(category);
            }
        } catch (Exception e) {
            System.out.println("카테고리 조회 실패: " + e.getMessage());
        }
    }
    
    private void updateCategory() {
        System.out.println("\n=== 카테고리 수정 ===");
        Long id = getLongInput("수정할 카테고리 ID를 입력하세요: ");
        
        try {
            Category category = categoryService.findById(id);
            System.out.println("현재 카테고리 정보:");
            printCategory(category);
            
            String newName = getStringInput("새 이름 (변경하지 않으려면 빈칸): ");
            
            if (!newName.isEmpty()) {
                categoryService.updateName(id, newName);
                System.out.println("카테고리 이름이 업데이트되었습니다:");
                printCategory(categoryService.findById(id));
            } else {
                System.out.println("카테고리 정보가 변경되지 않았습니다.");
            }
        } catch (Exception e) {
            System.out.println("카테고리 수정 실패: " + e.getMessage());
        }
    }
    
    private void deleteCategory() {
        System.out.println("\n=== 카테고리 삭제 ===");
        Long id = getLongInput("삭제할 카테고리 ID를 입력하세요: ");
        
        try {
            Category category = categoryService.findById(id);
            System.out.println("다음 카테고리를 삭제합니다:");
            printCategory(category);
            
            System.out.println("정말로 삭제하시겠습니까? (Y/N)");
            boolean confirm = scanner.nextLine().trim().equalsIgnoreCase("Y");
            
            if (confirm) {
                categoryService.delete(id);
                System.out.println("카테고리가 성공적으로 삭제되었습니다.");
            } else {
                System.out.println("카테고리 삭제가 취소되었습니다.");
            }
        } catch (Exception e) {
            System.out.println("카테고리 삭제 실패: " + e.getMessage());
        }
    }
    
    private void listAllCategories() {
        System.out.println("\n=== 모든 카테고리 목록 ===");
        
        try {
            List<Category> categories = categoryService.findAll();
            System.out.println("총 " + categories.size() + "개의 카테고리가 있습니다:");
            
            for (Category category : categories) {
                printCategory(category);
            }
        } catch (Exception e) {
            System.out.println("카테고리 목록 조회 실패: " + e.getMessage());
        }
    }
    
    private void printCategory(Category category) {
        System.out.println("----------------------------------------");
        System.out.println("ID: " + category.getId());
        System.out.println("이름: " + category.getName());
        System.out.println("깊이: " + category.getDepth());
        System.out.println("사용자 ID: " + category.getUserId());
        System.out.println("상위 카테고리 ID: " + (category.getTargetCategoryId() == null ? "없음" : category.getTargetCategoryId()));
        System.out.println("----------------------------------------");
    }
    
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
            }
        }
    }
    
    private long getLongInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
            }
        }
    }
} 