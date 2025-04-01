package com.silkastory.notifications;

/**
 * 알림 관련 오류 코드
 */
public enum NotificationsError {
    NOTIFICATION_NOT_FOUND("알림을 찾을 수 없습니다"),
    NOT_YOUR_NOTIFICATION("자신의 알림만 읽음 처리할 수 있습니다");
    
    private final String message;
    
    NotificationsError(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
} 