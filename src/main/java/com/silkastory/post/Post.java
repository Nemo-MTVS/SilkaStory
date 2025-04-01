package com.silkastory.post;

public class Post {
    String title;
    String content;
    String id;
    int categoryId;
    boolean isPublic;

    public Post(String title, String content, String id, int categoryId) {
        this.title = title;
        this.content = content;
        this.id = id;
        this.categoryId = categoryId;
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

    public int getCategoryId() {
        return categoryId;
    }

    public boolean ispublic() {
        return isPublic;
    }

    @Override
    public String toString() {
        return "post{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", id='" + id + '\'' +
                ", category_id='" + categoryId + '\'' +  // category_id 추가
                '}';
    }
}
