package com.silkastory.visitor;

import com.silkastory.View;
import com.silkastory.common.RepositoryFactory;
import com.silkastory.infrastructure.database.JDBCConnection;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class VisitorView implements View {
    private static final Scanner scanner = new Scanner(System.in);
    private static final VisitorRepository visitorRepository = RepositoryFactory.getRepository(VisitorRepository.class);
    private static final VisitorService visitorService = new VisitorService(visitorRepository);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private boolean running = false;
    
    @Override
    public void start() {
        running = true;
        System.out.println("방문자 시뮬레이터를 시작합니다.");
        
        while (running) {
            printMenu();
            int choice = getIntInput("메뉴를 선택하세요: ");
            
            try {
                switch (choice) {
                    case 1:
                        recordVisit();
                        break;
                    case 2:
                        findRecentVisitors();
                        break;
                    case 3:
                        findVisitorsByDate();
                        break;
                    case 4:
                        countVisitors();
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
        System.out.println("방문자 시뮬레이터를 종료합니다.");
        JDBCConnection.close();
    }
    
    @Override
    public String getName() {
        return "방문자 시뮬레이터";
    }
    
    private void printMenu() {
        System.out.println("\n=== 방문자 시뮬레이터 메뉴 ===");
        System.out.println("1. 방문 기록하기");
        System.out.println("2. 최근 방문자 조회");
        System.out.println("3. 날짜별 방문자 조회");
        System.out.println("4. 방문자 수 조회");
        System.out.println("0. 종료");
    }
    
    private void recordVisit() {
        System.out.println("\n=== 방문 기록하기 ===");
        String userId = getStringInput("방문자 ID를 입력하세요: ");
        String targetId = getStringInput("방문 대상 ID를 입력하세요: ");
        
        try {
            Visitor visit = visitorService.recordVisit(userId, targetId);
            System.out.println("방문이 성공적으로 기록되었습니다.");
            printVisitor(visit);
        } catch (Exception e) {
            System.out.println("방문 기록 실패: " + e.getMessage());
        }
    }
    
    private void findRecentVisitors() {
        System.out.println("\n=== 최근 방문자 조회 ===");
        String targetId = getStringInput("대상 ID를 입력하세요: ");
        int limit = getIntInput("조회할 방문자 수를 입력하세요: ");
        
        try {
            List<Visitor> visitors = visitorService.findRecentVisitors(targetId, limit);
            System.out.println(targetId + "의 최근 방문자 " + visitors.size() + "명:");
            visitors.forEach(this::printVisitor);
        } catch (Exception e) {
            System.out.println("방문자 조회 실패: " + e.getMessage());
        }
    }
    
    private void findVisitorsByDate() {
        System.out.println("\n=== 날짜별 방문자 조회 ===");
        String targetId = getStringInput("대상 ID를 입력하세요: ");
        String dateStr = getStringInput("조회할 날짜를 입력하세요 (yyyy-MM-dd): ");
        
        try {
            LocalDateTime date = LocalDateTime.parse(dateStr + " 00:00:00", formatter);
            List<Visitor> visitors = visitorService.findVisitorsByDate(targetId, date);
            System.out.println(targetId + "의 " + dateStr + " 방문자 " + visitors.size() + "명:");
            visitors.forEach(this::printVisitor);
        } catch (Exception e) {
            System.out.println("방문자 조회 실패: " + e.getMessage());
        }
    }
    
    private void countVisitors() {
        System.out.println("\n=== 방문자 수 조회 ===");
        String targetId = getStringInput("대상 ID를 입력하세요: ");
        
        try {
            int count = visitorService.countVisitors(targetId);
            System.out.println(targetId + "의 총 방문자 수: " + count + "명");
        } catch (Exception e) {
            System.out.println("방문자 수 조회 실패: " + e.getMessage());
        }
    }
    
    private void printVisitor(Visitor visitor) {
        System.out.println("----------------------------------------");
        System.out.println("방문 ID: " + visitor.getId());
        System.out.println("방문자 ID: " + visitor.getUserId());
        System.out.println("방문 대상 ID: " + visitor.getTargetId());
        System.out.println("방문 시간: " + visitor.getVisitDate().format(formatter));
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