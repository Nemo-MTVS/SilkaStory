package com.silkastory.users;

public class Users {

    private String id;
    private String name;
    private String nickname;
    private String email;
    private String password;

    // 기본 생성자
    public Users() {
    }

    // 사용자 생성 시 사용하는 생성자 (회원가입용)
    public Users(String name, String nickname, String email, String password) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

    // 전체 필드를 위한 생성자
    public Users(String id, String name, String nickname, String email, String password) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    // 이메일은 변경 불가능하므로 setEmail 메서드는 제공하지 않습니다

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
