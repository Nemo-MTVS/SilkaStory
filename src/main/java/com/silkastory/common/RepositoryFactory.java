package com.silkastory.common;

import com.silkastory.category.CategoryRepository;
import com.silkastory.category.CategoryRepositoryImpl;
import com.silkastory.notifications.NotificationsRepository;
import com.silkastory.notifications.NotificationsRepositoryImpl;
import com.silkastory.subscriptions.SubscriptionsRepository;
import com.silkastory.subscriptions.SubscriptionsRepositoryImpl;
import com.silkastory.visitor.VisitorRepository;
import com.silkastory.visitor.VisitorRepositoryImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Repository 인스턴스를 생성하는 팩토리 클래스
 */
public class RepositoryFactory {
    
    private static final Map<Class<?>, Object> REPOSITORIES = new HashMap<>();
    
    static {
        // 각 Repository 구현체 등록
        REPOSITORIES.put(CategoryRepository.class, new CategoryRepositoryImpl());
        REPOSITORIES.put(NotificationsRepository.class, new NotificationsRepositoryImpl());
        REPOSITORIES.put(SubscriptionsRepository.class, new SubscriptionsRepositoryImpl());
        REPOSITORIES.put(VisitorRepository.class, new VisitorRepositoryImpl());
    }
    
    /**
     * 요청된 Repository 인터페이스에 맞는 구현체 반환
     * @param repositoryInterface Repository 인터페이스 클래스
     * @return 구현된 Repository 인스턴스
     */
    @SuppressWarnings("unchecked")
    public static <T> T getRepository(Class<T> repositoryInterface) {
        Object repository = REPOSITORIES.get(repositoryInterface);
        if (repository == null) {
            throw new IllegalArgumentException("해당 Repository 구현체를 찾을 수 없습니다: " + repositoryInterface.getName());
        }
        return (T) repository;
    }
}
