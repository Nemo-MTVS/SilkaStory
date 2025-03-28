package com.silkastory.service;

public class Post {
    String title;
    String content;
    String id;
    int categoty_id;

    public Post(String title, String content, String id, int categoty_id) {
        this.title = title;
        this.content = content;
        this.id = id;
        this.categoty_id = categoty_id;
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

    public int getCategoty_id() {
        return categoty_id;
    }


    @Override
    public String toString() {
        return "post{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", id='" + id + '\'' +
                ", category_id='" + categoty_id + '\'' +  // category_id 추가
                '}';
    }
}
