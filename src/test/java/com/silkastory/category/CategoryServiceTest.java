package com.silkastory.category;

import com.silkastory.common.RepositoryFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CategoryServiceTest {

    private CategoryRepository categoryRepository;
    private CategoryService categoryService;
    
    private final String TEST_USER_ID = "test_user";
    private final String ROOT_NAME = "루트 카테고리";
    private final String SUB_NAME = "서브 카테고리";
    private final String UPDATE_NAME = "수정된 카테고리";
    
    // 테스트에서 생성한 카테고리 ID를 저장하여 나중에 정리할 수 있게 함
    private final List<Long> testCategoryIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // 실제 DB 레포지토리를 생성
        categoryRepository = RepositoryFactory.getRepository(CategoryRepository.class);
        categoryService = new CategoryService(categoryRepository);
    }
    
    @AfterEach
    void tearDown() {
        // 테스트에서 생성한 카테고리 정리
        for (Long id : testCategoryIds) {
            try {
                categoryRepository.deleteById(id);
            } catch (Exception e) {
                System.err.println("카테고리 삭제 실패: " + id + ", " + e.getMessage());
            }
        }
        testCategoryIds.clear();
    }
    
    // 테스트에서 생성된 카테고리 ID를 추적하는 헬퍼 메소드
    private void trackCategory(Category category) {
        if (category != null && category.getId() != null) {
            testCategoryIds.add(category.getId());
        }
    }

    @Test
    @DisplayName("루트 카테고리 생성 테스트")
    void createRootCategory() {
        // when
        Category rootCategory = categoryService.create(ROOT_NAME, TEST_USER_ID, null);
        trackCategory(rootCategory);
        
        // then
        assertNotNull(rootCategory);
        assertNotNull(rootCategory.getId());
        assertEquals(ROOT_NAME, rootCategory.getName());
        assertEquals(1, rootCategory.getDepth());
        assertNull(rootCategory.getTargetCategoryId());
        
        // 저장소에서 조회하여 확인
        Category savedCategory = categoryRepository.findById(rootCategory.getId()).orElse(null);
        assertNotNull(savedCategory);
        assertEquals(ROOT_NAME, savedCategory.getName());
    }
    
    @Test
    @DisplayName("서브 카테고리 생성 테스트")
    void createSubCategory() {
        // given
        Category rootCategory = categoryService.create(ROOT_NAME, TEST_USER_ID, null);
        trackCategory(rootCategory);
        
        // when
        Category subCategory = categoryService.create(SUB_NAME, TEST_USER_ID, rootCategory.getId());
        trackCategory(subCategory);
        
        // then
        assertNotNull(subCategory);
        assertEquals(SUB_NAME, subCategory.getName());
        assertEquals(2, subCategory.getDepth());
        assertEquals(rootCategory.getId(), subCategory.getTargetCategoryId());
        
        // 저장소에서 조회하여 확인
        Category savedCategory = categoryRepository.findById(subCategory.getId()).orElse(null);
        assertNotNull(savedCategory);
        assertEquals(SUB_NAME, savedCategory.getName());
        assertEquals(rootCategory.getId(), savedCategory.getTargetCategoryId());
    }
    
    @Test
    @DisplayName("존재하지 않는 부모 카테고리로 서브 카테고리 생성 시 예외 발생 테스트")
    void createSubCategoryWithNonExistentParent() {
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.create(SUB_NAME, TEST_USER_ID, 999L);
        });
        assertTrue(exception.getMessage().contains(CategoryError.CATEGORY_NOT_FOUND.getMessage()));
    }
    
    @Test
    @DisplayName("카테고리 조회 테스트")
    void findById() {
        // given
        Category rootCategory = categoryService.create(ROOT_NAME, TEST_USER_ID, null);
        trackCategory(rootCategory);
        
        // when
        Category foundCategory = categoryService.findById(rootCategory.getId());
        
        // then
        assertNotNull(foundCategory);
        assertEquals(rootCategory.getId(), foundCategory.getId());
        assertEquals(ROOT_NAME, foundCategory.getName());
    }
    
    @Test
    @DisplayName("존재하지 않는 카테고리 조회 시 예외 발생 테스트")
    void findByIdNonExistent() {
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.findById(999L);
        });
        
        assertTrue(exception.getMessage().contains(CategoryError.CATEGORY_NOT_FOUND.getMessage()));
    }
    
    @Test
    @DisplayName("루트 카테고리 목록 조회 테스트")
    void findByRootCategory() {
        // given
        Category rootCategory1 = categoryService.create("루트1", TEST_USER_ID, null);
        trackCategory(rootCategory1);
        Category rootCategory2 = categoryService.create("루트2", TEST_USER_ID, null);
        trackCategory(rootCategory2);
        Category subCategory = categoryService.create("서브1", TEST_USER_ID, rootCategory1.getId());
        trackCategory(subCategory);
        
        // when
        List<Category> rootCategories = categoryService.findByRootCategory(TEST_USER_ID);
        
        // then
        assertNotNull(rootCategories);
        assertTrue(rootCategories.size() >= 2);
        assertTrue(rootCategories.stream().anyMatch(c -> c.getName().equals("루트1")));
        assertTrue(rootCategories.stream().anyMatch(c -> c.getName().equals("루트2")));
    }
    
    @Test
    @DisplayName("특정 부모 카테고리의 하위 카테고리 목록 조회 테스트")
    void findByParentId() {
        // given
        Category rootCategory = categoryService.create(ROOT_NAME, TEST_USER_ID, null);
        trackCategory(rootCategory);
        Category subCategory1 = categoryService.create("서브1", TEST_USER_ID, rootCategory.getId());
        trackCategory(subCategory1);
        Category subCategory2 = categoryService.create("서브2", TEST_USER_ID, rootCategory.getId());
        trackCategory(subCategory2);
        
        // when
        List<Category> subCategories = categoryService.findByChild(rootCategory.getId());
        
        // then
        assertNotNull(subCategories);
        assertEquals(2, subCategories.size());
        assertTrue(subCategories.stream().allMatch(c -> c.getTargetCategoryId() != null 
                && c.getTargetCategoryId().equals(rootCategory.getId())));
    }
    
    @Test
    @DisplayName("카테고리 수정 테스트")
    void update() {
        // given
        Category category = categoryService.create(ROOT_NAME, TEST_USER_ID, null);
        trackCategory(category);
        
        // when
        Category updatedCategory = categoryService.update(UPDATE_NAME, category.getId(), TEST_USER_ID);
        
        // then
        assertNotNull(updatedCategory);
        assertEquals(category.getId(), updatedCategory.getId());
        assertEquals(UPDATE_NAME, updatedCategory.getName());
        
        // 저장소에서 조회하여 확인
        Category savedCategory = categoryRepository.findById(category.getId()).orElse(null);
        assertNotNull(savedCategory);
        assertEquals(UPDATE_NAME, savedCategory.getName());
    }
    
    @Test
    @DisplayName("존재하지 않는 카테고리 수정 시 예외 발생 테스트")
    void updateNonExistent() {
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.update(UPDATE_NAME, 999L, TEST_USER_ID);
        });
        
        assertTrue(exception.getMessage().contains(CategoryError.CATEGORY_NOT_FOUND.getMessage()));
    }
    
    @Test
    @DisplayName("카테고리 삭제 테스트")
    void delete() {
        // given
        Category category = categoryService.create(ROOT_NAME, TEST_USER_ID, null);
        Long categoryId = category.getId();
        
        // when
        categoryService.delete(categoryId, TEST_USER_ID);
        
        // then
        Optional<Category> deletedCategory = categoryRepository.findById(categoryId);
        assertFalse(deletedCategory.isPresent());
        
        // 이미 삭제되었으므로 추적 목록에서 제거
        testCategoryIds.remove(categoryId);
    }
    
    @Test
    @DisplayName("하위 카테고리가 있는 카테고리 삭제 시 예외 발생 테스트")
    void deleteWithSubCategories() {
        // given
        Category rootCategory = categoryService.create(ROOT_NAME, TEST_USER_ID, null);
        trackCategory(rootCategory);
        Category subCategory = categoryService.create(SUB_NAME, TEST_USER_ID, rootCategory.getId());
        trackCategory(subCategory);
        
        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.delete(rootCategory.getId(), TEST_USER_ID);
        });
        
        assertTrue(exception.getMessage().contains(CategoryError.CATEGORY_HAS_CHILDREN.getMessage()));
    }
} 