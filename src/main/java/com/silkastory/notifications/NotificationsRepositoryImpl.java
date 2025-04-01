package com.silkastory.notifications;

import com.silkastory.common.AbstractJPARepository;
import java.util.List;
import java.util.Optional;

public class NotificationsRepositoryImpl extends AbstractJPARepository<Notifications, Long> implements NotificationsRepository {
    
    @Override
    public Optional<Notifications> findById(Long id) {
        return super.findById(id);
    }
    
    @Override
    public List<Notifications> findByUserId(String userId) {
        return findByQuery("SELECT n FROM com.silkastory.notifications.Notifications n WHERE n.userId = ?1", userId);
    }
    
    @Override
    public List<Notifications> findByUserIdAndStateFalse(String userId) {
        return findByQuery("SELECT n FROM com.silkastory.notifications.Notifications n WHERE n.userId = ?1 AND n.state = false", userId);
    }
    
    @Override
    public int countByUserId(String userId) {
        Long count = (Long) findSingleResultByQuery("SELECT COUNT(n) FROM com.silkastory.notifications.Notifications n WHERE n.userId = ?1", userId);
        return count != null ? count.intValue() : 0;
    }
    
    @Override
    public int countByUserIdAndStateFalse(String userId) {
        Long count = (Long) findSingleResultByQuery("SELECT COUNT(n) FROM com.silkastory.notifications.Notifications n WHERE n.userId = ?1 AND n.state = false", userId);
        return count != null ? count.intValue() : 0;
    }
} 