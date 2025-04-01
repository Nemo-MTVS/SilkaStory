package com.bookmarks;

public class Bookmarks {
    public String userId;
    public int postId;
    public long name;

    public Bookmarks(String userId,int postId,long name){
        this.userId = userId;
        this.postId = postId;
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
    public long getName() {
        return name;
    }
    public void setName(long name) {
        this.name = name;
    }
    public Bookmarks(String userId,int postId){
        this.userId = userId;
        this.postId = postId;
    }


}
