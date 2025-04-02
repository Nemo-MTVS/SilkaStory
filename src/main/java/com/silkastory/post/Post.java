package com.silkastory.post;

/**
 * 게시글 모델 클래스
 */
public class Post {
    private int id;
    private String title;
    private String content;
    private String userId;
    private int categoryId;

    // 기본 생성자
    public Post() {
    }

    // 게시글 생성 시 사용하는 생성자
    public Post(String title, String content, String userId, int categoryId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.categoryId = categoryId;
    }

    // 게시글 조회 시 사용하는 생성자
    public Post(int id, String title, String content, String userId, int categoryId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.categoryId = categoryId;
    }

    // Getter 및 Setter 메서드
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", userId='" + userId + '\'' +
                ", categoryId=" + categoryId +
                '}';
    }
}
