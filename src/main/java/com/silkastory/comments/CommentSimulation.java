package com.silkastory.comments;

import com.silkastory.common.RepositoryFactory;

import java.util.List;

public class CommentSimulation {

    private Long id;
    private Long id2;
    private final CommentRepository commentRepository;

    public CommentSimulation() {
        this.commentRepository = RepositoryFactory.create(CommentRepository.class);
    }

    public void createComment() {
        System.out.println("유저식별값: userId, 게시글 식별값: 1L, 내용: 'content' 로 생성 시도");
        Comment comment = new Comment(null, "content", 1L, "userId");
        commentRepository.save(comment);
        id = comment.getId();
        System.out.println("Comment :" + comment);
    }

    public void create2Comment() {
        System.out.println("유저식별값: userId, 게시글 식별값: 2L, 내용: 'content2' 로 생성 시도");
        Comment comment = new Comment(null, "content2", 2L, "userId");
        commentRepository.save(comment);
        id2 = comment.getId();
        System.out.println("Comment :" + comment);
    }

    public void getComments() {
        System.out.println("모든 댓글 조회 시도");
        List<Comment> comments = commentRepository.findAll();
        for (Comment comment : comments) {
            System.out.println(comment);
        }
        System.out.println("모든 댓글 조회 종료");
    }

    public void getComment() {
        System.out.println(id + "번 댓글 조회 시도");
        Comment comments = commentRepository.findById(id);
        if (comments == null){
            System.out.println("1번 댓글이 없음");
            return;
        }
        System.out.println(comments);
    }

    public void getCommentsByPostId() {
        System.out.println("1번 게시글의 댓글 조회 시도");
        List<Comment> comments = commentRepository.findByPostId(1L);
        for (Comment comment : comments) {
            System.out.println(comment);
        }
        System.out.println("1번 게시글의 댓글 조회 종료");
    }

    public void updateComment() {
        Comment comment = commentRepository.findById(id);
        System.out.println(id + "번 댓글의 내용을 'new content'로 수정 시도");
        comment.updateContent("new content");
        commentRepository.save(comment);
        System.out.println("Comment :" + comment);
    }

    public void deleteComment() {
        System.out.println(id + " 댓글 삭제 시도");
        commentRepository.deleteById(id);
        Comment comments = commentRepository.findById(id);
        if (comments == null){
            System.out.println(id + "번 댓글 삭제 완료");
        }
    }
    public void deleteComment2() {
        System.out.println(id2 + " 댓글 삭제 시도");
        commentRepository.deleteById(id2);
        Comment comments = commentRepository.findById(id2);
        if (comments == null){
            System.out.println(id2 + "번 댓글 삭제 완료");
        }
    }

    public static void main(String[] args) {
        System.out.println("====Comment Simulation Start===");
        CommentSimulation commentSimulation = new CommentSimulation();
        commentSimulation.createComment();
        commentSimulation.create2Comment();
        commentSimulation.getComments();
        commentSimulation.getComment();
        commentSimulation.getCommentsByPostId();
        commentSimulation.updateComment();
        commentSimulation.deleteComment();
        commentSimulation.deleteComment2();
        System.out.println("====Comment Simulation END===");
    }
}
