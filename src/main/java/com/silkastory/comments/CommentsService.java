package com.silkastory.comments;

import java.util.List;

public class CommentsService {
    private final CommentsRepository commentsRepository;

    public CommentsService(CommentsRepository commentsRepository) {
        this.commentsRepository = commentsRepository;
    }

    public List<Comments> getComments() {
        return commentsRepository.findAll();
    }

    public Comments getComment(Long id) {
        return commentsRepository.findById(id);
    }

    public Comments createComment(String content, Long postId, String userId) {
        Comments comment = new Comments(null, content, postId, userId);
        return commentsRepository.save(comment);
    }

    public Comments updateComment(String content, Long id) {
        Comments comment = commentsRepository.findById(id);
        comment.updateContent(content);
        return commentsRepository.save(comment);
    }

    public void deleteComment(Long id) {
        commentsRepository.deleteById(id);
    }
}
