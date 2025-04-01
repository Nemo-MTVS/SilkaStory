package com.silkastory.category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "depth")
    private Integer depth;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "target_category_id")
    private Long targetCategoryId;

    public Category() {}
    public Category(String name, Integer depth, String userId, Long targetCategoryId) {
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

    public void updateName(String name) {
        this.name = name;
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
