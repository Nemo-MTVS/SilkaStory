package com.silkastory.bookmarks;

public class BookmarksDTO {
    private Long id;
    private String userId;
    private int postId;
    private String nickname;


    public BookmarksDTO(Long id, String userId, int postId, String nickname) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.nickname = nickname;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
