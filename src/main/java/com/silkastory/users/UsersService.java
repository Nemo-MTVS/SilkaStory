package com.silkastory.users;

public class UsersService {

    private final UsersDao usersDao;

    public UsersService(UsersDao usersDao) {
        this.usersDao = usersDao;
    }

    /**
     * 회원가입
     */
    public void saveUser(String name, String nickname, String email, String password) throws IllegalArgumentException {
        validateInputString(name, "이름");
        validateInputString(nickname, "닉네임");
        validateInputString(email, "이메일");
        validateInputString(password, "비밀번호");

        // 이메일 중복 체크
        if (usersDao.isEmailExists(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        
        // 비밀번호 유효성 검사 (최소 8자 이상)
        if (password.isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 최소 1자 이상이어야 합니다.");
        }
        
        usersDao.saveUser(name, nickname, email, password);
    }

    /**
     * 사용자 정보 수정
     */
    public boolean updateUser(String id, String newName, String newNickname, String newPassword) throws IllegalArgumentException {
        validateInputString(id, "사용자 ID");
        validateInputString(newName, "이름");
        validateInputString(newNickname, "닉네임");
        
        // 비밀번호가 제공된 경우에만 유효성 검사
        if (newPassword != null && !newPassword.isEmpty()) {
            validateInputString(newPassword, "비밀번호");
            if (newPassword.length() < 8) {
                throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
            }
        } else {
            // 비밀번호가 제공되지 않은 경우 기존 비밀번호 유지
            Users user = usersDao.getUserById(id);
            if (user == null) {
                throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
            }
            newPassword = user.getPassword();
        }
        
        return usersDao.updateUser(id, newName, newNickname, newPassword);
    }

    /**
     * 로그인
     */
    public Users login(String email, String password) throws IllegalArgumentException {
        validateInputString(email, "이메일");
        validateInputString(password, "비밀번호");
        
        Users user = usersDao.login(email, password);
        if (user == null) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        
        return user;
    }

    /**
     * 회원 탈퇴
     */
    public boolean deleteUser(String userId) throws IllegalArgumentException {
        validateInputString(userId, "사용자 ID");
        
        return usersDao.deleteUser(userId);
    }

    /**
     * 입력값 유효성 검사
     */
    private void validateInputString(String str, String fieldName) throws IllegalArgumentException {
        if (str == null || str.isBlank()) {
            throw new IllegalArgumentException(fieldName + "을(를) 입력해주세요.");
        }
    }

    /**
     * 이메일 형식 검사
     */
    private void validateEmail(String email) throws IllegalArgumentException {
        // 간단한 이메일 형식 검사
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }
    }
}
