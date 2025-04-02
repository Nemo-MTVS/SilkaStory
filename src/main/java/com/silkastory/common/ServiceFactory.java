package com.silkastory.common;

import com.silkastory.bookmarks.BookmarksDAO;
import com.silkastory.bookmarks.BookmarksService;
import com.silkastory.category.CategoryRepository;
import com.silkastory.category.CategoryService;
import com.silkastory.infrastructure.database.JDBCConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service 인스턴스를 생성하는 팩토리 클래스
 */
public class ServiceFactory {
    
    private static final Map<Class<?>, Object> SERVICES = new HashMap<>();
    
    static {
        // 북마크 서비스 초기화
        Connection jdbcConnection = null;
        try {
            jdbcConnection = JDBCConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        BookmarksDAO bookmarksDAO = new BookmarksDAO();
        BookmarksService bookmarksService = new BookmarksService(bookmarksDAO);
        
        // 카테고리 서비스 초기화
        CategoryRepository categoryRepository = RepositoryFactory.getRepository(CategoryRepository.class);
        CategoryService categoryService = new CategoryService(categoryRepository);
        
        // 서비스 등록
        SERVICES.put(BookmarksService.class, bookmarksService);
        SERVICES.put(CategoryService.class, categoryService);
    }
    
    /**
     * 요청된 Service 인터페이스에 맞는 구현체 반환
     * @param serviceClass Service 클래스
     * @return 구현된 Service 인스턴스
     */
    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> serviceClass) {
        Object service = SERVICES.get(serviceClass);
        if (service == null) {
            throw new IllegalArgumentException("해당 Service 구현체를 찾을 수 없습니다: " + serviceClass.getName());
        }
        return (T) service;
    }
} 