package com.silkastory.comments;

import java.util.List;

public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getComments() {
        return commentRepository.findAll();
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
        // return null;
    }

    public Comment getComment(Long id) {
        return commentRepository.findById(id);
    }

    public Comment createComment(String content, Long postId, String userId) {
        Comment comment = new Comment(null, content, postId, userId);
        return commentRepository.save(comment);
    }

    public Comment updateComment(String content, Long id) {
        Comment comment = commentRepository.findById(id);
        comment.updateContent(content);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
