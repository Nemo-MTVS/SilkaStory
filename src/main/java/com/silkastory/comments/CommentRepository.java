package com.silkastory.comments;

import com.silkastory.common.MiniJPARepository;

import java.util.List;

public interface CommentRepository extends MiniJPARepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
}
