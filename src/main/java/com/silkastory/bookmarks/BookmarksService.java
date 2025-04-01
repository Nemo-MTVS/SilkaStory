package com.silkastory.bookmarks;
/*
비즈니스 로직 관리 (예외 처리 등)
게시글 추가, 생성,수정 기능 담당

 */

import java.util.List;

public class BookmarksService {
    private final BookmarksDAO bookmarksDAO;

    public BookmarksService(BookmarksDAO bookmarksDAO) {
        this.bookmarksDAO = bookmarksDAO;
    }

    public void saveBookmarks(String userId,int postId,long name) {
        //Bookmarks에 UserId,name,PostId가 있나 확인
        if(!BookmarksDAO.getAllBookmarks(userId).contains(postId)){
            //이미 즐겨찾기 목록에 없으면 즐겨찾기 추가
            BookmarksDAO.addBookmark("아이디", 1212, 100L);
        }
    }

    public List<Bookmarks> getAllBookmarks() {
        return null;
    }

}
