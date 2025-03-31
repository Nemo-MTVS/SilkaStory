package com.silkastory.Comments;
import java.util.List;

public class CommentService {
    private final CommentDAO commentDAO;
    //CommentDAO를 주입 받아서 데이터베이스 연산을 수행
    //List를 사용해 여러 개의 댓글을 관리

    public CommentService(CommentDAO commentDAO) {
        this.commentDAO = commentDAO;
    }
    //DAO를 서비스 계층에서 감싸는 역할
    //다른 계층에서 CommentService를 사용하면 직접 DAO를 호출하지 않아도 된다. 비즈니스 로직과 DB 처리를 분리하여 유지보수성 높임

    // 댓글 작성
    public void addComment(String content, Long postId, String userId) {
        commentDAO.insertComment(content, postId, userId);
    }
    //CommentDAO.insertComment()를 호출한다.
    //DB에 새로운 댓글 추가

    // 특정 게시글의 댓글 조회
    public List<Comment> getComments(Long postId) {
        return commentDAO.getCommentsByPost(postId);
    }
    //postId를 이용해 CommentDAO.getCommentsByPost(postId) 호출
    //DB에서 해당 게시글의 댓글 목록을 가져옴
    //가져온 데이터를 List<Comment>형태로 변환한다.

    // 댓글 삭제 (소프트 삭제)
    public void removeComment(Long commentId) {
        commentDAO.deleteComment(commentId);
    }
    //CommentDAO.deleteComment(commentId) 호출.
    //DB에서 해당 댓글의 is_deleted 값을 TRUE로 변경.
    //실제 데이터를 삭제하는 것이 아니라 삭제된 것처럼 처리.

    //전체 흐름 정리
    /*
    서비스 계층(CommentService)에서 DAO를 감싸서 사용.

DB 관련 로직을 DAO가 처리하고, Service는 이를 호출만 함.

이점: 코드가 더 모듈화되고 유지보수가 쉬워짐.

비즈니스 로직을 추가할 경우, CommentService에서 처리 가능.

DAO 직접 호출을 방지하여, 계층 간 역할을 분리
비즈니스 로직을 서비스 계층에서 관리
JDBC와 HikariCP를 효율적으로 활용 가능
     */
}
