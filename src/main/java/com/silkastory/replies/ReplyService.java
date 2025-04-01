package com.silkastory.replies;
import java.util.List;

import com.silkastory.infrastructure.database.JDBCConnection;

public class ReplyService {

    private final ReplyDAO replyDAO;

    public ReplyService(ReplyDAO replyDAO) {
        this.replyDAO = replyDAO;
    }

    // 답글 추가
    public void addReply(String content, Long commentId, String userId) {
        replyDAO.insertReply(content, commentId, userId);
    }

    // 특정 댓글의 답글 조회
    public List<Reply> getReplies(Long commentId) {
        return replyDAO.getRepliesByComment(commentId);
    }

    // 답글 삭제 (소프트 삭제)
    public void removeReply(Long replyId) {
        replyDAO.deleteReply(replyId);
    }
}