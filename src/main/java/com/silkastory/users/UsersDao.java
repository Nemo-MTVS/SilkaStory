package com.silkastory.users;

import com.silkastory.infrastructure.database.JDBCConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class UsersDao {
    public void saveUser(String nickname, String userName){
        String sql = "INSERT INTO users(name, nickname, id) VALUES(?, ?, ?)";

        try(Connection conn = JDBCConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            UUID uuid = UUID.randomUUID();
            ps.setString(1, userName);
            ps.setString(2, nickname);
            ps.setString(3, uuid.toString());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("사용자 추가 완료! UUID");
                // NOTE: 연결 시 써야함
                // loadUserFromDatabase();
            }else{
                System.out.println("사용자 추가 실패");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public boolean updateUser(String userId, String newNickname, String newName) {
        String sql = "UPDATE users set nickname = ?, name = ? where id = ?";


        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newNickname);
            pstmt.setString(2, newName);
            pstmt.setString(3, userId); // 기존 ID 유지

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
        catch(SQLException e) {
            System.err.println("사용자 정보 업데이트 중 오류 발생: " + e.getMessage());
        }
        return false;
    }


    public boolean deleteUser(String id) {
        String checkUserSql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkUserSql);){
            //사용자 존재 여부 확인
            checkStmt.setString(1, id);

            int affectedRows = checkStmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
