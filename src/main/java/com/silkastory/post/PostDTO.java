package com.silkastory.post;

public class PostDTO {
    private int id;
    private String title;
    private String content;
    private String writer;
    private String categoryName;

    public PostDTO() {}
    public PostDTO(int id, String title, String content, String writer, String categoryName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.categoryName = categoryName;
    }

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

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
