package com.silkastory.category;

import java.util.List;
import java.util.Scanner;

/**
 * 카테고리 기능에 대한 View 클래스
 * 사용자와 직접 상호작용하여 입력을 받고 결과를 출력
 */
public class CategoryView {
    private final CategoryService categoryService;
    private final Scanner scanner;

    public CategoryView() {
        CategoryRepository categoryRepository = new CategoryRepositoryImpl();
        this.categoryService = new CategoryService(categoryRepository);
        this.scanner = new Scanner(System.in);
    }

    /**
     * 카테고리 메인 메뉴 실행
     * 
     * @param userId 현재 로그인한 사용자 ID
     */
    public void showMenu(String userId) {
        boolean running = true;
        
        while (running) {
            System.out.println("\n===== 카테고리 관리 =====");
            System.out.println("1. 카테고리 트리 보기");
            System.out.println("2. 최상위 카테고리 목록 보기");
            System.out.println("3. 하위 카테고리 목록 보기");
            System.out.println("4. 카테고리 추가하기");
            System.out.println("5. 카테고리 수정하기");
            System.out.println("6. 카테고리 삭제하기");
            System.out.println("0. 돌아가기");
            System.out.print("메뉴 선택: ");
            
            int choice = readInt();
            
            switch (choice) {
                case 1:
                    showCategoryTree(userId);
                    break;
                case 2:
                    showRootCategories(userId);
                    break;
                case 3:
                    showChildCategories();
                    break;
                case 4:
                    addCategory(userId);
                    break;
                case 5:
                    updateCategory(userId);
                    break;
                case 6:
                    deleteCategory(userId);
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("잘못된 선택입니다. 다시 시도해주세요.");
            }
        }
    }

    /**
     * 카테고리 트리 구조 출력
     */
    private void showCategoryTree(String userId) {
        System.out.println("\n===== 카테고리 트리 =====");
        try {
            List<CategoryDTO> categoryTree = categoryService.getCategoryTree(userId);
            
            if (categoryTree.isEmpty()) {
                System.out.println("카테고리가 없습니다.");
                return;
            }
            
            printCategoryTree(categoryTree, 0);
            
        } catch (Exception e) {
            System.out.println("카테고리 트리를 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 카테고리 트리 재귀적 출력
     */
    private void printCategoryTree(List<CategoryDTO> categories, int level) {
        String indent = "  ".repeat(level);
        
        for (CategoryDTO category : categories) {
            System.out.println(indent + "ㄴ " + category.getName() + " (ID: " + category.getId() + ")");
            printCategoryTree(category.getChildren(), level + 1);
        }
    }

    /**
     * 최상위 카테고리 목록 출력
     */
    private void showRootCategories(String userId) {
        System.out.println("\n===== 최상위 카테고리 목록 =====");
        List<Category> rootCategories = categoryService.findByRootCategory(userId);
        
        if (rootCategories.isEmpty()) {
            System.out.println("최상위 카테고리가 없습니다.");
            return;
        }
        
        System.out.println("ID\t깊이\t이름");
        for (Category category : rootCategories) {
            System.out.printf("%d\t%d\t%s\n", 
                category.getId(),
                category.getDepth(),
                category.getName());
        }
    }

    /**
     * 하위 카테고리 목록 출력
     */
    private void showChildCategories() {
        System.out.println("\n===== 하위 카테고리 목록 =====");
        System.out.print("상위 카테고리 ID: ");
        Long parentId = readLong();
        
        try {
            // 상위 카테고리 정보 출력
            try {
                Category parentCategory = categoryService.findById(parentId);
                System.out.println("상위 카테고리: " + parentCategory.getName() + " (ID: " + parentCategory.getId() + ")");
            } catch (IllegalArgumentException e) {
                System.out.println("해당 ID의 카테고리가 존재하지 않습니다.");
                return;
            }
            
            List<Category> childCategories = categoryService.findByChild(parentId);
            
            if (childCategories.isEmpty()) {
                System.out.println("하위 카테고리가 없습니다.");
                return;
            }
            
            System.out.println("ID\t깊이\t이름");
            for (Category category : childCategories) {
                System.out.printf("%d\t%d\t%s\n", 
                    category.getId(),
                    category.getDepth(),
                    category.getName());
            }
        } catch (Exception e) {
            System.out.println("하위 카테고리를 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 카테고리 추가
     */
    private void addCategory(String userId) {
        System.out.println("\n===== 카테고리 추가 =====");
        
        System.out.print("카테고리 이름: ");
        String name = scanner.nextLine();
        
        System.out.print("상위 카테고리 ID (최상위면 그냥 엔터): ");
        String parentIdStr = scanner.nextLine();
        Long parentId = parentIdStr.isEmpty() ? null : Long.parseLong(parentIdStr);
        
        try {
            Category newCategory = categoryService.create(name, userId, parentId);
            System.out.println("카테고리가 성공적으로 추가되었습니다. ID: " + newCategory.getId());
        } catch (IllegalArgumentException e) {
            System.out.println("카테고리 추가 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 카테고리 수정
     */
    private void updateCategory(String userId) {
        System.out.println("\n===== 카테고리 수정 =====");
        
        System.out.print("수정할 카테고리 ID: ");
        Long categoryId = readLong();
        
        try {
            // 기존 카테고리 정보 출력
            try {
                Category category = categoryService.findById(categoryId);
                System.out.println("현재 이름: " + category.getName());
                
                if (!category.getUserId().equals(userId)) {
                    System.out.println("해당 카테고리에 접근 권한이 없습니다.");
                    return;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("해당 ID의 카테고리가 존재하지 않습니다.");
                return;
            }
            
            System.out.print("새 카테고리 이름: ");
            String name = scanner.nextLine();
            
            Category updatedCategory = categoryService.update(name, categoryId, userId);
            System.out.println("카테고리가 성공적으로 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("카테고리 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 카테고리 삭제
     */
    private void deleteCategory(String userId) {
        System.out.println("\n===== 카테고리 삭제 =====");
        
        System.out.print("삭제할 카테고리 ID: ");
        Long categoryId = readLong();
        
        try {
            // 기존 카테고리 정보 출력
            try {
                Category category = categoryService.findById(categoryId);
                System.out.println("삭제할 카테고리: " + category.getName() + " (ID: " + category.getId() + ")");
                
                if (!category.getUserId().equals(userId)) {
                    System.out.println("해당 카테고리에 접근 권한이 없습니다.");
                    return;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("해당 ID의 카테고리가 존재하지 않습니다.");
                return;
            }
            
            System.out.print("정말로 삭제하시겠습니까? (y/n): ");
            String confirm = scanner.nextLine().toLowerCase();
            
            if (confirm.equals("y")) {
                categoryService.delete(categoryId, userId);
                System.out.println("카테고리가 성공적으로 삭제되었습니다.");
            } else {
                System.out.println("카테고리 삭제가 취소되었습니다.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("카테고리 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 정수 입력 받기
     */
    private int readInt() {
        try {
            int value = Integer.parseInt(scanner.nextLine());
            return value;
        } catch (NumberFormatException e) {
            System.out.println("숫자를 입력해주세요.");
            return readInt();
        }
    }
    
    /**
     * 롱 정수 입력 받기
     */
    private Long readLong() {
        try {
            Long value = Long.parseLong(scanner.nextLine());
            return value;
        } catch (NumberFormatException e) {
            System.out.println("숫자를 입력해주세요.");
            return readLong();
        }
    }
} 