package com.silkastory.infrastructure.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class JPAUtil {
    private static final Logger logger = LoggerFactory.getLogger(JPAUtil.class);
    private static EntityManagerFactory emf = null;
    
    static {
        try {
            // config.properties 파일 로드
            Properties configProps = new Properties();
            InputStream inputStream = JPAUtil.class.getClassLoader().getResourceAsStream("config.properties");
            if (inputStream != null) {
                configProps.load(inputStream);
                inputStream.close();
                
                // DB 연결 정보 로깅 (비밀번호는 마스킹)
                logger.info("데이터베이스 URL: {}", configProps.getProperty("db.url"));
                logger.info("데이터베이스 사용자: {}", configProps.getProperty("db.username"));
                
                // 속성 맵 생성
                Map<String, Object> props = new HashMap<>();
                props.put("jakarta.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");
                props.put("jakarta.persistence.jdbc.url", configProps.getProperty("db.url"));
                props.put("jakarta.persistence.jdbc.user", configProps.getProperty("db.username"));
                props.put("jakarta.persistence.jdbc.password", configProps.getProperty("db.password"));
                
                // EntityManagerFactory 생성
                try {
                    emf = Persistence.createEntityManagerFactory("silkastory", props);
                    logger.info("EntityManagerFactory 생성 완료");
                } catch (Exception e) {
                    logger.error("EntityManagerFactory 생성 실패: {}", e.getMessage(), e);
                    if (e.getCause() != null) {
                        logger.error("원인: {}", e.getCause().getMessage());
                    }
                    throw new RuntimeException("EntityManagerFactory 생성 실패: " + e.getMessage(), e);
                }
            } else {
                logger.error("config.properties 파일을 찾을 수 없습니다.");
                throw new RuntimeException("config.properties 파일을 찾을 수 없습니다.");
            }
        } catch (IOException e) {
            logger.error("config.properties 로딩 실패: {}", e.getMessage(), e);
            throw new RuntimeException("config.properties 로딩 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("JPAUtil 초기화 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("JPAUtil 초기화 실패: " + e.getMessage(), e);
        }
    }
    
    public static EntityManager getEntityManager() {
        if (emf == null) {
            throw new IllegalStateException("EntityManagerFactory가 초기화되지 않았습니다.");
        }
        return emf.createEntityManager();
    }
    
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            logger.info("EntityManagerFactory 종료");
        }
    }
} 