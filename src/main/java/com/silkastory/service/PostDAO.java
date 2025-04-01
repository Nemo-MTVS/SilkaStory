package com.silkastory.service;

import com.silkastory.infrastructure.database.JDBCConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostDAO {

    // 게시글, 카테고리, 사용자 정보를 JOIN해서 가져오는 메서드
    public Post getPostWithCategoryAndUser(Long postId) throws SQLException {
        String sql = "SELECT p.id, p.title, p.content, p.is_public, c.name AS category_name, u.name " +
                "FROM posts p " +
                "JOIN categories c ON p.category_id = c.id " +
                "JOIN users u ON p.user_id = u.id " +
                "WHERE p.id = ?";

        try(Connection conn = JDBCConnection.getConnection())
        {

        }

        //
    }



    public int createPost(Post postdata) throws SQLException {
        String sql = "INSERT INTO posts (title, content, user_id, category_id, is_public) VALUES (?, ?, ?, ?, ?)";
        //HikakiCP로 관리하는 DB 연결을 가져오는 부분
        try (Connection conn = JDBCConnection.getConnection();
             //SQL문을 실행할 pstmt 객체 생성
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, postdata.getTitle());
            pstmt.setString(2, postdata.getContent());
            pstmt.setString(3, postdata.getId());
            pstmt.setInt(4, postdata.getCategoty_id());
            pstmt.setBoolean(5, postdata.ispublic());
             return pstmt.executeUpdate(); // 삽입된 행의 개수 반환
            //데이터 변경 SQL 실행하여 데이터베이스의 새로운 게시글 저장
//             int rows = pstmt.executeUpdate();
//
//             System.out.println("삽입된 행의 개수 : " + rows);
        } catch (SQLException e) {
            System.err.println("게시글 생성 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

    }

    public int updatePost(Post postdata) throws SQLException {
        String sql = "UPDATE posts SET title = ?, content = ?, category_id = ? WHERE id = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, postdata.getTitle());
            pstmt.setString(2, postdata.getContent());
            pstmt.setString(3, postdata.getId());
            pstmt.setInt(4, postdata.getCategoty_id());
            return pstmt.executeUpdate();  // 수정된 행의 개수 반환
        } catch (SQLException e) {
            System.err.println("게시글 수정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public int deletePost(Long postId) throws SQLException {
        String sql = "DELETE FROM posts WHERE id = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, postId);  // 게시글 ID
            return pstmt.executeUpdate();  // 삭제된 행의 개수 반환
        } catch (SQLException e) {
            System.err.println("게시글 삭제 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

//    public Post getPostById(Long postId) throws SQLException {
//        String sql = "SELECT * FROM posts WHERE id = ?";
//        Post post = null;
//
//        try (Connection conn = JDBCConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setLong(1, postId);
//            ResultSet rs = pstmt.executeQuery();
//
//            if (rs.next()) {
//                post = new Post(
//                        rs.getLong("id"),
//                        rs.getString("title"),
//                        rs.getString("content"),
//                        rs.getString("user_id"),
//                        rs.getInt("category_id")
//                );
//            }
//        } catch (SQLException e) {
//            System.err.println("게시글 조회 중 오류 발생: " + e.getMessage());
//            e.printStackTrace();
//            throw e;
//        }
//        return post;
//    }

}
