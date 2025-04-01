package com.silkastory.common;

import com.silkastory.infrastructure.database.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JPARepository 인터페이스의 기본 구현
 */
public abstract class AbstractJPARepository<T, ID extends Serializable> implements JPARepository<T, ID> {
    
    protected final Class<T> entityClass;
    
    private static final Logger logger = LoggerFactory.getLogger(AbstractJPARepository.class);
    
    @SuppressWarnings("unchecked")
    public AbstractJPARepository() {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }
    
    protected EntityManager getEntityManager() {
        return JPAUtil.getEntityManager();
    }
    
    @Override
    public T save(T entity) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            // ID 필드 값을 확인하여 새 엔티티인지 기존 엔티티인지 판단
            boolean isNew = false;
            try {
                for (Field field : entityClass.getDeclaredFields()) {
                    if (field.isAnnotationPresent(jakarta.persistence.Id.class)) {
                        field.setAccessible(true);
                        Object idValue = field.get(entity);
                        isNew = idValue == null || (idValue instanceof Number && ((Number) idValue).longValue() == 0);
                        break;
                    }
                }
            } catch (Exception e) {
                logger.error("엔티티 ID 확인 중 오류 발생", e);
            }
            
            // 새 엔티티는 persist, 기존 엔티티는 merge 사용
            if (isNew) {
                em.persist(entity);
                logger.debug("새 엔티티 저장: {}", entity);
            } else {
                entity = em.merge(entity);
                logger.debug("기존 엔티티 업데이트: {}", entity);
            }
            
            tx.commit();
            return entity;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            logger.error("엔티티 저장 실패: {}", e.getMessage(), e);
            throw new RuntimeException("엔티티 저장 실패", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public Optional<T> findById(ID id) {
        EntityManager em = getEntityManager();
        try {
            T entity = em.find(entityClass, id);
            return Optional.ofNullable(entity);
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<T> query = cb.createQuery(entityClass);
            Root<T> root = query.from(entityClass);
            query.select(root);
            return em.createQuery(query).getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public List<T> findByQuery(String jpql, Object... params) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<T> query = em.createQuery(jpql, entityClass);
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    @Override
    public void deleteById(ID id) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("엔티티 삭제 실패", e);
        } finally {
            em.close();
        }
    }
    
    @Override
    public void delete(T entity) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.remove(em.contains(entity) ? entity : em.merge(entity));
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("엔티티 삭제 실패", e);
        } finally {
            em.close();
        }
    }
    
    /**
     * 단일 결과를 반환하는 쿼리 실행
     */
    protected Object findSingleResultByQuery(String jpql, Object... params) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Object> query = em.createQuery(jpql, Object.class);
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i + 1, params[i]);
            }
            try {
                return query.getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        } finally {
            em.close();
        }
    }
} 