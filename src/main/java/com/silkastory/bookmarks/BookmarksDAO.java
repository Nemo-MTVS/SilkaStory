package com.silkastory.bookmarks;

import com.silkastory.infrastructure.database.JDBCConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookmarksDAO{
    //생성자
    //의존성 주입
    //BookmarksDAO는 JDBCConnection에 의존하고 있습니다. 이 생성자를 통해 FavoriteDAO가 초기화될 때
    // JDBCConnection 객체를 주입받습니다.
    // 이는 의존성 주입(Dependency Injection) 패턴의 일종입니다.
    private final JDBCConnection connection;

    public BookmarksDAO(JDBCConnection connection) {
        this.connection = connection;
    }

    public void addBookmark(String userId, int postId, String name) throws SQLException {
        String sql = "INSERT INTO bookmarks (user_id, post_id, name) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = null;
        //conn은 Connection 객체입니다. SQL 쿼리 sql을 준비하고, 그 결과로 PreparedStatement 객체를 생성합니다.
        try(Connection conn = JDBCConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, userId);
            pstmt.setInt(2, postId);
            pstmt.setString(3, name);
            pstmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
            throw  e;
        }
    }

    // 수정 이름만 userId, postId, name을 받는다
    public void updateBookmark(String userId, int postId, String name) throws SQLException {
        String sql = "UPDATE bookmarks SET name = ? WHERE user_id = ? AND post_id = ?";
        PreparedStatement preparedStatement = null;
        try(Connection conn = JDBCConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, name);
            pstmt.setString(2, userId);
            pstmt.setInt(3, postId);
            pstmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
            throw e;
        }
    }

    //삭제
    public void removeBookmark(String userId, int postId) throws SQLException {
        String sql = "DELETE FROM bookmarks WHERE user_id = ? AND post_id = ?";
        PreparedStatement preparedStatement = null;
        try(Connection conn = JDBCConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, userId);
            pstmt.setInt(2, postId);
            pstmt.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
            throw e;
        }
    }

    public boolean existBookmark(String userId, int postId) throws SQLException {
        String sql = """
                SELECT EXISTS (
                  SELECT 1 FROM bookmarks WHERE user_id = ? AND post_id = ?
                ) AS is_bookmarked;
                """;
        try (Connection conn = JDBCConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, userId);
                pstmt.setInt(2, postId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getBoolean("is_bookmarked");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            }
        return false;
    }

    public List<BookmarksDTO> getAllBookmarks(String userId) {
        // 즐겨찾기 테이블에서 모든 데이터를 조회하는 SQL 쿼리
        String sql = """
                    SELECT
                        b.id as bookmark_id,
                        b.user_id,
                        b.post_id,
                        u.nickname
                        FROM bookmarks b
                    join SilkaStory.users u on u.id = b.user_id
                    where u.id = ?
                """;

        List<BookmarksDTO> bookmarksList = new ArrayList<>(); // 결과로 반환할 즐겨찾기 리스트

        try(Connection conn = JDBCConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                BookmarksDTO bookmarks = new BookmarksDTO(
                        rs.getLong("bookmark_id"),
                        rs.getString("user_id"),
                        rs.getInt("post_id"),
                        rs.getString("name")
                );
                bookmarksList.add(bookmarks); // 결과에서 각 즐겨찾기를 리스트에 추가
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return bookmarksList;
    }
}
