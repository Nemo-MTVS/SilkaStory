package com.silkastory.bookmarks;

/**
 * 북마크 데이터를 표현하는 클래스
 */
public class Bookmarks {
    private Long id;
    private String userId;
    private int postId;
    private String name;

    public Bookmarks() {
    }

    public Bookmarks(Long id, String userId, int postId, String name) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.name = name;
    }

    public Bookmarks(String userId, int postId, String name) {
        this.userId = userId;
        this.postId = postId;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "Bookmarks{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", postId=" + postId +
                ", name='" + name + '\'' +
                '}';
    }
}
