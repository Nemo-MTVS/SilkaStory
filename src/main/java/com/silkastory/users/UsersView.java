package com.silkastory.users;

import java.util.Scanner;

/**
 * 사용자 기능에 대한 View 클래스
 * 사용자와 직접 상호작용하여 입력을 받고 결과를 출력
 */
public class UsersView {
    private final UsersService usersService;
    private final Scanner scanner;
    private final UserContext userContext;

    public UsersView() {
        UsersDao usersDao = new UsersDao();
        this.usersService = new UsersService(usersDao);
        this.scanner = new Scanner(System.in);
        this.userContext = UserContext.getInstance();
    }

    /**
     * 사용자 메인 메뉴 실행
     */
    public void showMenu() {
        boolean running = true;
        
        while (running) {
            System.out.println("\n===== 사용자 관리 =====");
            System.out.println("1. 회원가입");
            System.out.println("2. 로그인");
            
            // 로그인 상태에 따라 메뉴 표시
            if (userContext.isLoggedIn()) {
                System.out.println("3. 사용자 정보 수정");
                System.out.println("4. 회원 탈퇴");
                System.out.println("5. 로그아웃");
            }
            
            System.out.println("0. 돌아가기");
            System.out.print("메뉴 선택: ");
            
            int choice = readInt();
            
            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    if (userContext.isLoggedIn()) {
                        updateUserInfo();
                    } else {
                        System.out.println("로그인이 필요한 기능입니다.");
                    }
                    break;
                case 4:
                    if (userContext.isLoggedIn()) {
                        deleteUser();
                    } else {
                        System.out.println("로그인이 필요한 기능입니다.");
                    }
                    break;
                case 5:
                    if (userContext.isLoggedIn()) {
                        logout();
                        System.out.println("로그아웃 되었습니다.");
                    } else {
                        System.out.println("로그인이 필요한 기능입니다.");
                    }
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
     * 회원가입 처리
     */
    public void registerUser() {
        System.out.println("\n===== 회원가입 =====");
        
        System.out.print("이름: ");
        String name = scanner.nextLine();
        
        System.out.print("닉네임: ");
        String nickname = scanner.nextLine();
        
        System.out.print("이메일: ");
        String email = scanner.nextLine();
        
        System.out.print("비밀번호: ");
        String password = scanner.nextLine();
        
        System.out.print("비밀번호 확인: ");
        String confirmPassword = scanner.nextLine();
        
        if (!password.equals(confirmPassword)) {
            System.out.println("비밀번호가 일치하지 않습니다.");
            return;
        }
        
        try {
            usersService.saveUser(name, nickname, email, password);
            System.out.println("회원가입이 성공적으로 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("회원가입 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 로그인 처리
     */
    public void login() {
        System.out.println("\n===== 로그인 =====");
        
        System.out.print("이메일: ");
        String email = scanner.nextLine();
        
        System.out.print("비밀번호: ");
        String password = scanner.nextLine();
        
        try {
            Users user = usersService.login(email, password);
            userContext.setCurrentUser(user);
            System.out.println(user.getNickname() + "님 환영합니다!");
        } catch (IllegalArgumentException e) {
            System.out.println("로그인 실패: " + e.getMessage());
        }
    }

    /**
     * 사용자 정보 수정 처리
     */
    private void updateUserInfo() {
        Users currentUser = userContext.getCurrentUser();
        if (currentUser == null) {
            System.out.println("로그인이 필요한 기능입니다.");
            return;
        }
        
        System.out.println("\n===== 사용자 정보 수정 =====");
        
        System.out.print("새 이름 (현재: " + currentUser.getName() + "): ");
        String newName = scanner.nextLine();
        if (newName.isEmpty()) {
            newName = currentUser.getName();
        }
        
        System.out.print("새 닉네임 (현재: " + currentUser.getNickname() + "): ");
        String newNickname = scanner.nextLine();
        if (newNickname.isEmpty()) {
            newNickname = currentUser.getNickname();
        }
        
        System.out.print("새 비밀번호 (변경하지 않으려면 빈칸): ");
        String newPassword = scanner.nextLine();
        
        if (!newPassword.isEmpty()) {
            System.out.print("비밀번호 확인: ");
            String confirmPassword = scanner.nextLine();
            
            if (!newPassword.equals(confirmPassword)) {
                System.out.println("비밀번호가 일치하지 않습니다.");
                return;
            }
        }
        
        try {
            boolean success = usersService.updateUser(currentUser.getId(), newName, newNickname, newPassword);
            if (success) {
                System.out.println("사용자 정보가 성공적으로 수정되었습니다.");
                // 전역 사용자 정보 업데이트
                Users updatedUser;
                if (newPassword.isEmpty()) {
                    updatedUser = new Users(currentUser.getId(), newName, newNickname, currentUser.getEmail(), currentUser.getPassword());
                } else {
                    updatedUser = new Users(currentUser.getId(), newName, newNickname, currentUser.getEmail(), newPassword);
                }
                userContext.setCurrentUser(updatedUser);
            } else {
                System.out.println("사용자 정보 수정에 실패했습니다.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("사용자 정보 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 회원 탈퇴 처리
     */
    private void deleteUser() {
        Users currentUser = userContext.getCurrentUser();
        if (currentUser == null) {
            System.out.println("로그인이 필요한 기능입니다.");
            return;
        }
        
        System.out.println("\n===== 회원 탈퇴 =====");
        System.out.println("정말로 탈퇴하시겠습니까? (y/n)");
        String confirm = scanner.nextLine().toLowerCase();
        
        if (!confirm.equals("y")) {
            System.out.println("회원 탈퇴가 취소되었습니다.");
            return;
        }
        
        try {
            boolean success = usersService.deleteUser(currentUser.getId());
            if (success) {
                System.out.println("회원 탈퇴가 완료되었습니다.");
                userContext.logout();
            } else {
                System.out.println("회원 탈퇴에 실패했습니다.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("회원 탈퇴 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 로그아웃 처리
     */
    public void logout() {
        userContext.logout();
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