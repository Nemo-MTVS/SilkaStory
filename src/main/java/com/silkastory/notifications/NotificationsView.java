package com.silkastory.notifications;

import com.silkastory.common.RepositoryFactory;
import com.silkastory.subscriptions.SubscriptionsRepository;
import com.silkastory.subscriptions.SubscriptionsService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * 알림 기능에 대한 View 클래스
 * 사용자와 직접 상호작용하여 입력을 받고 결과를 출력
 */
public class NotificationsView {
    private final NotificationsService notificationsService;
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter;

    public NotificationsView() {
        NotificationsRepository notificationsRepository = RepositoryFactory.getRepository(NotificationsRepository.class);
        SubscriptionsRepository subscriptionsRepository = RepositoryFactory.getRepository(SubscriptionsRepository.class);
        SubscriptionsService subscriptionsService = new SubscriptionsService(subscriptionsRepository);
        this.notificationsService = new NotificationsService(notificationsRepository, subscriptionsService);
        this.scanner = new Scanner(System.in);
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 알림 메인 메뉴 실행
     * 
     * @param userId 현재 로그인한 사용자 ID
     */
    public void showMenu(String userId) {
        boolean running = true;
        
        while (running) {
            int unreadCount = notificationsService.countUnreadNotifications(userId);
            System.out.println("\n===== 알림 관리 ===== " + (unreadCount > 0 ? "(" + unreadCount + "개의 읽지 않은 알림)" : ""));
            System.out.println("1. 모든 알림 보기");
            System.out.println("2. 읽지 않은 알림 보기");
            System.out.println("3. 알림 읽음 처리하기");
            System.out.println("4. 모든 알림 읽음 처리하기");
            System.out.println("5. 구독자에게 알림 보내기");
            System.out.println("0. 돌아가기");
            System.out.print("메뉴 선택: ");
            
            int choice = readInt();
            
            switch (choice) {
                case 1:
                    showAllNotifications(userId);
                    break;
                case 2:
                    showUnreadNotifications(userId);
                    break;
                case 3:
                    markNotificationAsRead(userId);
                    break;
                case 4:
                    markAllNotificationsAsRead(userId);
                    break;
                case 5:
                    sendNotificationToSubscribers(userId);
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("잘못된 선택입니다. 다시 시도해주세요.");
            }
        }
    }

    /**
     * 모든 알림 출력
     */
    private void showAllNotifications(String userId) {
        System.out.println("\n===== 모든 알림 =====");
        List<Notifications> notifications = notificationsService.getUserNotifications(userId);
        
        if (notifications.isEmpty()) {
            System.out.println("알림이 없습니다.");
            return;
        }
        
        System.out.println("번호\t상태\t발송 시간\t\t\t내용");
        int count = 1;
        for (Notifications notification : notifications) {
            System.out.printf("%d\t%s\t%s\t%s\n", 
                count++, 
                notification.isState() ? "읽음" : "읽지 않음", 
                notification.getSendDate().format(dateFormatter),
                notification.getMessage());
        }
    }

    /**
     * 읽지 않은 알림 출력
     */
    private void showUnreadNotifications(String userId) {
        System.out.println("\n===== 읽지 않은 알림 =====");
        List<Notifications> notifications = notificationsService.getUnreadNotifications(userId);
        
        if (notifications.isEmpty()) {
            System.out.println("읽지 않은 알림이 없습니다.");
            return;
        }
        
        System.out.println("번호\t발송 시간\t\t\t내용");
        int count = 1;
        for (Notifications notification : notifications) {
            System.out.printf("%d\t%s\t%s\n", 
                count++, 
                notification.getSendDate().format(dateFormatter),
                notification.getMessage());
        }
    }

    /**
     * 특정 알림 읽음 처리
     */
    private void markNotificationAsRead(String userId) {
        System.out.println("\n===== 알림 읽음 처리 =====");
        // 읽지 않은 알림 목록 표시
        List<Notifications> unreadNotifications = notificationsService.getUnreadNotifications(userId);
        
        if (unreadNotifications.isEmpty()) {
            System.out.println("읽지 않은 알림이 없습니다.");
            return;
        }
        
        System.out.println("번호\t발송 시간\t\t\t내용");
        int count = 1;
        for (Notifications notification : unreadNotifications) {
            System.out.printf("%d\t%s\t%s\n", 
                count++, 
                notification.getSendDate().format(dateFormatter),
                notification.getMessage());
        }
        
        System.out.print("읽음 처리할 알림 번호 (0: 취소): ");
        int index = readInt();
        
        if (index == 0) {
            System.out.println("읽음 처리가 취소되었습니다.");
            return;
        }
        
        if (index < 1 || index > unreadNotifications.size()) {
            System.out.println("잘못된 번호입니다.");
            return;
        }
        
        try {
            Long notificationId = unreadNotifications.get(index - 1).getId();
            notificationsService.markAsRead(notificationId, userId);
            System.out.println("알림이 성공적으로 읽음 처리되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("알림 읽음 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 모든 알림 읽음 처리
     */
    private void markAllNotificationsAsRead(String userId) {
        System.out.println("\n===== 모든 알림 읽음 처리 =====");
        
        int unreadCount = notificationsService.countUnreadNotifications(userId);
        if (unreadCount == 0) {
            System.out.println("읽지 않은 알림이 없습니다.");
            return;
        }
        
        System.out.printf("읽지 않은 알림 %d개를 모두 읽음 처리하시겠습니까? (y/n): ", unreadCount);
        String answer = scanner.nextLine().toLowerCase();
        
        if (!answer.equals("y")) {
            System.out.println("모든 알림 읽음 처리가 취소되었습니다.");
            return;
        }
        
        try {
            notificationsService.markAllNotificationsAsRead(userId);
            System.out.println("모든 알림이 성공적으로 읽음 처리되었습니다.");
        } catch (Exception e) {
            System.out.println("알림 읽음 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 구독자에게 알림 보내기
     */
    private void sendNotificationToSubscribers(String userId) {
        System.out.println("\n===== 구독자에게 알림 보내기 =====");
        
        System.out.print("보낼 알림 메시지: ");
        String message = scanner.nextLine();
        
        if (message.isEmpty()) {
            System.out.println("알림 메시지를 입력해주세요.");
            return;
        }
        
        try {
            notificationsService.sendNotificationToSubscribers(message, userId);
            System.out.println("알림이 성공적으로 전송되었습니다.");
        } catch (Exception e) {
            System.out.println("알림 전송 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 정수 입력 받기
     */
    private int readInt() {
        try {
            int value = Integer.parseInt(scanner.nextLine());
            return value;
        } catch (NumberFormatException e) {
            System.out.println("숫자를 입력해주세요.");
            return readInt();
        }
    }
} 