package com.silkastory.replies;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

/**
 * 답글 관련 사용자 인터페이스 클래스
 */
public class RepliesView {
    
    private final RepliesService repliesService;
    private final Scanner scanner;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public RepliesView(RepliesService repliesService, Scanner scanner) {
        this.repliesService = repliesService;
        this.scanner = scanner;
    }
    
    /**
     * 답글 메뉴 표시
     */
    public void showMenu(String userId, Long commentId) {
        boolean running = true;
        
        while (running) {
            System.out.println("\n===== 답글 관리 =====");
            System.out.println("1. 답글 목록 보기");
            System.out.println("2. 답글 작성하기");
            System.out.println("3. 답글 수정하기");
            System.out.println("4. 답글 삭제하기");
            System.out.println("5. 내 답글 보기");
            System.out.println("0. 이전 메뉴로 돌아가기");
            System.out.print("선택: ");
            
            int choice = readInt();
            
            switch (choice) {
                case 1:
                    showRepliesByComment(commentId);
                    break;
                case 2:
                    createReply(userId, commentId);
                    break;
                case 3:
                    updateReply(userId);
                    break;
                case 4:
                    deleteReply(userId);
                    break;
                case 5:
                    showMyReplies(userId);
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
     * 댓글의 답글 목록 표시
     */
    public void showRepliesByComment(Long commentId) {
        try {
            List<Reply> replies = repliesService.getRepliesByComment(commentId);
            
            if (replies.isEmpty()) {
                System.out.println("답글이 없습니다.");
                return;
            }
            
            System.out.println("\n===== 답글 목록 =====");
            int count = 1;
            for (Reply reply : replies) {
                System.out.println(count + ". 작성자: " + reply.getUserId() + 
                        "\n   내용: " + reply.getContent());
                count++;
            }
        } catch (SQLException e) {
            System.out.println("답글 목록을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 새 답글 작성
     */
    private void createReply(String userId, Long commentId) {
        System.out.println("\n===== 답글 작성 =====");
        System.out.print("내용: ");
        scanner.nextLine(); // 버퍼 비우기
        String content = scanner.nextLine();
        
        try {
            boolean success = repliesService.addReply(content, commentId, userId);
            if (success) {
                System.out.println("답글이 성공적으로 작성되었습니다.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("답글 작성 실패: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("답글 작성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 답글 수정
     */
    private void updateReply(String userId) {
        System.out.println("\n===== 답글 수정 =====");
        System.out.print("수정할 답글 ID: ");
        Long replyId = (long) readInt();
        
        try {
            Reply reply = repliesService.getReplyById(replyId);
            
            if (reply == null) {
                System.out.println("존재하지 않는 답글입니다.");
                return;
            }
            
            System.out.println("현재 내용: " + reply.getContent());
            System.out.print("새 내용: ");
            scanner.nextLine(); // 버퍼 비우기
            String newContent = scanner.nextLine();
            
            boolean success = repliesService.updateReply(replyId, newContent, userId);
            if (success) {
                System.out.println("답글이 성공적으로 수정되었습니다.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("답글 수정 실패: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("답글 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 답글 삭제
     */
    private void deleteReply(String userId) {
        System.out.println("\n===== 답글 삭제 =====");
        System.out.print("삭제할 답글 ID: ");
        Long replyId = (long) readInt();
        
        try {
            boolean success = repliesService.deleteReply(replyId, userId);
            if (success) {
                System.out.println("답글이 성공적으로 삭제되었습니다.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("답글 삭제 실패: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("답글 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 내 답글 목록 표시
     */
    private void showMyReplies(String userId) {
        try {
            List<Reply> replies = repliesService.getRepliesByUser(userId);
            
            if (replies.isEmpty()) {
                System.out.println("작성한 답글이 없습니다.");
                return;
            }
            
            System.out.println("\n===== 내 답글 목록 =====");
            int count = 1;
            for (Reply reply : replies) {
                System.out.println(count + ". 댓글 ID: " + reply.getCommentId() + 
                        "\n   내용: " + reply.getContent());
                count++;
            }
        } catch (SQLException e) {
            System.out.println("답글 목록을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 정수 입력 처리
     */
    private int readInt() {
        int value;
        try {
            value = scanner.nextInt();
        } catch (Exception e) {
            System.out.println("숫자를 입력해주세요.");
            scanner.nextLine(); // 버퍼 비우기
            return -1;
        }
        return value;
    }
} 