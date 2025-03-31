package com.silkastory.Comments;

import com.silkastory.infrastructure.database.JDBCConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class CommentDAO {
    //JDBC 객체를 주입 받아서 데이터베이스 연결을 관리
    //HikariCP 연결 풀을 사용하여 성능을 최적화한다.
    private final JDBCConnection jdbcConnection;

    public CommentDAO(JDBCConnection jdbcConnection) {
        this.jdbcConnection = jdbcConnection;
    }

    // 댓글 저장
    //새 댓글은 Comments 테이블에 추가
    //?를 사용한 PreparedStatement로 SQL 인젝션 방지
    //jdbcConnection.getConnection() → HikariCP에서 DB 연결을 가져옴.
    //INSERT INTO comments (content, post_id, user_id) VALUES (?, ?, ?) 실행.
    //댓글이 추가된다.
    public void insertComment(String content, Long postId, String userId) {
        String sql = "INSERT INTO comments (content, post_id, user_id) VALUES (?, ?, ?)";
        try (Connection conn = jdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, content);
            pstmt.setLong(2, postId);
            pstmt.setString(3, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 특정 게시글의 댓글 목록 조회
    public List<Comment> getCommentsByPost(Long postId) {
        String sql = "SELECT * FROM comments WHERE post_id = ? AND is_deleted = FALSE ORDER BY created_at DESC";
        List<Comment> comments = new ArrayList<>();

        try (Connection conn = jdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, postId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                comments.add(new Comment(
                        rs.getLong("id"),
                        rs.getString("content"),
                        rs.getString("created_at"),
                        rs.getString("updated_at"),
                        rs.getString("deleted_at"),
                        rs.getBoolean("is_deleted"),
                        rs.getLong("post_id"),
                        rs.getString("user_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    // 댓글 삭제 (소프트 삭제)
    //데이터는 유지되지만 조회되지 않음.
    public void deleteComment(Long commentId) {
        String sql = "UPDATE comments SET is_deleted = TRUE, deleted_at = NOW() WHERE id = ?";
        //댓글을 물리적으로 삭제하지 않고 is_deleted = TRUE로 변경 (소프트 삭제).(삭제 되는 것처럼 처리)
        //deleted_at = NOW()로 삭제 시간을 기록.
        try (Connection conn = jdbcConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, commentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
