package com.silkastory.replies;

/**
 * 답글 모델 클래스
 */
public class Reply {
    private Long id;
    private String content;
    private Long commentId;
    private String userId;

    // 기본 생성자
    public Reply() {
    }

    // 새 답글 생성용 생성자
    public Reply(String content, Long commentId, String userId) {
        this.content = content;
        this.commentId = commentId;
        this.userId = userId;
    }

    // 전체 필드 생성자
    public Reply(Long id, String content, Long commentId, String userId) {
        this.id = id;
        this.content = content;
        this.commentId = commentId;
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

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Reply{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", commentId=" + commentId +
                ", userId='" + userId + '\'' +
                '}';
    }
}
