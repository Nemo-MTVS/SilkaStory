package com.silkastory.post;

public class PostResponseDTO {
    private String id;
    private String title;
    private String content;
    private String categoryName;
    private String userNickName;

    public PostResponseDTO(String id, String title, String content, String categoryName, String userNickName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.categoryName = categoryName;
        this.userNickName = userNickName;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getUserNickName() {
        return userNickName;
    }
}
