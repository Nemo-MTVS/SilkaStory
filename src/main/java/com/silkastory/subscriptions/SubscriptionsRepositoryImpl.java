package com.silkastory.subscriptions;

import com.silkastory.common.AbstractJPARepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;

public class SubscriptionsRepositoryImpl extends AbstractJPARepository<Subscriptions, Long> implements SubscriptionsRepository {
    
    @Override
    public Optional<Subscriptions> findByUserIdAndTargetId(String userId, String targetId) {
        String jpql = "SELECT s FROM com.silkastory.subscriptions.Subscriptions s WHERE s.userId = ?1 AND s.targetId = ?2";
        List<Subscriptions> result = findByQuery(jpql, userId, targetId);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }
    
    @Override
    public List<Subscriptions> findByUserId(String userId) {
        return findByQuery("SELECT s FROM com.silkastory.subscriptions.Subscriptions s WHERE s.userId = ?1", userId);
    }
    
    @Override
    public List<Subscriptions> findByTargetId(String targetId) {
        return findByQuery("SELECT s FROM com.silkastory.subscriptions.Subscriptions s WHERE s.targetId = ?1", targetId);
    }
    
    @Override
    public List<Subscriptions> findByTargetIdAndIsAlramTrue(String targetId) {
        return findByQuery("SELECT s FROM com.silkastory.subscriptions.Subscriptions s WHERE s.targetId = ?1 AND s.isAlram = true", targetId);
    }
    
    @Override
    public void deleteByUserIdAndTargetId(String userId, String targetId) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery("DELETE FROM com.silkastory.subscriptions.Subscriptions s WHERE s.userId = ?1 AND s.targetId = ?2")
                    .setParameter(1, userId)
                    .setParameter(2, targetId)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("구독 관계 삭제 실패", e);
        } finally {
            em.close();
        }
    }
} 