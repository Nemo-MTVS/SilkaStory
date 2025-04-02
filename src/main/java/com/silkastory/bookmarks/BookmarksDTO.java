package com.silkastory.bookmarks;

public class BookmarksDTO {
    private Long id;
    private String userId;
    private int postId;
    private String nickname;
    private String name;

    public BookmarksDTO(Long id, String userId, int postId, String name) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.name = name;
    }
    
    public BookmarksDTO(Long id, String userId, int postId, String nickname, String name) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.nickname = nickname;
        this.name = name;
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
}
