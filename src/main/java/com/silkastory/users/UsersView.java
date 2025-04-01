package com.silkastory.users;

import com.silkastory.View;

import java.util.List;
import java.util.Scanner;

public class UsersView implements View {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UsersDao usersDao = new UsersDao();
    private static final UsersService usersService = new UsersService(usersDao);
    
    private boolean running = false;
    
    @Override
    public void start() {
        running = true;
        System.out.println("사용자 시뮬레이터를 시작합니다.");
        
        while (running) {
            printMenu();
            int choice = getIntInput("메뉴를 선택하세요: ");
            
            try {
                switch (choice) {
                    case 1:
                        createUser();
                        break;
                    case 2:
                        findUser();
                        break;
                    case 3:
                        updateUser();
                        break;
                    case 4:
                        deleteUser();
                        break;
                    case 5:
                        listAllUsers();
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
        System.out.println("사용자 시뮬레이터를 종료합니다.");
    }
    
    @Override
    public String getName() {
        return "사용자 시뮬레이터";
    }
    
    private void printMenu() {
        System.out.println("\n=== 사용자 시뮬레이터 메뉴 ===");
        System.out.println("1. 사용자 생성");
        System.out.println("2. 사용자 조회");
        System.out.println("3. 사용자 정보 수정");
        System.out.println("4. 사용자 삭제");
        System.out.println("5. 모든 사용자 목록 조회");
        System.out.println("0. 종료");
    }
    
    private void createUser() {
        System.out.println("\n=== 사용자 생성 ===");
        String userId = getStringInput("사용자 ID를 입력하세요: ");
        String password = getStringInput("비밀번호를 입력하세요: ");
        String name = getStringInput("이름을 입력하세요: ");
        
        try {
            Users user = usersService.createUser(userId, password, name);
            System.out.println("사용자가 성공적으로 생성되었습니다.");
            printUser(user);
        } catch (Exception e) {
            System.out.println("사용자 생성 실패: " + e.getMessage());
        }
    }
    
    private void findUser() {
        System.out.println("\n=== 사용자 조회 ===");
        String userId = getStringInput("조회할 사용자 ID를 입력하세요: ");
        
        try {
            Users user = usersService.findUser(userId);
            if (user != null) {
                System.out.println("사용자를 찾았습니다:");
                printUser(user);
            } else {
                System.out.println("사용자를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            System.out.println("사용자 조회 실패: " + e.getMessage());
        }
    }
    
    private void updateUser() {
        System.out.println("\n=== 사용자 정보 수정 ===");
        String userId = getStringInput("수정할 사용자 ID를 입력하세요: ");
        
        try {
            Users user = usersService.findUser(userId);
            if (user == null) {
                System.out.println("사용자를 찾을 수 없습니다.");
                return;
            }
            
            System.out.println("현재 사용자 정보:");
            printUser(user);
            
            String newPassword = getStringInput("새 비밀번호 (변경하지 않으려면 빈칸): ");
            String newName = getStringInput("새 이름 (변경하지 않으려면 빈칸): ");
            
            if (!newPassword.isEmpty()) {
                user.setPassword(newPassword);
            }
            if (!newName.isEmpty()) {
                user.setName(newName);
            }
            
            usersService.updateUser(user);
            System.out.println("사용자 정보가 업데이트되었습니다:");
            printUser(user);
        } catch (Exception e) {
            System.out.println("사용자 정보 수정 실패: " + e.getMessage());
        }
    }
    
    private void deleteUser() {
        System.out.println("\n=== 사용자 삭제 ===");
        String userId = getStringInput("삭제할 사용자 ID를 입력하세요: ");
        
        try {
            boolean deleted = usersService.deleteUser(userId);
            if (deleted) {
                System.out.println("사용자가 성공적으로 삭제되었습니다.");
            } else {
                System.out.println("사용자 삭제에 실패했습니다.");
            }
        } catch (Exception e) {
            System.out.println("사용자 삭제 실패: " + e.getMessage());
        }
    }
    
    private void listAllUsers() {
        System.out.println("\n=== 모든 사용자 목록 ===");
        
        try {
            List<Users> users = usersService.getAllUsers();
            System.out.println("총 " + users.size() + "명의 사용자가 있습니다:");
            
            for (Users user : users) {
                printUser(user);
            }
        } catch (Exception e) {
            System.out.println("사용자 목록 조회 실패: " + e.getMessage());
        }
    }
    
    private void printUser(Users user) {
        System.out.println("----------------------------------------");
        System.out.println("사용자 ID: " + user.getUserId());
        System.out.println("이름: " + user.getName());
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