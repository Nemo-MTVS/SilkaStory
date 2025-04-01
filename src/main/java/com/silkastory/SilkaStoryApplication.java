package com.silkastory;

import java.util.Scanner;

/**
 * SilkaStory 애플리케이션의 메인 클래스
 */
public class SilkaStoryApplication {
    
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("SilkaStory 애플리케이션을 시작합니다.");
        
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = getIntInput("메뉴를 선택하세요: ");
            
            try {
                if (choice == 0) {
                    running = false;
                    System.out.println("프로그램을 종료합니다.");
                } else if (choice >= 1 && choice <= ViewFactory.getAllDomains().length) {
                    String domain = ViewFactory.getAllDomains()[choice - 1];
                    View view = ViewFactory.getView(domain);
                    view.start();
                } else {
                    System.out.println("잘못된 메뉴 선택입니다. 다시 선택해주세요.");
                }
            } catch (Exception e) {
                System.out.println("오류 발생: " + e.getMessage());
            }
            
            System.out.println();
        }
        
        scanner.close();
    }
    
    private static void printMainMenu() {
        System.out.println("\n=== SilkaStory 메인 메뉴 ===");
        
        String[] domains = ViewFactory.getAllDomains();
        for (int i = 0; i < domains.length; i++) {
            System.out.println((i + 1) + ". " + domains[i] + " 시뮬레이터");
        }
        
        System.out.println("0. 종료");
    }
    
    private static int getIntInput(String prompt) {
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