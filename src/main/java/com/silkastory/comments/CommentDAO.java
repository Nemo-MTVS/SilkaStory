package com.silkastory.comments;

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

    public CommentDAO() {
    }

    // 댓글 저장
    //새 댓글은 Comments 테이블에 추가
    //?를 사용한 PreparedStatement로 SQL 인젝션 방지
    //JDBCConnection.getConnection() → HikariCP에서 DB 연결을 가져옴.
    //INSERT INTO comments (content, post_id, user_id) VALUES (?, ?, ?) 실행.
    //댓글이 추가된다.
    public void insertComment(String content, Long postId, String userId) {
        String sql = "INSERT INTO comments (content, post_id, user_id) VALUES (?, ?, ?)";
        try (Connection conn = JDBCConnection.getConnection();
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
        String sql = "SELECT * FROM comments WHERE post_id = ?";
        List<Comment> comments = new ArrayList<>();

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, postId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                comments.add(new Comment(
                        rs.getLong("id"),
                        rs.getString("content"),
                        rs.getLong("post_id"),
                        rs.getString("user_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    public Comment getCommentById(Long commentId) {
        String sql = "SELECT * FROM comments WHERE id = ?";
        Comment comment = null;

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, commentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                comment = new Comment(
                        rs.getLong("id"),
                        rs.getString("content"),
                        rs.getLong("post_id"),
                        rs.getString("user_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comment;
    }

    public void deleteComment(Long commentId) {
        String sql = "delete FROM comments WHERE id = ?";
        Comment comment = null;

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, commentId);
            pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Comment> getCommentsByUser(String userId) {
        String sql = "SELECT * FROM comments WHERE user_id = ?";
        List<Comment> comments = null;

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                comments.add(new Comment(
                        rs.getLong("id"),
                        rs.getString("content"),
                        rs.getLong("post_id"),
                        rs.getString("user_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    public void updateComment(Long commentId, String content) {
        String sql = "UPDATE comments SET content = ? WHERE id = ?";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, content);
            pstmt.setLong(2, commentId);
            pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
