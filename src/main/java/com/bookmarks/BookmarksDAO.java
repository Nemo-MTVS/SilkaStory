package com.bookmarks;

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

    public static void addBookmark(String username, int postId, Long name) {
        String sql = "INSERT INTO Bookmarks (user_id, post_id, name) VALUES (?, ?, ?)";
        PreparedStatement preparedStatement = null;
        //conn은 Connection 객체입니다. SQL 쿼리 sql을 준비하고, 그 결과로 PreparedStatement 객체를 생성합니다.
        try(Connection conn = JDBCConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            pstmt.setInt(2, postId);
            pstmt.setLong(3, name);
            pstmt.executeUpdate();
        }catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    //삭제
    public void removeBookmark(Bookmarks bookmarks) {
        String sql = "DELETE FROM Bookmarks WHERE user_id = ? AND post_id = ?";
        PreparedStatement preparedStatement = null;
        try(Connection conn = JDBCConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, bookmarks.getUserId());
            pstmt.setInt(2, bookmarks.getPostId());
            pstmt.setLong(3, bookmarks.getName());
            pstmt.executeUpdate();
        }catch(SQLException e)
        {
            e.printStackTrace();
        }

        }
        //조회
        public static List<Long> getAllBookmarks(String userId) {
            // 즐겨찾기 테이블에서 user_id에 해당하는 post_id를 조회하는 SQL 쿼리
            String sql = "SELECT * FROM Bookmarks WHERE user_id = ?";
            List<Long> bookmarks = new ArrayList<>(); // 결과로 반환할 게시글 ID 리스트

            try(Connection conn = JDBCConnection.getConnection();// JDBC 연결을 가져옵니다.
                PreparedStatement pstmt = conn.prepareStatement(sql)){// SQL문을 준비합니다.
                pstmt.setString(1, userId); // ?에 userId 값을 설정합니다.
                ResultSet rs = pstmt.executeQuery();// 쿼리를 실행하여 결과를 얻습니다.

                while(rs.next()){// 결과가 있을 때마다 반복
                    bookmarks.add(rs.getLong("post_id")); // 결과에서 post_id 값을 리스트에 추가
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return bookmarks;
        }


}
