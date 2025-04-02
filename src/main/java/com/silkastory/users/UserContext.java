package com.silkastory.users;

/**
 * 애플리케이션 전역에서 현재 로그인된 사용자 정보를 관리하는 싱글톤 클래스
 */
public class UserContext {
    
    private static UserContext instance;
    private Users currentUser;
    
    private UserContext() {
        // 싱글톤 패턴을 위한 private 생성자
    }
    
    /**
     * UserContext의 싱글톤 인스턴스 반환
     */
    public static synchronized UserContext getInstance() {
        if (instance == null) {
            instance = new UserContext();
        }
        return instance;
    }
    
    /**
     * 현재 로그인된 사용자 설정
     */
    public void setCurrentUser(Users user) {
        this.currentUser = user;
    }
    
    /**
     * 현재 로그인된 사용자 정보 반환
     */
    public Users getCurrentUser() {
        return currentUser;
    }
    
    /**
     * 현재 로그인된 사용자 ID 반환
     */
    public String getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }
    
    /**
     * 로그인 상태 확인
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * 로그아웃 (현재 사용자 정보 초기화)
     */
    public void logout() {
        currentUser = null;
    }
} 