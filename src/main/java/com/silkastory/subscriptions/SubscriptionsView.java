package com.silkastory.subscriptions;

import com.silkastory.View;
import com.silkastory.common.RepositoryFactory;
import com.silkastory.infrastructure.database.JDBCConnection;

import java.util.List;
import java.util.Scanner;

public class SubscriptionsView implements View {
    private static final Scanner scanner = new Scanner(System.in);
    private static final SubscriptionsRepository subscriptionsRepository = RepositoryFactory.getRepository(SubscriptionsRepository.class);
    private static final SubscriptionsService subscriptionsService = new SubscriptionsService(subscriptionsRepository);
    private static final String TEST_USER_ID = "test_user";
    
    private boolean running = false;
    
    @Override
    public void start() {
        running = true;
        System.out.println("구독 시뮬레이터를 시작합니다.");
        
        while (running) {
            printMenu();
            int choice = getIntInput("메뉴를 선택하세요: ");
            
            try {
                switch (choice) {
                    case 1:
                        subscribe();
                        break;
                    case 2:
                        unsubscribe();
                        break;
                    case 3:
                        updateNotificationSetting();
                        break;
                    case 4:
                        checkSubscription();
                        break;
                    case 5:
                        listSubscriptions();
                        break;
                    case 6:
                        listSubscribers();
                        break;
                    case 7:
                        listNotifiableSubscribers();
                        break;
                    case 0:
                        stop();
                        break;
                    default:
                        System.out.println("잘못된 메뉴 선택입니다. 다시 선택해주세요.");
                }
            } catch (Exception e) {
                System.out.println("오류 발생: " + e.getMessage());
            }
            
            System.out.println();
        }
    }
    
    @Override
    public void stop() {
        running = false;
        System.out.println("구독 시뮬레이터를 종료합니다.");
        JDBCConnection.close();
    }
    
    @Override
    public String getName() {
        return "구독 시뮬레이터";
    }
    
    private void printMenu() {
        System.out.println("\n=== 구독 시뮬레이터 메뉴 ===");
        System.out.println("1. 구독하기");
        System.out.println("2. 구독 취소하기");
        System.out.println("3. 알림 설정 변경하기");
        System.out.println("4. 구독 여부 확인하기");
        System.out.println("5. 내 구독 목록 보기");
        System.out.println("6. 나를 구독한 사람 목록 보기");
        System.out.println("7. 알림 받는 구독자 목록 보기");
        System.out.println("0. 종료");
    }
    
    private void subscribe() {
        System.out.println("\n=== 구독하기 ===");
        String userId = getStringInput("구독자 ID (기본값: " + TEST_USER_ID + "): ");
        if (userId.isEmpty()) userId = TEST_USER_ID;
        
        String targetId = getStringInput("구독 대상 ID: ");
        
        try {
            Subscriptions subscription = subscriptionsService.subscribe(userId, targetId);
            System.out.println("구독이 성공적으로 처리되었습니다.");
            printSubscription(subscription);
        } catch (Exception e) {
            System.out.println("구독 실패: " + e.getMessage());
        }
    }
    
    private void unsubscribe() {
        System.out.println("\n=== 구독 취소하기 ===");
        String userId = getStringInput("구독자 ID (기본값: " + TEST_USER_ID + "): ");
        if (userId.isEmpty()) userId = TEST_USER_ID;
        
        String targetId = getStringInput("구독 대상 ID: ");
        
        try {
            boolean unsubscribed = subscriptionsService.unsubscribe(userId, targetId);
            if (unsubscribed) {
                System.out.println("구독이 취소되었습니다.");
            } else {
                System.out.println("구독 취소에 실패했습니다. 구독 정보가 없습니다.");
            }
        } catch (Exception e) {
            System.out.println("구독 취소 실패: " + e.getMessage());
        }
    }
    
    private void updateNotificationSetting() {
        System.out.println("\n=== 알림 설정 변경하기 ===");
        String userId = getStringInput("구독자 ID (기본값: " + TEST_USER_ID + "): ");
        if (userId.isEmpty()) userId = TEST_USER_ID;
        
        String targetId = getStringInput("구독 대상 ID: ");
        
        try {
            Subscriptions subscription = subscriptionsService.findByUserIdAndTargetId(userId, targetId);
            if (subscription == null) {
                System.out.println("구독 정보가 없습니다. 먼저 구독을 해주세요.");
                return;
            }
            
            System.out.println("현재 알림 설정: " + (subscription.isAlram() ? "활성화" : "비활성화"));
            System.out.println("알림을 " + (subscription.isAlram() ? "비활성화" : "활성화") + " 하시겠습니까? (Y/N)");
            boolean confirm = scanner.nextLine().trim().equalsIgnoreCase("Y");
            
            if (confirm) {
                subscription.setAlram(!subscription.isAlram());
                subscriptionsService.updateNotificationSetting(subscription);
                System.out.println("알림 설정이 변경되었습니다.");
                printSubscription(subscription);
            } else {
                System.out.println("알림 설정 변경이 취소되었습니다.");
            }
        } catch (Exception e) {
            System.out.println("알림 설정 변경 실패: " + e.getMessage());
        }
    }
    
    private void checkSubscription() {
        System.out.println("\n=== 구독 여부 확인하기 ===");
        String userId = getStringInput("구독자 ID (기본값: " + TEST_USER_ID + "): ");
        if (userId.isEmpty()) userId = TEST_USER_ID;
        
        String targetId = getStringInput("구독 대상 ID: ");
        
        try {
            boolean isSubscribed = subscriptionsService.isSubscribed(userId, targetId);
            if (isSubscribed) {
                System.out.println(userId + "님은 " + targetId + "님을 구독하고 있습니다.");
                Subscriptions subscription = subscriptionsService.findByUserIdAndTargetId(userId, targetId);
                printSubscription(subscription);
            } else {
                System.out.println(userId + "님은 " + targetId + "님을 구독하고 있지 않습니다.");
            }
        } catch (Exception e) {
            System.out.println("구독 여부 확인 실패: " + e.getMessage());
        }
    }
    
    private void listSubscriptions() {
        System.out.println("\n=== 내 구독 목록 보기 ===");
        String userId = getStringInput("사용자 ID (기본값: " + TEST_USER_ID + "): ");
        if (userId.isEmpty()) userId = TEST_USER_ID;
        
        try {
            List<Subscriptions> subscriptions = subscriptionsService.findByUserId(userId);
            System.out.println(userId + "님의 구독 목록 " + subscriptions.size() + "개:");
            
            for (Subscriptions subscription : subscriptions) {
                printSubscription(subscription);
            }
        } catch (Exception e) {
            System.out.println("구독 목록 조회 실패: " + e.getMessage());
        }
    }
    
    private void listSubscribers() {
        System.out.println("\n=== 나를 구독한 사람 목록 보기 ===");
        String targetId = getStringInput("대상 ID (기본값: " + TEST_USER_ID + "): ");
        if (targetId.isEmpty()) targetId = TEST_USER_ID;
        
        try {
            List<Subscriptions> subscribers = subscriptionsService.findByTargetId(targetId);
            System.out.println(targetId + "님을 구독한 사용자 " + subscribers.size() + "명:");
            
            for (Subscriptions subscription : subscribers) {
                printSubscription(subscription);
            }
        } catch (Exception e) {
            System.out.println("구독자 목록 조회 실패: " + e.getMessage());
        }
    }
    
    private void listNotifiableSubscribers() {
        System.out.println("\n=== 알림 받는 구독자 목록 보기 ===");
        String targetId = getStringInput("대상 ID (기본값: " + TEST_USER_ID + "): ");
        if (targetId.isEmpty()) targetId = TEST_USER_ID;
        
        try {
            List<Subscriptions> notifiableSubscribers = subscriptionsService.findNotifiableSubscribers(targetId);
            System.out.println(targetId + "님으로부터 알림을 받는 구독자 " + notifiableSubscribers.size() + "명:");
            
            for (Subscriptions subscription : notifiableSubscribers) {
                printSubscription(subscription);
            }
        } catch (Exception e) {
            System.out.println("알림 받는 구독자 목록 조회 실패: " + e.getMessage());
        }
    }
    
    private void printSubscription(Subscriptions subscription) {
        System.out.println("----------------------------------------");
        System.out.println("구독 ID: " + subscription.getId());
        System.out.println("구독자 ID: " + subscription.getUserId());
        System.out.println("대상 사용자 ID: " + subscription.getTargetId());
        System.out.println("알림 설정: " + (subscription.isAlram() ? "활성화" : "비활성화"));
        System.out.println("----------------------------------------");
    }
    
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("숫자를 입력해주세요.");
            }
        }
    }
} 