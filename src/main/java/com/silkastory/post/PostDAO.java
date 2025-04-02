package com.silkastory.post;

import com.silkastory.infrastructure.database.JDBCConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 게시글 데이터 액세스 객체
 */
public class PostDAO {

    /**
     * 새 게시글 생성
     */
    public int createPost(Post post) throws SQLException {
        String sql = "INSERT INTO posts (title, content, user_id, category_id, is_public) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, post.getTitle());
            pstmt.setString(2, post.getContent());
            pstmt.setString(3, post.getUserId());
            pstmt.setInt(4, post.getCategoryId());
            pstmt.setBoolean(5, true);

            return pstmt.executeUpdate();
        }
    }
    
    /**
     * 모든 게시글 조회
     */
    public List<Post> getAllPosts() throws SQLException {
        String sql = "SELECT * FROM posts";
        List<Post> posts = new ArrayList<>();
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Post post = mapResultSetToPost(rs);
                posts.add(post);
            }
        }
        
        return posts;
    }
    
    /**
     * 특정 게시글 조회
     */
    public Post getPostById(int postId) throws SQLException {
        String sql = "SELECT * FROM posts WHERE id = ?";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, postId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPost(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * 게시글 수정
     */
    public int updatePost(int postId, String title, String content, int categoryId) throws SQLException {
        String sql = "UPDATE posts SET title = ?, content = ?, category_id = ? WHERE id = ?";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setInt(3, categoryId);
            pstmt.setInt(4, postId);
            
            return pstmt.executeUpdate();
        }
    }
    
    /**
     * 게시글 삭제 (실제 삭제)
     */
    public int deletePost(int postId) throws SQLException {
        String sql = "DELETE FROM posts WHERE id = ?";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, postId);
            
            return pstmt.executeUpdate();
        }
    }
    
    /**
     * 특정 사용자의 게시글 목록 조회
     */
    public List<Post> getPostsByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM posts WHERE user_id = ?";
        List<Post> posts = new ArrayList<>();
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Post post = mapResultSetToPost(rs);
                    posts.add(post);
                }
            }
        }
        
        return posts;
    }
    
    /**
     * 특정 카테고리의 게시글 목록 조회
     */
    public List<Post> getPostsByCategoryId(int categoryId) throws SQLException {
        String sql = "SELECT * FROM posts WHERE category_id = ?";
        List<Post> posts = new ArrayList<>();
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Post post = mapResultSetToPost(rs);
                    posts.add(post);
                }
            }
        }
        
        return posts;
    }
    
    /**
     * ResultSet에서 Post 객체로 매핑
     */
    private Post mapResultSetToPost(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setId(rs.getInt("id"));
        post.setTitle(rs.getString("title"));
        post.setContent(rs.getString("content"));
        post.setUserId(rs.getString("user_id"));
        post.setCategoryId(rs.getInt("category_id"));
        
        return post;
    }

    // 게시글, 카테고리, 사용자 정보를 JOIN해서 가져오는 메서드
    public PostResponseDTO getPostWithCategoryAndUser(Long postId) throws SQLException {
        String sql = """
                    SELECT
                    p.id, p.title, p.content, c.name AS category_name, u.nickname
                    FROM posts p
                    JOIN categories c ON p.category_id = c.id
                    JOIN users u ON p.user_id = u.id
                    WHERE p.id = ?
                """;
        try(Connection conn = JDBCConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, postId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new PostResponseDTO(
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("category_name"),
                        rs.getString("nickname")
                );
            } else {
                return null; // 게시글이 존재하지 않는 경우
            }
        } catch (SQLException e) {
            System.err.println("게시글 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
