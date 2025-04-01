package com.silkastory.bookmarks;

public class Bookmarks {
    private Long id;
    public String userId;
    public int postId;
    public String name;

    public Bookmarks(String userId,int postId,String name){
        this.userId = userId;
        this.postId = postId;
        this.name = name;
    }

    public Long getId() {
        return id;
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
