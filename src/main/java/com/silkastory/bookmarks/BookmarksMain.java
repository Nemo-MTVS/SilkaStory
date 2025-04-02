package com.silkastory.bookmarks;

import com.silkastory.infrastructure.database.JDBCConnection;

public class BookmarksMain {
    public static void main(String[] args) {
        //데이터베이스 연결 및 DAO, 서비스 객체 생성
        JDBCConnection jdbcConnection = new JDBCConnection();
        BookmarksDAO bookmarksDAO = new BookmarksDAO();
        BookmarksService bookmarksService = new BookmarksService(bookmarksDAO);

        String userId = "1111";
        int postId = 100;
        Long name = 1L;
    }
}
