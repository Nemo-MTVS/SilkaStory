package com.silkastory.category;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /*
     * # 카테고리 등록
     * - 자신의 블로그에서 개별적으로 존재하는 카테고리 입니다
     * - 제목과 상위 카테고리의 식별 값, 깊이가 필수입니다
     * - 깊이는 상위 카테고리가 없다면 1 아니면 상위 카테고리의 깊이 + 1
     */
    public Category create(String name, String userId, Long targetCategoryId) {
        if (targetCategoryId != null) {
            Category targetCategory = findById(targetCategoryId);
            if (!targetCategory.getUserId().equals(userId)) {
                throw new IllegalArgumentException(CategoryError.CATEGORY_NOT_BELONG_TO_USER.getMessage());
            }
            Category category = new Category(
                    name,
                    targetCategory.getDepth() + 1,
                    userId,
                    targetCategory.getId()
            );
            return categoryRepository.save(category);
        }
        Category category = new Category(
                name,
                1,
                userId,
                targetCategoryId
        );
        return categoryRepository.save(category);
    }

    /*
     * # 특정 카테고리 조회
     * - 카테고리 조회는 카테고리의 식별 값으로 조회합니다.
     */
    public Category findById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new IllegalArgumentException(CategoryError.CATEGORY_NOT_FOUND.getMessage())
        );
    }

    /*
     * # 카테고리 수정
     * - 카테고리 수정은 카테고리의 식별 값으로 수정합니다.
     * - 카테고리 생성자와 수정요청자는 동일해야합니다.
     * - 카테고리 이름만 수정 가능합니다.
     */
    public Category update(String name, Long categoryId, String userId) {
        Category category = findById(categoryId);
        if (!category.getUserId().equals(userId)) {
            throw new IllegalArgumentException(CategoryError.CATEGORY_NOT_BELONG_TO_USER.getMessage());
        }
        category.updateName(name);
        return categoryRepository.save(category);
    }

    /*
     * # 카테고리 삭제
     * - 카테고리 삭제는 카테고리의 식별 값으로 삭제합니다.
     * - 카테고리 생성자와 삭제요청자는 동일해야합니다.
     * - 하위 카테고리가 있다면 카테고리를 삭제 할 수 없습니다.
     */
    public void delete(Long categoryId, String userId) {
        Category category = findById(categoryId);
        if (!category.getUserId().equals(userId)) {
            throw new IllegalArgumentException(CategoryError.CATEGORY_NOT_BELONG_TO_USER.getMessage());
        }
        if (categoryRepository.existsByTargetCategoryId(category.getId())) {
            throw new IllegalArgumentException(CategoryError.CATEGORY_HAS_CHILDREN.getMessage());
        }
        try{
            categoryRepository.deleteById(category.getId());
        }catch (Exception e){
            throw new IllegalArgumentException(CategoryError.CATEGORY_IS_USED.getMessage());
        }
    }

    // 최상위 카테고리 리스트
    public List<Category> findByRootCategory(String targetUserId) {
        return categoryRepository.findByTargetCategoryIdIsNullAndUserId(targetUserId);
    }

    // 특정 카테고리의 하위 카테고리 리스트
    public List<Category> findByChild(Long parentCategoryId) {
        return categoryRepository.findByTargetCategoryId(parentCategoryId);
    }
    
    // 테스트 코드에서 사용하는 findByParentId 메서드 (findByChild와 동일한 기능)
    public List<Category> findByParentId(Long parentCategoryId) {
        return categoryRepository.findByTargetCategoryId(parentCategoryId);
    }
    
    /**
     * 사용자의 전체 카테고리 트리 구조 조회
     * 
     * @param userId 사용자 ID
     * @return 계층 구조를 가진 카테고리 목록
     */
    public List<CategoryDTO> getCategoryTree(String userId) {
        // 1. 모든 카테고리를 가져와서 DTO로 변환
        List<Category> allCategories = categoryRepository.findAll()
            .stream()
            .filter(category -> category.getUserId().equals(userId))
            .collect(Collectors.toList());
        
        // 2. ID를 키로 하는 맵 생성
        Map<Long, CategoryDTO> categoryMap = new HashMap<>();
        for (Category category : allCategories) {
            categoryMap.put(category.getId(), new CategoryDTO(category));
        }
        
        // 3. 트리 구조 구성
        List<CategoryDTO> rootCategories = new ArrayList<>();
        
        for (Category category : allCategories) {
            CategoryDTO dto = categoryMap.get(category.getId());
            
            if (category.getTargetCategoryId() == null) {
                // 최상위 카테고리인 경우
                rootCategories.add(dto);
            } else {
                // 하위 카테고리인 경우
                CategoryDTO parentDto = categoryMap.get(category.getTargetCategoryId());
                if (parentDto != null) {
                    parentDto.addChild(dto);
                }
            }
        }
        
        return rootCategories;
    }
}
