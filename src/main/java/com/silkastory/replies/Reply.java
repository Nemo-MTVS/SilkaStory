package com.silkastory.replies;

public class Reply {
    private Long id;
    private String content;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
    private boolean isUsed;
    private Long commentId;
    private String userId;

    public Reply(Long id, String content, String createdAt, String updatedAt, String deletedAt, boolean isUsed, Long commentId, String userId) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.isUsed = isUsed;
        this.commentId = commentId;
        this.userId = userId;
    }

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
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    public String getDeletedAt() {
        return deletedAt;
    }
    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }
    public boolean isIsUsed() {
        return isUsed;
    }
    public void setIsUsed(boolean isUsed) {
        this.isUsed = isUsed;
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
}
