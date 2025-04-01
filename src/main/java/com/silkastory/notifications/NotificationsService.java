package com.silkastory.notifications;

import com.silkastory.subscriptions.Subscriptions;
import com.silkastory.subscriptions.SubscriptionsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationsService {
    private final NotificationsRepository notificationsRepository;
    private final SubscriptionsService subscriptionsService;

    public NotificationsService(NotificationsRepository notificationsRepository, SubscriptionsService subscriptionsService) {
        this.notificationsRepository = notificationsRepository;
        this.subscriptionsService = subscriptionsService;
    }

    /**
     * 새 알림 생성
     */
    public Notifications createNotification(String message, String userId) {
        Notifications notification = new Notifications(message, userId, LocalDateTime.now());
        return notificationsRepository.save(notification);
    }

    /**
     * 여러 구독자에게 일괄 알림 전송
     */
    public void sendNotificationToSubscribers(String message, String publisherUserId) {
        // 알림을 받기로 설정한 구독자 목록 조회
        List<Subscriptions> subscribers = subscriptionsService.getNotifiableSubscribers(publisherUserId);
        
        // 각 구독자에게 알림 전송
        for (Subscriptions subscription : subscribers) {
            createNotification(message, subscription.getUserId());
        }
    }

    /**
     * 구독자에게 알림 전송하고 생성된 알림 목록 반환
     */
    public List<Notifications> notifySubscribers(String message, String publisherUserId) {
        // 알림을 받기로 설정한 구독자 목록 조회
        List<Subscriptions> subscribers = subscriptionsService.getNotifiableSubscribers(publisherUserId);
        List<Notifications> notifications = new ArrayList<>();
        
        // 각 구독자에게 알림 전송하고 생성된 알림 추적
        for (Subscriptions subscription : subscribers) {
            Notifications notification = createNotification(message, subscription.getUserId());
            notifications.add(notification);
        }
        
        return notifications;
    }

    /**
     * 특정 사용자의 알림 목록 조회
     */
    public List<Notifications> getUserNotifications(String userId) {
        return notificationsRepository.findByUserId(userId);
    }

    /**
     * 특정 사용자의 읽지 않은 알림 목록 조회
     */
    public List<Notifications> getUnreadNotifications(String userId) {
        return notificationsRepository.findByUserIdAndStateFalse(userId);
    }

    /**
     * 특정 알림을 읽음 처리하는 메서드
     */
    public Notifications markAsRead(Long notificationId, String userId) {
        Notifications notification = notificationsRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException(NotificationsError.NOTIFICATION_NOT_FOUND.getMessage()));
        
        if (!notification.getUserId().equals(userId)) {
            throw new IllegalArgumentException(NotificationsError.NOT_YOUR_NOTIFICATION.getMessage());
        }
        
        notification.markAsRead();
        return notificationsRepository.save(notification);
    }

    /**
     * 알림 읽음 처리
     */
    public Notifications markNotificationAsRead(Long notificationId) {
        Notifications notification = notificationsRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        
        notification.markAsRead();
        return notificationsRepository.save(notification);
    }

    /**
     * 사용자의 모든 알림 읽음 처리
     */
    public void markAllNotificationsAsRead(String userId) {
        List<Notifications> unreadNotifications = notificationsRepository.findByUserIdAndStateFalse(userId);
        
        for (Notifications notification : unreadNotifications) {
            notification.markAsRead();
            notificationsRepository.save(notification);
        }
    }

    /**
     * 알림 개수 조회
     */
    public int countNotifications(String userId) {
        return notificationsRepository.countByUserId(userId);
    }

    /**
     * 읽지 않은 알림 개수 조회
     */
    public int countUnreadNotifications(String userId) {
        return notificationsRepository.countByUserIdAndStateFalse(userId);
    }
} 