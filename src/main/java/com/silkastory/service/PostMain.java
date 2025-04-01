package com.silkastory.service;

import java.sql.SQLException;

public class PostMain {
    //전체 흐름을 연결하는 메인 클래스
    public static void main(String[] args) throws SQLException {
        try {
            PostService postService = new PostService();
            postService.addPost("아이디", "제목", "123", 255);
        } catch (Exception e) {
            e.printStackTrace();  // 예외 발생 시 스택 트레이스를 출력
        }
    }
}
