package com.silkastory.bookmarks;
/*
비즈니스 로직 관리 (예외 처리 등)
게시글 추가, 생성,수정 기능 담당

 */

import java.sql.SQLException;
import java.util.List;

public class BookmarksService {
    private final BookmarksDAO bookmarksDAO;

    public BookmarksService(BookmarksDAO bookmarksDAO) {
        this.bookmarksDAO = bookmarksDAO;
    }

    // 즐겨찾기 추가
    public void saveBookmarks(String userId,int postId,String name) throws SQLException {
        //Bookmarks에 UserId,name,PostId가 있나 확인
        if(!bookmarksDAO.existBookmark(userId, postId)){
            //이미 즐겨찾기 목록에 없으면 즐겨찾기 추가
            bookmarksDAO.addBookmark(userId, postId, name);
        }
    }

    // 즐겨찾기 수정
    public void updateBookmarks(String userId,int postId,String name) throws SQLException {
        //즐겨찾기 목록에 있으면 즐겨찾기 수정
        bookmarksDAO.updateBookmark(userId, postId, name);
    }

    // 즐겨찾기 삭제
    public void deleteBookmarks(String userId,int postId) throws SQLException {
        //즐겨찾기 목록에 있으면 즐겨찾기 삭제
        bookmarksDAO.removeBookmark(userId, postId);
    }

    // 즐겨찾기 목록 가져오기
    public List<BookmarksDTO> getBookmarks(String userId, int postId) throws SQLException {
        //즐겨찾기 목록에 있으면 즐겨찾기 가져오기
        return bookmarksDAO.getAllBookmarks(userId);
    }
    
    // 즐겨찾기 여부 확인
    public boolean isBookmarked(String userId, int postId) throws SQLException {
        return bookmarksDAO.existBookmark(userId, postId);
    }
}
