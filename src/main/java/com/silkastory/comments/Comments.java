package com.silkastory.comments;

import com.silkastory.common.Column;
import com.silkastory.common.Id;
import com.silkastory.common.Table;

@Table(name = "comments")
public class Comments {
    @Id
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "user_id")
    private String userId;

    public Comments() {}

    public Comments(Long id, String content, Long postId, String userId) {
        this.id = id;
        this.content = content;
        this.postId = postId;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Long getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Comments{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", postId=" + postId +
                ", userId='" + userId + '\'' +
                '}';
    }
}
