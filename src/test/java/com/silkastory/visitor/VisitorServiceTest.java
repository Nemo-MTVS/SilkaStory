package com.silkastory.visitor;

import com.silkastory.common.RepositoryFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class VisitorServiceTest {

    private VisitorRepository visitorRepository;
    private VisitorService visitorService;

    private final String TEST_USER_ID = "test_user";
    private final String TEST_USER_ID_2 = "test_user_2";
    private final Long TEST_POST_ID = 1000L;
    private final Long OTHER_POST_ID = 2000L;
    
    // 테스트에서 생성한 방문 기록 ID를 저장하여 나중에 정리할 수 있게 함
    private final List<Long> testVisitorIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // 실제 데이터베이스 레포지토리 사용
        visitorRepository = RepositoryFactory.getRepository(VisitorRepository.class);
        visitorService = new VisitorService(visitorRepository);
    }
    
    @AfterEach
    void tearDown() {
        // 테스트에서 생성한 방문 기록 정리
        for (Long id : testVisitorIds) {
            try {
                visitorRepository.deleteById(id);
            } catch (Exception e) {
                System.err.println("방문 기록 삭제 실패: " + id + ", " + e.getMessage());
            }
        }
        testVisitorIds.clear();
    }
    
    // 테스트에서 생성된 방문 기록 ID를 추적하는 헬퍼 메소드
    private void trackVisitor(Visitor visitor) {
        if (visitor != null && visitor.getId() != null) {
            testVisitorIds.add(visitor.getId());
        }
    }

    @Test
    @DisplayName("방문 기록 저장 테스트")
    void save() {
        // when
        Visitor result = visitorService.save(TEST_USER_ID, TEST_POST_ID);
        trackVisitor(result);

        // then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(TEST_USER_ID, result.getUserId());
        assertEquals(TEST_POST_ID, result.getPostId());
        assertNotNull(result.getVisitDate());
        
        // 레포지토리에서 조회하여 검증
        List<Visitor> visitors = visitorRepository.findByPostId(TEST_POST_ID);
        assertFalse(visitors.isEmpty());
        assertEquals(1, visitors.size());
    }

    @Test
    @DisplayName("동일 사용자의 추가 방문 기록 테스트")
    void saveAdditionalVisit() {
        // given - 첫 번째 방문 기록 생성
        Visitor firstVisit = visitorService.save(TEST_USER_ID, TEST_POST_ID);
        trackVisitor(firstVisit);
        
        // when - 동일 사용자의 추가 방문
        Visitor result = visitorService.save(TEST_USER_ID, TEST_POST_ID);
        trackVisitor(result);

        // then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(TEST_USER_ID, result.getUserId());
        assertEquals(TEST_POST_ID, result.getPostId());
        
        // 레포지토리에서 조회하여 검증 - 2개의 방문 기록이 있어야 함
        List<Visitor> visitors = visitorRepository.findByPostId(TEST_POST_ID);
        assertEquals(2, visitors.size());
    }

    @Test
    @DisplayName("게시글 방문자 수 조회 테스트")
    void countByPostId() {
        // given - 방문 기록 생성
        Visitor visit1 = visitorService.save(TEST_USER_ID, TEST_POST_ID);
        trackVisitor(visit1);
        Visitor visit2 = visitorService.save(TEST_USER_ID_2, TEST_POST_ID);
        trackVisitor(visit2);
        Visitor visit3 = visitorService.save(TEST_USER_ID, TEST_POST_ID);  // 동일 사용자 재방문
        trackVisitor(visit3);

        // when
        Long count = visitorService.countByPostId(TEST_POST_ID);

        // then
        assertEquals(2L, count);  // 고유 사용자 수는 2명
    }

    @Test
    @DisplayName("방문 기록이 없는 게시글 방문자 수 조회 테스트")
    void countByPostIdWithNoVisitors() {
        // when
        Long count = visitorService.countByPostId(OTHER_POST_ID);

        // then
        assertEquals(0L, count);
    }

    @Test
    @DisplayName("게시글별 방문 기록 조회 테스트")
    void findByPostId() {
        // given
        Visitor visit1 = visitorService.save(TEST_USER_ID, TEST_POST_ID);
        trackVisitor(visit1);
        Visitor visit2 = visitorService.save(TEST_USER_ID_2, TEST_POST_ID);
        trackVisitor(visit2);

        // when
        List<Visitor> results = visitorService.findByPostId(TEST_POST_ID);

        // then
        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(v -> v.getUserId().equals(TEST_USER_ID)));
        assertTrue(results.stream().anyMatch(v -> v.getUserId().equals(TEST_USER_ID_2)));
    }

    @Test
    @DisplayName("방문 기록이 없는 게시글 조회 테스트")
    void findByPostIdWithNoVisitors() {
        // when
        List<Visitor> results = visitorService.findByPostId(OTHER_POST_ID);

        // then
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("사용자별 방문 기록 조회 테스트")
    void findByUserId() {
        // given
        Visitor visit1 = visitorService.save(TEST_USER_ID, TEST_POST_ID);
        trackVisitor(visit1);
        Visitor visit2 = visitorService.save(TEST_USER_ID, OTHER_POST_ID);
        trackVisitor(visit2);

        // when
        List<Visitor> results = visitorService.findByUserId(TEST_USER_ID);

        // then
        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(v -> v.getUserId().equals(TEST_USER_ID)));
        assertTrue(results.stream().anyMatch(v -> v.getPostId().equals(TEST_POST_ID)));
        assertTrue(results.stream().anyMatch(v -> v.getPostId().equals(OTHER_POST_ID)));
    }
} 