package com.silkastory.comments;

import com.silkastory.common.MiniJpa;

public class CommentsRepositoryImpl extends MiniJpa<Comments, Long> implements CommentsRepository {
    public CommentsRepositoryImpl() {
        super(Comments.class);
    }
}
