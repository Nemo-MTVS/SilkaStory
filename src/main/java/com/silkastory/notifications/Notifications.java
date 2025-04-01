package com.silkastory.notifications;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notifications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message")
    private String message;

    @Column(name = "state")
    private boolean state = false;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "send_date")
    private LocalDateTime sendDate;


    public Notifications(String message, String userId, LocalDateTime sendDate) {
        this.message = message;
        this.userId = userId;
        this.sendDate = sendDate;
    }
    
    public Notifications() {
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public boolean isState() {
        return state;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getSendDate() {
        return sendDate;
    }
    
    /**
     * 알림 읽음 상태로 변경
     */
    public void markAsRead() {
        this.state = true;
    }
}
