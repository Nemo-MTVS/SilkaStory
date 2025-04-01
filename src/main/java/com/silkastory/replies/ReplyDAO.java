package com.silkastory.replies;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.silkastory.infrastructure.database.JDBCConnection;

public class ReplyDAO {
    private final JDBCConnection jdbcConnection;

    public ReplyDAO(JDBCConnection jdbcConnection) {
        this.jdbcConnection = jdbcConnection;
    }

    // 답글 저장
    public void insertReply(String content, Long commentId, String userId) {
        String sql = "INSERT INTO replies (content, comment_id, user_id) VALUES (?, ?, ?)";
        try (Connection conn = jdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, content);
            pstmt.setLong(2, commentId);
            pstmt.setString(3, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 특정 댓글의 답글 조회
    public List<Reply> getRepliesByComment(Long commentId) {
        String sql = "SELECT * FROM replies WHERE comment_id = ? AND isUsed = FALSE ORDER BY createdAt ASC";
        List<Reply> replies = new ArrayList<>();

        try (Connection conn = jdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, commentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                replies.add(new Reply(
                        rs.getLong("id"),
                        rs.getString("content"),
                        rs.getString("createdAt"),
                        rs.getString("updatedAt"),
                        rs.getString("deletedAt"),
                        rs.getBoolean("isUsed"),
                        rs.getLong("comment_id"),
                        rs.getString("user_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return replies;
    }

    // 답글 삭제 (소프트 삭제)
    public void deleteReply(Long replyId) {
        String sql = "UPDATE replies SET isUsed = TRUE, deletedAt = NOW() WHERE id = ?";
        try (Connection conn = jdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, replyId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
