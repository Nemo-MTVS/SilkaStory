package com.silkastory.post;

import com.silkastory.category.CategoryRepository;
import java.sql.SQLException;
import java.util.List;

/**
 * 게시글 서비스 클래스
 * 비즈니스 로직을 처리하는 계층
 */
public class PostService {
    private final PostDAO postDAO;
    private final CategoryRepository categoryRepository;

    public PostService(PostDAO postDAO, CategoryRepository categoryRepository) {
        this.postDAO = postDAO;
        this.categoryRepository = categoryRepository;
    }

    /**
     * 새 게시글 추가
     */
    public boolean addPost(String userId, String title, String content, int categoryId) throws SQLException {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("제목을 입력해주세요.");
        }
        
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("내용을 입력해주세요.");
        }
        
        // 카테고리 존재 여부 검증
        if (!isCategoryValid(categoryId, userId)) {
            throw new IllegalArgumentException("유효하지 않은 카테고리입니다.");
        }
        
        Post post = new Post(title, content, userId, categoryId);
        int rows = postDAO.createPost(post);
        return rows > 0;
    }

    /**
     * 전체 게시글 목록 조회
     */
    public List<Post> getAllPosts() throws SQLException {
        return postDAO.getAllPosts();
    }

    /**
     * 특정 게시글 조회
     */
    public Post getPostById(int postId) throws SQLException {
        Post post = postDAO.getPostById(postId);
        if (post == null) {
            throw new IllegalArgumentException("해당 게시글을 찾을 수 없습니다.");
        }
        return post;
    }

    /**
     * 게시글 수정
     */
    public boolean updatePost(int postId, String userId, String title, String content, int categoryId) throws SQLException {
        Post post = postDAO.getPostById(postId);
        if (post == null) {
            throw new IllegalArgumentException("해당 게시글을 찾을 수 없습니다.");
        }
        
        if (!post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 게시글만 수정할 수 있습니다.");
        }
        
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("제목을 입력해주세요.");
        }
        
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("내용을 입력해주세요.");
        }
        
        // 카테고리 존재 여부 검증
        if (!isCategoryValid(categoryId, userId)) {
            throw new IllegalArgumentException("유효하지 않은 카테고리입니다.");
        }
        
        int rows = postDAO.updatePost(postId, title, content, categoryId);
        return rows > 0;
    }

    /**
     * 카테고리 유효성 검증
     * - 카테고리가 존재하는지 확인
     * - 사용자에게 속한 카테고리인지 확인
     */
    private boolean isCategoryValid(int categoryId, String userId) {
        try {
            return categoryRepository.findById((long) categoryId)
                .map(category -> category.getUserId().equals(userId))
                .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 게시글 삭제
     */
    public boolean deletePost(int postId, String userId) throws SQLException {
        Post post = postDAO.getPostById(postId);
        if (post == null) {
            throw new IllegalArgumentException("해당 게시글을 찾을 수 없습니다.");
        }
        
        if (!post.getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 게시글만 삭제할 수 있습니다.");
        }
        
        int rows = postDAO.deletePost(postId);
        return rows > 0;
    }

    /**
     * 특정 사용자의 게시글 목록 조회
     */
    public List<Post> getPostsByUserId(String userId) throws SQLException {
        return postDAO.getPostsByUserId(userId);
    }

    /**
     * 특정 카테고리의 게시글 목록 조회
     */
    public List<Post> getPostsByCategoryId(int categoryId) throws SQLException {
        return postDAO.getPostsByCategoryId(categoryId);
    }
}