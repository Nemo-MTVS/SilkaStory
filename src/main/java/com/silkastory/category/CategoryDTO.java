package com.silkastory.category;

import java.util.ArrayList;
import java.util.List;

/**
 * 카테고리의 트리 구조를 표현하기 위한 DTO 클래스
 */
public class CategoryDTO {
    private Long id;
    private String name;
    private Integer depth;
    private String userId;
    private Long parentId;
    private List<CategoryDTO> children;

    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.depth = category.getDepth();
        this.userId = category.getUserId();
        this.parentId = category.getTargetCategoryId();
        this.children = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getDepth() {
        return depth;
    }

    public String getUserId() {
        return userId;
    }

    public Long getParentId() {
        return parentId;
    }

    public List<CategoryDTO> getChildren() {
        return children;
    }

    public void addChild(CategoryDTO child) {
        this.children.add(child);
    }
} 