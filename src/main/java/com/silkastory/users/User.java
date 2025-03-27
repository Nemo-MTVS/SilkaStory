package com.silkastory.users;

import com.silkastory.infrastructure.database.JDBCConnection;

import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class User {

    public static void main(String[] args) {


        Scanner scanner = new Scanner(System.in);

        System.out.println("==== 회원가입 ====");

        //id 입력 받기
        String userId = "";
        while (userId.isEmpty()) {
            System.out.println("이름를 입력하세요.");
            userId = scanner.nextLine();
            if (userId.isEmpty()) {
                System.out.println("이름은 필수로 입력해야 합니다.");
            }

            // 이름 입력 받기
            String userName = "";
            while (userName.isEmpty()) {
                System.out.print("닉네임을 입력하세요: ");
                userName = scanner.nextLine();  // 사용자가 입력한 이름
                if (userName.isEmpty()) {
                    System.out.println("닉네임은 필수로 입력해야 합니다.");
                }
            }

            // 데이터베이스에 연결하고 저장
            saveUserToDatabase(userId, userName);
            scanner.close();

        }
    }

    public static void saveUserToDatabase(String nickname, String userName) {
        String sql =  "INSERT INTO users(name, nickname, id) VALUES(?, ?, ?)";

        try(Connection conn = JDBCConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){

            UUID uuid = UUID.randomUUID();
            ps.setString(1, userName);
            ps.setString(2, nickname);
            ps.setString(3, uuid.toString());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("사용자 추가 완료! UUID");
                loadUserFromDatabase();

            }else{
                System.out.println("사용자 추가 실패");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static void  loadUserFromDatabase() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("메뉴를 선택하세요.");
        System.out.println("1번 닉네임 변경, 2번 회원 탈퇴, 3번 회원 정보 조회");

        int choice = scanner.nextInt();
        scanner.nextLine(); // 개행 문자 제거

        switch(scanner.nextInt()){
            case 1:
              System.out.println("닉변");
                updateUserByScanner();// 닉네임 변경 메서드 실행
                break;
            case 2:
//                boolean isDeleted = deleteUser(id);
//                if (isDeleted) {
//                    System.out.println("사용자 삭제 성공!");
//                } else {
//                    System.out.println("사용자 삭제 실패! ID 또는 닉네임을 확인하세요.");
//                }

                break;
            case 3:
                System.out.println("조회");//회원 정보 조회 메서스 실행
                break;
            default:
                System.out.println("잘못된 접근입니다.");
        }
    }

    public static void updateUserByScanner()
    {
        Scanner scanner = new Scanner(System.in);

        System.out.println("수정할 사용자의 ID : ");
        String userId = scanner.nextLine();

        System.out.print("새로운 닉네임을 입력하세요: ");
        String newNickname = scanner.nextLine();

        System.out.print("새로운 이름을 입력하세요: ");
        String newName = scanner.nextLine();

        boolean result = updateUser(userId, newNickname, newName);

        if (result) {
            System.out.println("사용자 정보가 성공적으로 업데이트되었습니다!");
        } else {
            System.out.println("사용자 정보 업데이트에 실패했습니다.");
        }

        scanner.close();

    }

    public static boolean updateUser(String userId, String newNickname, String newName) {
        String sql = "UPDATE users set nickname = ?, name = ? where id = ?";


        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newNickname);
            pstmt.setString(2, newNickname);
            pstmt.setString(3, userId); // 기존 ID 유지

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
        catch(SQLException e)
            {
                System.err.println("사용자 정보 업데이트 중 오류 발생: " + e.getMessage());
            }
        return false;
    }

    public static boolean deleteUser(String id) {
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
