package com.silkastory.replies;

import com.silkastory.infrastructure.database.JDBCConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 답글 데이터 액세스 객체
 */
public class RepliesDAO {
    
    /**
     * 새 답글 생성
     */
    public void insertReply(String content, Long commentId, String userId) throws SQLException {
        String sql = "INSERT INTO replies (content, comment_id, user_id) VALUES (?, ?, ?)";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, content);
            pstmt.setLong(2, commentId);
            pstmt.setString(3, userId);
            
            pstmt.executeUpdate();
        }
    }
    
    /**
     * 특정 댓글의 모든 답글 조회
     */
    public List<Reply> getRepliesByComment(Long commentId) throws SQLException {
        String sql = "SELECT * FROM replies WHERE comment_id = ?";
        List<Reply> replies = new ArrayList<>();
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, commentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reply reply = mapResultSetToReply(rs);
                    replies.add(reply);
                }
            }
        }
        
        return replies;
    }
    
    /**
     * 특정 답글 조회
     */
    public Reply getReplyById(Long replyId) throws SQLException {
        String sql = "SELECT * FROM replies WHERE id = ?";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, replyId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReply(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * 답글 삭제 (실제 삭제)
     */
    public void deleteReply(Long replyId) throws SQLException {
        String sql = "DELETE FROM replies WHERE id = ?";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, replyId);
            
            pstmt.executeUpdate();
        }
    }
    
    /**
     * 특정 사용자의 모든 답글 조회
     */
    public List<Reply> getRepliesByUser(String userId) throws SQLException {
        String sql = "SELECT * FROM replies WHERE user_id = ?";
        List<Reply> replies = new ArrayList<>();
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reply reply = mapResultSetToReply(rs);
                    replies.add(reply);
                }
            }
        }
        
        return replies;
    }
    
    /**
     * 답글 내용 수정
     */
    public void updateReply(Long replyId, String content) throws SQLException {
        String sql = "UPDATE replies SET content = ? WHERE id = ?";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, content);
            pstmt.setLong(2, replyId);
            
            pstmt.executeUpdate();
        }
    }
    
    /**
     * ResultSet에서 Reply 객체로 매핑
     */
    private Reply mapResultSetToReply(ResultSet rs) throws SQLException {
        Reply reply = new Reply();
        reply.setId(rs.getLong("id"));
        reply.setContent(rs.getString("content"));
        reply.setCommentId(rs.getLong("comment_id"));
        reply.setUserId(rs.getString("user_id"));
        
        return reply;
    }
} 