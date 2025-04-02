package com.silkastory.users;

import com.silkastory.infrastructure.database.JDBCConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UsersDao {
    /**
     * 새 사용자 저장
     */
    public void saveUser(String name, String nickname, String email, String password) {
        String sql = "INSERT INTO users(id, name, nickname, email, password) VALUES(?, ?, ?, ?, ?)";

        try(Connection conn = JDBCConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            UUID uuid = UUID.randomUUID();
            ps.setString(1, uuid.toString());
            ps.setString(2, name);
            ps.setString(3, nickname);
            ps.setString(4, email);
            ps.setString(5, password); // 실제 구현에서는 암호화된 비밀번호를 저장해야 함

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("사용자 추가 완료!");
            } else {
                System.out.println("사용자 추가 실패");
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 사용자 정보 업데이트
     */
    public boolean updateUser(String userId, String newName, String newNickname, String newPassword) {
        String sql = "UPDATE users SET name = ?, nickname = ?, password = ? WHERE id = ?";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, newNickname);
            pstmt.setString(3, newPassword); // 실제 구현에서는 암호화된 비밀번호를 저장해야 함
            pstmt.setString(4, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
        catch(SQLException e) {
            System.err.println("사용자 정보 업데이트 중 오류 발생: " + e.getMessage());
        }
        return false;
    }

    /**
     * 이메일이 이미 존재하는지 확인
     */
    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("이메일 확인 중 오류 발생: " + e.getMessage());
        }
        return false;
    }

    /**
     * 로그인 검증
     */
    public Users login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password); // 실제 구현에서는 암호화된 비밀번호와 비교해야 함
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("로그인 중 오류 발생: " + e.getMessage());
        }
        return null;
    }

    /**
     * 사용자 정보 조회
     */
    public Users getUserById(String userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("사용자 정보 조회 중 오류 발생: " + e.getMessage());
        }
        return null;
    }

    /**
     * 사용자 삭제 (실제 삭제)
     */
    public boolean deleteUser(String userId) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("사용자 삭제 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    /**
     * ResultSet에서 Users 객체로 매핑
     */
    private Users mapResultSetToUser(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String nickname = rs.getString("nickname");
        String email = rs.getString("email");
        String password = rs.getString("password");
        
        return new Users(id, name, nickname, email, password);
    }
}
