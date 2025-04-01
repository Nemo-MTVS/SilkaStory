package com.silkastory.service;

public class PostView {
    private PostService postService = new PostService();

    public void createatPost(){
        Post post = new Post("제목","내용", "아이디", 255);
    }
}
