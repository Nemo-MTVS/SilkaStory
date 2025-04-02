// package com.silkastory.replies;
// import java.sql.*;
// import java.util.ArrayList;
// import java.util.List;
//
// import com.silkastory.infrastructure.database.JDBCConnection;
//
// public class ReplyMain {
//     public static void main(String[] args) {
//         // JDBC 연결 객체 생성
//         JDBCConnection jdbcConnection = new JDBCConnection();
//
//         // DAO, Service 객체 생성
//         ReplyDAO replyDAO = new ReplyDAO(jdbcConnection);
//         ReplyService replyService = new ReplyService(replyDAO);
//
//         // 1. 답글 추가
//         replyService.addReply("이 댓글에 대한 답글입니다.", 1L, "user123");
//         System.out.println("답글이 등록되었습니다.");
//
//         // 2. 특정 댓글의 답글 조회
//         List<Reply> replies = replyService.getReplies(1L);
//         for (Reply reply : replies) {
//             System.out.println("답글 ID: " + reply.getId() + ", 내용: " + reply.getContent());
//         }
//
//         // 3. 답글 삭제
//         replyService.removeReply(1L);
//         System.out.println("답글이 삭제되었습니다.");
//     }
// }
