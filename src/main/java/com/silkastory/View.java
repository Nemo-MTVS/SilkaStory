package com.silkastory;

/**
 * 도메인별 시뮬레이터에 대한 View 인터페이스
 * 이 인터페이스는 각 도메인 시뮬레이터의 시작점 역할을 합니다.
 */
public interface View {
    
    /**
     * 시뮬레이터를 시작하는 메서드
     */
    void start();
    
    /**
     * 시뮬레이터를 종료하는 메서드
     */
    void stop();
    
    /**
     * 현재 실행 중인 시뮬레이터의 이름을 반환하는 메서드
     * @return 시뮬레이터 이름
     */
    String getName();
} 