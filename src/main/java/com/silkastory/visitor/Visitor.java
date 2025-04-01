package com.silkastory.visitor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "visitor")
public class Visitor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "post_id")
    private Long postId;
    
    @Column(name = "visit_date")
    private LocalDateTime visitDate;

    public Visitor(String userId, Long postId, LocalDateTime visitDate) {
        this.userId = userId;
        this.postId = postId;
        this.visitDate = visitDate;
    }

    public Visitor(){}

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public Long getPostId() {
        return postId;
    }

    public LocalDateTime getVisitDate() {
        return visitDate;
    }
}
