package com.silkastory.comments;

/**
 * 댓글 모델 클래스
 */
public class Comment {
    private Long id;
    private String content;
    private Long postId;
    private String userId;

    // 기본 생성자
    public Comment() {
    }

    // 새 댓글 생성용 생성자
    public Comment(String content, Long postId, String userId) {
        this.content = content;
        this.postId = postId;
        this.userId = userId;
    }

    // 전체 필드 생성자
    public Comment(Long id, String content, Long postId, String userId) {
        this.id = id;
        this.content = content;
        this.postId = postId;
        this.userId = userId;
    }

    // Getter와 Setter 메서드
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", postId=" + postId +
                ", userId='" + userId + '\'' +
                '}';
    }
}
