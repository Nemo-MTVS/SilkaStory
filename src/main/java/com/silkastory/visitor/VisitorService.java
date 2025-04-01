package com.silkastory.visitor;

import java.time.LocalDateTime;
import java.util.List;

public class VisitorService {
    private final VisitorRepository visitorRepository;

    public VisitorService(VisitorRepository visitorRepository) {
        this.visitorRepository = visitorRepository;
    }

    // 로그 개념
    public Visitor save(String userId, Long postId) {
        Visitor visitor = new Visitor(userId, postId, LocalDateTime.now());
        return visitorRepository.save(visitor);
    }
    
    // 게시글 방문자 수 반환 (동일인은 집계에서 제외)
    public Long countByPostId(Long postId) {
        return visitorRepository.countDistinctUserIdByPostId(postId);
    }
    
    // 특정 게시글의 방문 기록 목록 조회
    public List<Visitor> findByPostId(Long postId) {
        return visitorRepository.findByPostId(postId);
    }
    
    // 특정 사용자의 방문 기록 목록 조회
    public List<Visitor> findByUserId(String userId) {
        return visitorRepository.findByUserId(userId);
    }
}
