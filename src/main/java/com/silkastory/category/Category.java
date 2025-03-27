package com.silkastory.category;

import com.silkastory.common.Table;

@Table(name = "categories")
public class Category {
    private Long id;
    private String name;
    private Integer depth;
    private String userId;
    private Long targetCategoryId;

    public Category() {}
    public Category(Long id, String name, Integer depth, String userId, Long targetCategoryId) {
        this.id = id;
        this.name = name;
        this.depth = depth;
        this.userId = userId;
        this.targetCategoryId = targetCategoryId;
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

    public Long getTargetCategoryId() {
        return targetCategoryId;
    }
}
