package com.silkastory.replies;

import com.silkastory.comments.CommentDAO;

import java.sql.SQLException;
import java.util.List;

/**
 * 답글 서비스 클래스 - 비즈니스 로직 처리
 */
public class RepliesService {
    
    private final RepliesDAO repliesDAO;
    private final CommentDAO commentDAO;
    
    public RepliesService(RepliesDAO repliesDAO, CommentDAO commentDAO) {
        this.repliesDAO = repliesDAO;
        this.commentDAO = commentDAO;
    }
    
    /**
     * 새 답글 생성
     */
    public boolean addReply(String content, Long commentId, String userId) throws SQLException {
        // 유효성 검사
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("답글 내용은 비어있을 수 없습니다.");
        }
        
        if (commentId == null || commentId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 댓글 ID입니다.");
        }
        
        // 댓글 존재 여부 확인
        if (commentDAO.getCommentById(commentId) == null) {
            throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
        }
        
        repliesDAO.insertReply(content, commentId, userId);
        return true;
    }
    
    /**
     * 특정 댓글의 모든 답글 조회
     */
    public List<Reply> getRepliesByComment(Long commentId) throws SQLException {
        if (commentId == null || commentId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 댓글 ID입니다.");
        }
        
        return repliesDAO.getRepliesByComment(commentId);
    }
    
    /**
     * 특정 답글 조회
     */
    public Reply getReplyById(Long replyId) throws SQLException {
        if (replyId == null || replyId <= 0) {
            throw new IllegalArgumentException("유효하지 않은 답글 ID입니다.");
        }
        
        return repliesDAO.getReplyById(replyId);
    }
    
    /**
     * 답글 삭제
     */
    public boolean deleteReply(Long replyId, String userId) throws SQLException {
        Reply reply = repliesDAO.getReplyById(replyId);
        
        if (reply == null) {
            throw new IllegalArgumentException("존재하지 않는 답글입니다.");
        }
        
        // 작성자만 삭제 가능
        if (!reply.getUserId().equals(userId)) {
            throw new IllegalArgumentException("자신의 답글만 삭제할 수 있습니다.");
        }
        
        repliesDAO.deleteReply(replyId);
        return true;
    }
    
    /**
     * 답글 수정
     */
    public boolean updateReply(Long replyId, String content, String userId) throws SQLException {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("답글 내용은 비어있을 수 없습니다.");
        }
        
        Reply reply = repliesDAO.getReplyById(replyId);
        
        if (reply == null) {
            throw new IllegalArgumentException("존재하지 않는 답글입니다.");
        }
        
        // 작성자만 수정 가능
        if (!reply.getUserId().equals(userId)) {
            throw new IllegalArgumentException("자신의 답글만 수정할 수 있습니다.");
        }
        
        repliesDAO.updateReply(replyId, content);
        return true;
    }
    
    /**
     * 특정 사용자의 모든 답글 조회
     */
    public List<Reply> getRepliesByUser(String userId) throws SQLException {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 사용자 ID입니다.");
        }
        
        return repliesDAO.getRepliesByUser(userId);
    }
} 