package com.silkastory.comments;

import com.silkastory.post.PostDAO;

import java.sql.SQLException;
import java.util.List;

/**
 * 댓글 서비스 클래스 - 비즈니스 로직 처리
 */
public class CommentService {
    
    private final CommentDAO commentDAO;
    private final PostDAO postDAO;
    
    public CommentService(CommentDAO commentDAO, PostDAO postDAO) {
        this.commentDAO = commentDAO;
        this.postDAO = postDAO;
    }
    
    /**
     * 새 댓글 생성
     */
    public boolean addComment(String content, Long postId, String userId) throws SQLException {
        // 유효성 검사
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용은 비어있을 수 없습니다.");
        }
        
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 게시글 ID입니다.");
        }
        
        // 게시글 존재 여부 확인
        if (postDAO.getPostById(postId.intValue()) == null) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }
        
        commentDAO.insertComment(content, postId, userId);
        return true;
    }
    
    /**
     * 특정 게시글의 모든 댓글 조회
     */
    public List<Comment> getCommentsByPost(Long postId) throws SQLException {
        if (postId == null || postId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 게시글 ID입니다.");
        }
        
        return commentDAO.getCommentsByPost(postId);
    }
    
    /**
     * 특정 댓글 조회
     */
    public Comment getCommentById(Long commentId) throws SQLException {
        if (commentId == null || commentId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 댓글 ID입니다.");
        }
        
        return commentDAO.getCommentById(commentId);
    }
    
    /**
     * 댓글 삭제
     */
    public boolean deleteComment(Long commentId, String userId) throws SQLException {
        Comment comment = commentDAO.getCommentById(commentId);
        
        if (comment == null) {
            throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
        }
        
        // 작성자만 삭제 가능
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("자신의 댓글만 삭제할 수 있습니다.");
        }
        
        commentDAO.deleteComment(commentId);
        return true;
    }
    
    /**
     * 댓글 수정
     */
    public boolean updateComment(Long commentId, String content, String userId) throws SQLException {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용은 비어있을 수 없습니다.");
        }
        
        Comment comment = commentDAO.getCommentById(commentId);
        
        if (comment == null) {
            throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
        }
        
        // 작성자만 수정 가능
        if (!comment.getUserId().equals(userId)) {
            throw new IllegalArgumentException("자신의 댓글만 수정할 수 있습니다.");
        }
        
        commentDAO.updateComment(commentId, content);
        return true;
    }
    
    /**
     * 특정 사용자의 모든 댓글 조회
     */
    public List<Comment> getCommentsByUser(String userId) throws SQLException {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 사용자 ID입니다.");
        }
        
        return commentDAO.getCommentsByUser(userId);
    }
}
