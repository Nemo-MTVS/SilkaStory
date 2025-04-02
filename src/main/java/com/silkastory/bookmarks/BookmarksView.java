package com.silkastory.bookmarks;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * 북마크 기능에 대한 View 클래스
 * 사용자와 직접 상호작용하여 입력을 받고 결과를 출력
 */
public class BookmarksView {
    private final BookmarksService bookmarksService;
    private final Scanner scanner;

    public BookmarksView() {
        this.bookmarksService = new BookmarksService(new BookmarksDAO());
        this.scanner = new Scanner(System.in);
    }

    /**
     * 북마크 메인 메뉴 실행
     * 
     * @param userId 현재 로그인한 사용자 ID
     */
    public void showMenu(String userId) {
        boolean running = true;
        
        while (running) {
            System.out.println("\n===== 북마크 관리 =====");
            System.out.println("1. 북마크 목록 보기");
            System.out.println("2. 북마크 추가하기");
            System.out.println("3. 북마크 수정하기");
            System.out.println("4. 북마크 삭제하기");
            System.out.println("5. 북마크 여부 확인하기");
            System.out.println("0. 돌아가기");
            System.out.print("메뉴 선택: ");
            
            int choice = readInt();
            
            switch (choice) {
                case 1:
                    showAllBookmarks(userId);
                    break;
                case 2:
                    addBookmark(userId);
                    break;
                case 3:
                    updateBookmark(userId);
                    break;
                case 4:
                    removeBookmark(userId);
                    break;
                case 5:
                    checkBookmark(userId);
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
     * 모든 북마크 목록 표시
     */
    private void showAllBookmarks(String userId) {
        System.out.println("\n===== 북마크 목록 =====");
        try {
            List<BookmarksDTO> bookmarks = bookmarksService.getBookmarks(userId, -1);
            
            if (bookmarks.isEmpty()) {
                System.out.println("북마크가 없습니다.");
                return;
            }
            
            System.out.println("번호\t사용자ID\t게시글ID\t이름\t별명");
            int count = 1;
            for (BookmarksDTO bookmark : bookmarks) {
                System.out.printf("%d\t%s\t%d\t%s\t%s\n", 
                    count++, 
                    bookmark.getUserId(), 
                    bookmark.getPostId(), 
                    bookmark.getName(),
                    bookmark.getNickname());
            }
        } catch (SQLException e) {
            System.out.println("북마크 목록을 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 북마크 추가
     */
    private void addBookmark(String userId) {
        System.out.println("\n===== 북마크 추가 =====");
        
        System.out.print("게시글 ID: ");
        int postId = readInt();
        
        System.out.print("북마크 이름: ");
        String name = scanner.nextLine();
        
        try {
            bookmarksService.saveBookmarks(userId, postId, name);
            System.out.println("북마크가 성공적으로 추가되었습니다.");
        } catch (SQLException e) {
            System.out.println("북마크 추가 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 북마크 수정
     */
    private void updateBookmark(String userId) {
        System.out.println("\n===== 북마크 수정 =====");
        
        System.out.print("수정할 게시글 ID: ");
        int postId = readInt();
        
        try {
            if (!bookmarksService.isBookmarked(userId, postId)) {
                System.out.println("해당 게시글은 북마크에 없습니다.");
                return;
            }
            
            System.out.print("새 북마크 이름: ");
            String name = scanner.nextLine();
            
            bookmarksService.updateBookmarks(userId, postId, name);
            System.out.println("북마크가 성공적으로 수정되었습니다.");
        } catch (SQLException e) {
            System.out.println("북마크 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 북마크 삭제
     */
    private void removeBookmark(String userId) {
        System.out.println("\n===== 북마크 삭제 =====");
        
        System.out.print("삭제할 게시글 ID: ");
        int postId = readInt();
        
        try {
            if (!bookmarksService.isBookmarked(userId, postId)) {
                System.out.println("해당 게시글은 북마크에 없습니다.");
                return;
            }
            
            bookmarksService.deleteBookmarks(userId, postId);
            System.out.println("북마크가 성공적으로 삭제되었습니다.");
        } catch (SQLException e) {
            System.out.println("북마크 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 북마크 여부 확인
     */
    private void checkBookmark(String userId) {
        System.out.println("\n===== 북마크 여부 확인 =====");
        
        System.out.print("확인할 게시글 ID: ");
        int postId = readInt();
        
        try {
            boolean isBookmarked = bookmarksService.isBookmarked(userId, postId);
            if (isBookmarked) {
                System.out.println("해당 게시글은 북마크에 추가되어 있습니다.");
            } else {
                System.out.println("해당 게시글은 북마크에 추가되어 있지 않습니다.");
            }
        } catch (SQLException e) {
            System.out.println("북마크 확인 중 오류가 발생했습니다: " + e.getMessage());
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