package com.silkastory;

import com.silkastory.bookmarks.BookmarksView;
import com.silkastory.category.CategoryRepository;
import com.silkastory.category.CategoryRepositoryImpl;
import com.silkastory.category.CategoryView;
import com.silkastory.comments.CommentDAO;
import com.silkastory.comments.CommentService;
import com.silkastory.comments.CommentView;
import com.silkastory.notifications.NotificationsView;
import com.silkastory.post.PostDAO;
import com.silkastory.post.PostService;
import com.silkastory.post.PostView;
import com.silkastory.replies.RepliesDAO;
import com.silkastory.replies.RepliesService;
import com.silkastory.replies.RepliesView;
import com.silkastory.subscriptions.SubscriptionsView;
import com.silkastory.users.UserContext;
import com.silkastory.users.Users;
import com.silkastory.users.UsersView;

import java.util.Scanner;

/**
 * 실크 스토리 애플리케이션 메인 클래스
 * 모든 기능을 통합해서 관리하는 메인 서비스
 */
public class SilkaStoryApplication {
    private final UsersView usersView;
    private final BookmarksView bookmarksView;
    private final CategoryView categoryView;
    private final CommentView commentView;
    private final NotificationsView notificationsView;
    private final PostView postView;
    private final RepliesView repliesView;
    private final SubscriptionsView subscriptionsView;
    
    private final Scanner scanner;
    private final UserContext userContext;
    private Long currentPostId = null;
    private Long currentCommentId = null;

    public SilkaStoryApplication() {
        // Scanner 인스턴스 생성
        this.scanner = new Scanner(System.in);
        
        // UserContext 인스턴스 가져오기
        this.userContext = UserContext.getInstance();
        
        // DAO 객체 생성
        PostDAO postDAO = new PostDAO();
        CommentDAO commentDAO = new CommentDAO();
        RepliesDAO repliesDAO = new RepliesDAO();
        
        // Repository 객체 생성
        CategoryRepository categoryRepository = new CategoryRepositoryImpl();
        
        // Service 객체 생성
        PostService postService = new PostService(postDAO, categoryRepository);
        CommentService commentService = new CommentService(commentDAO, postDAO);
        RepliesService repliesService = new RepliesService(repliesDAO, commentDAO);
        
        // View 객체 생성
        this.usersView = new UsersView();
        this.bookmarksView = new BookmarksView();
        this.categoryView = new CategoryView();
        this.commentView = new CommentView(commentService, scanner);
        this.notificationsView = new NotificationsView();
        this.postView = new PostView(postService, scanner);
        this.repliesView = new RepliesView(repliesService, scanner);
        this.subscriptionsView = new SubscriptionsView();
    }

    /**
     * 메인 애플리케이션 실행
     */
    public void run() {
        boolean running = true;
        
        System.out.println("===== 실크 스토리 애플리케이션 =====");
        System.out.println("환영합니다!");
        
        while (running) {
            if (!userContext.isLoggedIn()) {
                // 로그인되지 않은 경우
                showLoginMenu();
            } else {
                // 로그인된 경우
                showMainMenu();
            }
        }
    }

    /**
     * 로그인 메뉴 표시
     */
    private void showLoginMenu() {
        System.out.println("\n===== 시작 메뉴 =====");
        System.out.println("1. 로그인");
        System.out.println("2. 회원가입");
        System.out.println("0. 종료");
        System.out.print("메뉴 선택: ");
        
        int choice = readInt();
        
        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 0:
                System.out.println("애플리케이션을 종료합니다. 감사합니다!");
                System.exit(0);
                break;
            default:
                System.out.println("잘못된 선택입니다. 다시 시도해주세요.");
        }
    }

    /**
     * 메인 메뉴 표시
     */
    private void showMainMenu() {
        int unreadCount = 0; // 실제로는 알림 서비스에서 읽지 않은 알림 개수를 가져와야 함
        String currentUsername = userContext.getCurrentUser().getNickname();
        
        System.out.println("\n===== 메인 메뉴 ===== (로그인: " + currentUsername + ")");
        if (unreadCount > 0) {
            System.out.println("📢 읽지 않은 알림이 " + unreadCount + "개 있습니다.");
        }
        System.out.println("1. 게시글 관리");
        System.out.println("2. 댓글 관리");
        System.out.println("3. 답글 관리");
        System.out.println("4. 북마크 관리");
        System.out.println("5. 카테고리 관리");
        System.out.println("6. 구독 관리");
        System.out.println("7. 알림 관리");
        System.out.println("8. 사용자 정보 관리");
        System.out.println("9. 로그아웃");
        System.out.println("0. 종료");
        System.out.print("메뉴 선택: ");
        
        int choice = readInt();
        String userId = userContext.getCurrentUserId();
        
        switch (choice) {
            case 1:
                postView.showMenu(userId);
                break;
            case 2:
                System.out.print("게시글 ID를 입력하세요: ");
                currentPostId = (long) readInt();
                scanner.nextLine(); // 버퍼 비우기
                commentView.showMenu(userId, currentPostId);
                break;
            case 3:
                System.out.print("댓글 ID를 입력하세요: ");
                currentCommentId = (long) readInt();
                scanner.nextLine(); // 버퍼 비우기
                repliesView.showMenu(userId, currentCommentId);
                break;
            case 4:
                bookmarksView.showMenu(userId);
                break;
            case 5:
                categoryView.showMenu(userId);
                break;
            case 6:
                subscriptionsView.showMenu(userId);
                break;
            case 7:
                notificationsView.showMenu(userId);
                break;
            case 8:
                manageUserInfo();
                break;
            case 9:
                logout();
                break;
            case 0:
                System.out.println("애플리케이션을 종료합니다. 감사합니다!");
                System.exit(0);
                break;
            default:
                System.out.println("잘못된 선택입니다. 다시 시도해주세요.");
        }
    }

    /**
     * 사용자 정보 관리
     */
    private void manageUserInfo() {
        System.out.println("\n===== 사용자 정보 관리 =====");
        usersView.showMenu();
    }

    /**
     * 로그인 처리
     */
    private void login() {
        // UsersView의 로그인 기능 사용
        usersView.login();
    }

    /**
     * 회원가입 처리
     */
    private void register() {
        // UsersView의 회원가입 기능으로 이동
        usersView.registerUser();
    }

    /**
     * 로그아웃 처리
     */
    private void logout() {
        System.out.println("로그아웃 되었습니다.");
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

    /**
     * 애플리케이션 메인 메서드
     */
    public static void main(String[] args) {
        SilkaStoryApplication app = new SilkaStoryApplication();
        app.run();
    }
}