package com.silkastory.service;

import com.silkastory.infrastructure.database.JDBCConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PostDAO {

//    private JDBCConnection jdbcConnection = new JDBCConnection();

//    public String getPost(String userid, String title,String content) {
//        String sql = "SELECT id FROM USER WHERE id = ?";
//        try(Connection conn = jdbcConnection.getConnection();
//            PreparedStatement ps = conn.prepareStatement(sql){
//                ps.setString(1, userid);
//                try(ResultSet rs = pstmt.executeQuery())
//                {
//                    if(rs.next())
//                    {
//                        return rs.getString("id");
//                    }
//                }
//        }
//    }

    public int createPost(Post postdata) throws SQLException {
        String sql = "INSERT INTO posts(title, content, user_id, category_id) VALUES (?, ?, ?, ?)";
        //HikakiCP로 관리하는 DB 연결을 가져오는 부분
        try(Connection conn = JDBCConnection.getConnection();
            //SQL문을 실행할 pstmt 객체 생성
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, postdata.getTitle());
            pstmt.setString(2, postdata.getContent());
            pstmt.setString(3, postdata.getId());
            pstmt.setInt(4, postdata.getCategoty_id());
            pstmt.executeUpdate();
            //데이터 변경 SQL 실행하여 데이터베이스의 새로운 게시글 저장
//             int rows = pstmt.executeUpdate();
//
//             System.out.println("삽입된 행의 개수 : " + rows);
        }
        catch(Exception e){
            e.printStackTrace();
            throw e;
        }
        return 0;
    }


}
