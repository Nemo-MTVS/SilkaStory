// package com.silkastory.notifications;
//
// import com.silkastory.common.View;
// import com.silkastory.common.RepositoryFactory;
// import com.silkastory.infrastructure.database.JDBCConnection;
//
// import java.time.format.DateTimeFormatter;
// import java.util.List;
// import java.util.Scanner;
//
// public class NotificationsView implements View {
//     private static final Scanner scanner = new Scanner(System.in);
//     private static final NotificationsRepository notificationsRepository = RepositoryFactory.getRepository(NotificationsRepository.class);
//     private static final NotificationsService notificationsService = new NotificationsService(notificationsRepository);
//     private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//     private static final String TEST_USER_ID = "test_user";
//
//     private boolean running = false;
//
//     @Override
//     public void start() {
//         running = true;
//         System.out.println("알림 시뮬레이터를 시작합니다.");
//
//         while (running) {
//             printMenu();
//             int choice = getIntInput("메뉴를 선택하세요: ");
//
//             try {
//                 switch (choice) {
//                     case 1:
//                         createNotification();
//                         break;
//                     case 2:
//                         sendNotificationToSubscribers();
//                         break;
//                     case 3:
//                         viewAllNotifications();
//                         break;
//                     case 4:
//                         viewUnreadNotifications();
//                         break;
//                     case 5:
//                         markNotificationAsRead();
//                         break;
//                     case 6:
//                         markAllNotificationsAsRead();
//                         break;
//                     case 7:
//                         countNotifications();
//                         break;
//                     case 0:
//                         stop();
//                         break;
//                     default:
//                         System.out.println("잘못된 메뉴 선택입니다. 다시 선택해주세요.");
//                 }
//             } catch (Exception e) {
//                 System.out.println("오류 발생: " + e.getMessage());
//             }
//
//             System.out.println();
//         }
//     }
//
//     @Override
//     public void stop() {
//         running = false;
//         System.out.println("알림 시뮬레이터를 종료합니다.");
//         JDBCConnection.close();
//     }
//
//     @Override
//     public String getName() {
//         return "알림 시뮬레이터";
//     }
//
//     private void printMenu() {
//         System.out.println("\n=== 알림 시뮬레이터 메뉴 ===");
//         System.out.println("1. 개별 알림 생성하기");
//         System.out.println("2. 구독자들에게 알림 전송하기");
//         System.out.println("3. 내 알림 모두 보기");
//         System.out.println("4. 읽지 않은 알림 보기");
//         System.out.println("5. 알림 읽음 처리하기");
//         System.out.println("6. 모든 알림 읽음 처리하기");
//         System.out.println("7. 알림 개수 확인하기");
//         System.out.println("0. 종료");
//     }
//
//     private void createNotification() {
//         System.out.println("\n=== 개별 알림 생성하기 ===");
//         String userId = getStringInput("알림 수신자 ID: ");
//         String message = getStringInput("알림 메시지: ");
//
//         try {
//             Notifications notification = notificationsService.createNotification(message, userId);
//             System.out.println("알림이 성공적으로 생성되었습니다.");
//             printNotification(notification);
//         } catch (Exception e) {
//             System.out.println("알림 생성 실패: " + e.getMessage());
//         }
//     }
//
//     private void sendNotificationToSubscribers() {
//         System.out.println("\n=== 구독자들에게 알림 전송하기 ===");
//         String publisherId = getStringInput("발신자 ID (기본값: " + TEST_USER_ID + "): ");
//         if (publisherId.isEmpty()) publisherId = TEST_USER_ID;
//
//         String message = getStringInput("알림 메시지: ");
//
//         try {
//             notificationsService.sendNotificationToSubscribers(message, publisherId);
//             System.out.println("구독자들에게 알림이 전송되었습니다.");
//         } catch (Exception e) {
//             System.out.println("알림 전송 실패: " + e.getMessage());
//         }
//     }
//
//     private void viewAllNotifications() {
//         System.out.println("\n=== 내 알림 모두 보기 ===");
//         String userId = getStringInput("사용자 ID (기본값: " + TEST_USER_ID + "): ");
//         if (userId.isEmpty()) userId = TEST_USER_ID;
//
//         try {
//             List<Notifications> notifications = notificationsService.findByUserId(userId);
//             System.out.println(userId + "의 알림 " + notifications.size() + "개:");
//
//             for (Notifications notification : notifications) {
//                 printNotification(notification);
//             }
//         } catch (Exception e) {
//             System.out.println("알림 조회 실패: " + e.getMessage());
//         }
//     }
//
//     private void viewUnreadNotifications() {
//         System.out.println("\n=== 읽지 않은 알림 보기 ===");
//         String userId = getStringInput("사용자 ID (기본값: " + TEST_USER_ID + "): ");
//         if (userId.isEmpty()) userId = TEST_USER_ID;
//
//         try {
//             List<Notifications> unreadNotifications = notificationsService.findUnreadByUserId(userId);
//             System.out.println(userId + "의 읽지 않은 알림 " + unreadNotifications.size() + "개:");
//
//             for (Notifications notification : unreadNotifications) {
//                 printNotification(notification);
//             }
//         } catch (Exception e) {
//             System.out.println("알림 조회 실패: " + e.getMessage());
//         }
//     }
//
//     private void markNotificationAsRead() {
//         System.out.println("\n=== 알림 읽음 처리하기 ===");
//         Long notificationId = getLongInput("읽음 처리할 알림 ID: ");
//
//         try {
//             notificationsService.markAsRead(notificationId);
//             System.out.println("알림이 읽음 처리되었습니다.");
//             printNotification(notificationsService.findById(notificationId));
//         } catch (Exception e) {
//             System.out.println("알림 읽음 처리 실패: " + e.getMessage());
//         }
//     }
//
//     private void markAllNotificationsAsRead() {
//         System.out.println("\n=== 모든 알림 읽음 처리하기 ===");
//         String userId = getStringInput("사용자 ID (기본값: " + TEST_USER_ID + "): ");
//         if (userId.isEmpty()) userId = TEST_USER_ID;
//
//         try {
//             int count = notificationsService.markAllAsRead(userId);
//             System.out.println(count + "개의 알림이 읽음 처리되었습니다.");
//         } catch (Exception e) {
//             System.out.println("알림 읽음 처리 실패: " + e.getMessage());
//         }
//     }
//
//     private void countNotifications() {
//         System.out.println("\n=== 알림 개수 확인하기 ===");
//         String userId = getStringInput("사용자 ID (기본값: " + TEST_USER_ID + "): ");
//         if (userId.isEmpty()) userId = TEST_USER_ID;
//
//         try {
//             int totalCount = notificationsService.countByUserId(userId);
//             int unreadCount = notificationsService.countUnreadByUserId(userId);
//
//             System.out.println(userId + "의 알림 통계:");
//             System.out.println("전체 알림: " + totalCount + "개");
//             System.out.println("읽지 않은 알림: " + unreadCount + "개");
//             System.out.println("읽은 알림: " + (totalCount - unreadCount) + "개");
//         } catch (Exception e) {
//             System.out.println("알림 개수 확인 실패: " + e.getMessage());
//         }
//     }
//
//     private void printNotification(Notifications notification) {
//         System.out.println("----------------------------------------");
//         System.out.println("알림 ID: " + notification.getId());
//         System.out.println("수신자 ID: " + notification.getUserId());
//         System.out.println("메시지: " + notification.getMessage());
//         System.out.println("읽음 상태: " + (notification.isState() ? "읽음" : "읽지 않음"));
//         System.out.println("발송 시간: " + notification.getSendDate().format(formatter));
//         System.out.println("----------------------------------------");
//     }
//
//     private String getStringInput(String prompt) {
//         System.out.print(prompt);
//         return scanner.nextLine().trim();
//     }
//
//     private int getIntInput(String prompt) {
//         while (true) {
//             try {
//                 System.out.print(prompt);
//                 return Integer.parseInt(scanner.nextLine().trim());
//             } catch (NumberFormatException e) {
//                 System.out.println("숫자를 입력해주세요.");
//             }
//         }
//     }
//
//     private long getLongInput(String prompt) {
//         while (true) {
//             try {
//                 System.out.print(prompt);
//                 return Long.parseLong(scanner.nextLine().trim());
//             } catch (NumberFormatException e) {
//                 System.out.println("숫자를 입력해주세요.");
//             }
//         }
//     }
// }