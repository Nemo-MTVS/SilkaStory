package com.silkastory.post;

import com.silkastory.common.RepositoryFactory;
import com.silkastory.notifications.NotificationsRepository;
import com.silkastory.notifications.NotificationsService;
import com.silkastory.subscriptions.SubscriptionsRepository;
import com.silkastory.subscriptions.SubscriptionsService;
import com.silkastory.users.Users;
import com.silkastory.users.UsersDao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

/**
 * 게시글 기능에 대한 View 클래스
 * 사용자와 직접 상호작용하여 입력을 받고 결과를 출력
 */
public class PostView {
    private final PostService postService;
    private final Scanner scanner;
    private final SimpleDateFormat dateFormat;
    private final SubscriptionsService subscriptionsService;
    private final NotificationsService notificationsService;
    private final UsersDao usersDao;

    public PostView(PostService postService, Scanner scanner) {
        this.usersDao = new UsersDao();
        this.subscriptionsService = new SubscriptionsService(RepositoryFactory.getRepository(SubscriptionsRepository.class));
        this.notificationsService = new NotificationsService(RepositoryFactory.getRepository(NotificationsRepository.class), subscriptionsService);
        this.postService = postService;
        this.scanner = scanner;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public void printMenus() {
        System.out.println("\n===== 게시글 관리 =====");
        System.out.println("1. 게시글 목록 보기");
        System.out.println("2. 게시글 상세 보기");
        System.out.println("3. 게시글 작성하기");
        System.out.println("4. 게시글 수정하기");
        System.out.println("5. 게시글 삭제하기");
        System.out.println("6. 내 게시글 보기");
        System.out.println("7. 카테고리별 게시글 보기");
        System.out.println("0. 돌아가기");
        System.out.print("메뉴 선택: ");
    }

    /**
     * 게시글 메인 메뉴 실행
     * 
     * @param userId 현재 로그인한 사용자 ID
     */
    public void showMenu(String userId) {
        boolean running = true;
        
        while (running) {
            printMenus();
            
            int choice = readInt();
            
            switch (choice) {
                case 1:
                    showPostList();
                    break;
                case 2:
                    showPostDetail();
                    break;
                case 3:
                    createPost(userId);
                    break;
                case 4:
                    updatePost(userId);
                    break;
                case 5:
                    deletePost(userId);
                    break;
                case 6:
                    showMyPosts(userId);
                    break;
                case 7:
                    showPostsByCategory();
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
     * 게시글 목록 출력
     */
    private void showPostList() {
        System.out.println("\n===== 게시글 목록 =====");
        try {
            List<PostDTO> posts = postService.getAllPosts();
            
            if (posts.isEmpty()) {
                System.out.println("게시글이 없습니다.");
                return;
            }
            
            System.out.println("번호\t제목\t작성자\t카테고리명");
            for (PostDTO post : posts) {
                System.out.printf("%d\t%s\t%s\t%s\\n",
                    post.getId(), 
                    post.getTitle(),
                    post.getWriter(),
                    post.getCategoryName()
                );
            }
        } catch (SQLException e) {
            System.out.println("게시글 목록을 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 게시글 상세 정보 출력
     */
    private void showPostDetail() {
        System.out.println("\n===== 게시글 상세 정보 =====");
        System.out.print("조회할 게시글 번호: ");
        int postId = readInt();
        
        try {
            Post post = postService.getPostById(postId);
            
            System.out.println("제목: " + post.getTitle());
            System.out.println("작성자: " + post.getUserId());
            System.out.println("카테고리: " + post.getCategoryId());
            System.out.println("내용: " + post.getContent());
        } catch (Exception e) {
            System.out.println("게시글 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 게시글 작성
     */
    private void createPost(String userId) {
        System.out.println("\n===== 게시글 작성 =====");
        
        System.out.print("제목: ");
        String title = scanner.nextLine();
        
        System.out.print("내용: ");
        String content = scanner.nextLine();
        
        System.out.print("카테고리 ID: ");
        int categoryId = readInt();
        
        try {

            boolean success = postService.addPost(userId, title, content, categoryId);
            // 구독자 목록 호출
            // 알림 발송
            Users user = usersDao.getUserById(userId);

            notificationsService.sendNotificationToSubscribers(user.getNickname() + "님이 새로운 게시글을 작성하였습니다", userId);
            if (success) {
                System.out.println("게시글이 성공적으로 작성되었습니다.");
            } else {
                System.out.println("게시글 작성에 실패했습니다.");
            }
        } catch (Exception e) {
            System.out.println("게시글 작성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 게시글 수정
     */
    private void updatePost(String userId) {
        System.out.println("\n===== 게시글 수정 =====");
        
        System.out.print("수정할 게시글 번호: ");
        int postId = readInt();
        
        try {
            Post post = postService.getPostById(postId);
            
            if (!post.getUserId().equals(userId)) {
                System.out.println("본인의 게시글만 수정할 수 있습니다.");
                return;
            }
            
            System.out.println("현재 제목: " + post.getTitle());
            System.out.print("새 제목: ");
            String title = scanner.nextLine();
            
            System.out.println("현재 내용: " + post.getContent());
            System.out.print("새 내용: ");
            String content = scanner.nextLine();
            
            System.out.println("현재 카테고리: " + post.getCategoryId());
            System.out.print("새 카테고리 ID: ");
            int categoryId = readInt();
            
            boolean success = postService.updatePost(postId, userId, title, content, categoryId);
            if (success) {
                System.out.println("게시글이 성공적으로 수정되었습니다.");
            } else {
                System.out.println("게시글 수정에 실패했습니다.");
            }
        } catch (Exception e) {
            System.out.println("게시글 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 게시글 삭제
     */
    private void deletePost(String userId) {
        System.out.println("\n===== 게시글 삭제 =====");
        
        System.out.print("삭제할 게시글 번호: ");
        int postId = readInt();
        
        try {
            Post post = postService.getPostById(postId);
            
            if (!post.getUserId().equals(userId)) {
                System.out.println("본인의 게시글만 삭제할 수 있습니다.");
                return;
            }
            
            System.out.println("삭제할 게시글: " + post.getTitle());
            System.out.print("정말로 삭제하시겠습니까? (y/n): ");
            String confirm = scanner.nextLine().toLowerCase();
            
            if (confirm.equals("y")) {
                boolean success = postService.deletePost(postId, userId);
                if (success) {
                    System.out.println("게시글이 성공적으로 삭제되었습니다.");
                } else {
                    System.out.println("게시글 삭제에 실패했습니다.");
                }
            } else {
                System.out.println("게시글 삭제가 취소되었습니다.");
            }
        } catch (Exception e) {
            System.out.println("게시글 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 내 게시글 보기
     */
    private void showMyPosts(String userId) {
        System.out.println("\n===== 내 게시글 목록 =====");
        try {
            List<Post> posts = postService.getPostsByUserId(userId);
            
            if (posts.isEmpty()) {
                System.out.println("작성한 게시글이 없습니다.");
                return;
            }
            
            System.out.println("번호\t제목\t작성일");
            for (Post post : posts) {
                System.out.printf("%d\t%s\n",
                    post.getId(), 
                    post.getTitle()
                        );
            }
        } catch (SQLException e) {
            System.out.println("게시글 목록을 가져오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 카테고리별 게시글 보기
     */
    private void showPostsByCategory() {
        System.out.println("\n===== 카테고리별 게시글 =====");
        System.out.print("카테고리 ID: ");
        int categoryId = readInt();
        
        try {
            List<Post> posts = postService.getPostsByCategoryId(categoryId);
            
            if (posts.isEmpty()) {
                System.out.println("해당 카테고리에 게시글이 없습니다.");
                return;
            }
            
            System.out.println("번호\t제목\t작성자\t작성일");
            for (Post post : posts) {
                System.out.printf("%d\t%s\t%s\n",
                    post.getId(), 
                    post.getTitle(), 
                    post.getUserId());
            }
        } catch (SQLException e) {
            System.out.println("게시글 목록을 가져오는 중 오류가 발생했습니다: " + e.getMessage());
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
