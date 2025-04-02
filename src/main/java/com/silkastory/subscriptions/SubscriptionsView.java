package com.silkastory.subscriptions;

import com.silkastory.common.RepositoryFactory;

import java.util.List;
import java.util.Scanner;

/**
 * 구독 기능에 대한 View 클래스
 * 사용자와 직접 상호작용하여 입력을 받고 결과를 출력
 */
public class SubscriptionsView {
    private final SubscriptionsService subscriptionsService;
    private final Scanner scanner;

    public SubscriptionsView() {
        SubscriptionsRepository subscriptionsRepository = RepositoryFactory.getRepository(SubscriptionsRepository.class);
        this.subscriptionsService = new SubscriptionsService(subscriptionsRepository);
        this.scanner = new Scanner(System.in);
    }

    /**
     * 구독 메인 메뉴 실행
     * 
     * @param userId 현재 로그인한 사용자 ID
     */
    public void showMenu(String userId) {
        boolean running = true;
        
        while (running) {
            System.out.println("\n===== 구독 관리 =====");
            System.out.println("1. 내가 구독한 목록 보기");
            System.out.println("2. 나를 구독한 목록 보기");
            System.out.println("3. 새 구독 추가하기");
            System.out.println("4. 구독 취소하기");
            System.out.println("5. 구독 알림 설정 변경하기");
            System.out.println("0. 돌아가기");
            System.out.print("메뉴 선택: ");
            
            int choice = readInt();
            
            switch (choice) {
                case 1:
                    showMySubscriptions(userId);
                    break;
                case 2:
                    showMySubscribers(userId);
                    break;
                case 3:
                    subscribe(userId);
                    break;
                case 4:
                    unsubscribe(userId);
                    break;
                case 5:
                    updateNotificationSetting(userId);
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
     * 내가 구독한 목록 출력
     */
    private void showMySubscriptions(String userId) {
        System.out.println("\n===== 내가 구독한 목록 =====");
        List<Subscriptions> subscriptions = subscriptionsService.getSubscriptionsBySubscriber(userId);
        
        if (subscriptions.isEmpty()) {
            System.out.println("구독한 사용자가 없습니다.");
            return;
        }
        
        System.out.println("번호\t구독 대상\t알림 설정");
        int count = 1;
        for (Subscriptions subscription : subscriptions) {
            System.out.printf("%d\t%s\t%s\n", 
                count++, 
                subscription.getTargetId(), 
                subscription.isAlram() ? "켜짐" : "꺼짐");
        }
    }

    /**
     * 나를 구독한 목록 출력
     */
    private void showMySubscribers(String userId) {
        System.out.println("\n===== 나를 구독한 목록 =====");
        List<Subscriptions> subscribers = subscriptionsService.getSubscribersByCreator(userId);
        
        if (subscribers.isEmpty()) {
            System.out.println("구독자가 없습니다.");
            return;
        }
        
        System.out.println("번호\t구독자\t알림 설정");
        int count = 1;
        for (Subscriptions subscriber : subscribers) {
            System.out.printf("%d\t%s\t%s\n", 
                count++, 
                subscriber.getUserId(), 
                subscriber.isAlram() ? "켜짐" : "꺼짐");
        }
    }

    /**
     * 새 구독 추가
     */
    private void subscribe(String userId) {
        System.out.println("\n===== 새 구독 추가 =====");
        
        System.out.print("구독할 사용자 ID: ");
        String targetId = scanner.nextLine();
        
        try {
            if (userId.equals(targetId)) {
                System.out.println("자기 자신을 구독할 수 없습니다.");
                return;
            }
            
            if (subscriptionsService.isSubscribed(userId, targetId)) {
                System.out.println("이미 구독 중인 사용자입니다.");
                return;
            }
            
            Subscriptions subscription = subscriptionsService.subscribe(userId, targetId);
            System.out.println("구독이 성공적으로 추가되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("구독 추가 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 구독 취소
     */
    private void unsubscribe(String userId) {
        System.out.println("\n===== 구독 취소 =====");
        
        // 현재 구독 목록 표시
        List<Subscriptions> subscriptions = subscriptionsService.getSubscriptionsBySubscriber(userId);
        if (subscriptions.isEmpty()) {
            System.out.println("구독한 사용자가 없습니다.");
            return;
        }
        
        System.out.println("번호\t구독 대상");
        int count = 1;
        for (Subscriptions subscription : subscriptions) {
            System.out.printf("%d\t%s\n", count++, subscription.getTargetId());
        }
        
        System.out.print("취소할 구독 번호: ");
        int index = readInt() - 1;
        
        if (index < 0 || index >= subscriptions.size()) {
            System.out.println("잘못된 번호입니다.");
            return;
        }
        
        try {
            String targetId = subscriptions.get(index).getTargetId();
            subscriptionsService.unsubscribe(userId, targetId);
            System.out.println("구독이 성공적으로 취소되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("구독 취소 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 구독 알림 설정 변경
     */
    private void updateNotificationSetting(String userId) {
        System.out.println("\n===== 구독 알림 설정 변경 =====");
        
        // 현재 구독 목록 표시
        List<Subscriptions> subscriptions = subscriptionsService.getSubscriptionsBySubscriber(userId);
        if (subscriptions.isEmpty()) {
            System.out.println("구독한 사용자가 없습니다.");
            return;
        }
        
        System.out.println("번호\t구독 대상\t알림 설정");
        int count = 1;
        for (Subscriptions subscription : subscriptions) {
            System.out.printf("%d\t%s\t%s\n", 
                count++, 
                subscription.getTargetId(), 
                subscription.isAlram() ? "켜짐" : "꺼짐");
        }
        
        System.out.print("설정을 변경할 구독 번호: ");
        int index = readInt() - 1;
        
        if (index < 0 || index >= subscriptions.size()) {
            System.out.println("잘못된 번호입니다.");
            return;
        }
        
        Subscriptions subscription = subscriptions.get(index);
        boolean currentSetting = subscription.isAlram();
        
        System.out.printf("현재 알림 설정: %s\n", currentSetting ? "켜짐" : "꺼짐");
        System.out.print("새 알림 설정 (y/n): ");
        String input = scanner.nextLine().toLowerCase();
        boolean newSetting = input.equals("y");
        
        if (currentSetting == newSetting) {
            System.out.println("설정이 변경되지 않았습니다.");
            return;
        }
        
        try {
            String targetId = subscription.getTargetId();
            subscriptionsService.updateNotificationSetting(userId, targetId, newSetting);
            System.out.println("알림 설정이 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("알림 설정 변경 중 오류가 발생했습니다: " + e.getMessage());
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