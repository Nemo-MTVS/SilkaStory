package com.silkastory.comments;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

/**
 * 댓글 관련 사용자 인터페이스 클래스
 */
public class CommentView {
    
    private final CommentService commentService;
    private final Scanner scanner;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public CommentView(CommentService commentService, Scanner scanner) {
        this.commentService = commentService;
        this.scanner = scanner;
    }
    
    /**
     * 댓글 메뉴 표시
     */
    public void showMenu(String userId, Long postId) {
        boolean running = true;
        
        while (running) {
            System.out.println("\n===== 댓글 관리 =====");
            System.out.println("1. 댓글 목록 보기");
            System.out.println("2. 댓글 작성하기");
            System.out.println("3. 댓글 수정하기");
            System.out.println("4. 댓글 삭제하기");
            System.out.println("5. 내 댓글 보기");
            System.out.println("0. 이전 메뉴로 돌아가기");
            System.out.print("선택: ");
            
            int choice = readInt();
            
            switch (choice) {
                case 1:
                    showCommentsByPost(postId);
                    break;
                case 2:
                    createComment(userId, postId);
                    break;
                case 3:
                    updateComment(userId);
                    break;
                case 4:
                    deleteComment(userId);
                    break;
                case 5:
                    showMyComments(userId);
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
     * 게시글의 댓글 목록 표시
     */
    public void showCommentsByPost(Long postId) {
        try {
            List<Comment> comments = commentService.getCommentsByPost(postId);
            
            if (comments.isEmpty()) {
                System.out.println("댓글이 없습니다.");
                return;
            }
            
            System.out.println("\n===== 댓글 목록 =====");
            int count = 1;
            for (Comment comment : comments) {
                System.out.println(count + ". 작성자: " + comment.getUserId() + 
                        "\n   내용: " + comment.getContent());
                count++;
            }
        } catch (SQLException e) {
            System.out.println("댓글 목록을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 새 댓글 작성
     */
    private void createComment(String userId, Long postId) {
        System.out.println("\n===== 댓글 작성 =====");
        System.out.print("내용: ");
        scanner.nextLine(); // 버퍼 비우기
        String content = scanner.nextLine();
        
        try {
            boolean success = commentService.addComment(content, postId, userId);
            if (success) {
                System.out.println("댓글이 성공적으로 작성되었습니다.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("댓글 작성 실패: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("댓글 작성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 댓글 수정
     */
    private void updateComment(String userId) {
        System.out.println("\n===== 댓글 수정 =====");
        System.out.print("수정할 댓글 ID: ");
        Long commentId = (long) readInt();
        
        try {
            Comment comment = commentService.getCommentById(commentId);
            
            if (comment == null) {
                System.out.println("존재하지 않는 댓글입니다.");
                return;
            }
            
            System.out.println("현재 내용: " + comment.getContent());
            System.out.print("새 내용: ");
            scanner.nextLine(); // 버퍼 비우기
            String newContent = scanner.nextLine();
            
            boolean success = commentService.updateComment(commentId, newContent, userId);
            if (success) {
                System.out.println("댓글이 성공적으로 수정되었습니다.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("댓글 수정 실패: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("댓글 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 댓글 삭제
     */
    private void deleteComment(String userId) {
        System.out.println("\n===== 댓글 삭제 =====");
        System.out.print("삭제할 댓글 ID: ");
        Long commentId = (long) readInt();
        
        try {
            boolean success = commentService.deleteComment(commentId, userId);
            if (success) {
                System.out.println("댓글이 성공적으로 삭제되었습니다.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("댓글 삭제 실패: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("댓글 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 내 댓글 목록 표시
     */
    private void showMyComments(String userId) {
        try {
            List<Comment> comments = commentService.getCommentsByUser(userId);
            
            if (comments.isEmpty()) {
                System.out.println("작성한 댓글이 없습니다.");
                return;
            }
            
            System.out.println("\n===== 내 댓글 목록 =====");
            int count = 1;
            for (Comment comment : comments) {
                System.out.println(count + ". 게시글 ID: " + comment.getPostId() + 
                        "\n   내용: " + comment.getContent());
                count++;
            }
        } catch (SQLException e) {
            System.out.println("댓글 목록을 불러오는 중 오류가 발생했습니다: " + e.getMessage());
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