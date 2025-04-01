package com.silkastory.subscriptions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "subscriptions")
public class Subscriptions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "target_id")
    private String targetId;
    
    @Column(name = "is_alram")
    private boolean isAlram = true;
    
    public Subscriptions() {
    }
    
    public Subscriptions(String userId, String targetId) {
        this.userId = userId;
        this.targetId = targetId;
        this.isAlram = true;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getTargetId() {
        return targetId;
    }

    public boolean isAlram() {
        return isAlram;
    }

    public void updateIsAlram(boolean isAlram) {
        this.isAlram = isAlram;
    }
}
