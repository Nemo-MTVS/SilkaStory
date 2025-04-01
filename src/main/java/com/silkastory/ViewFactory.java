package com.silkastory;

import com.silkastory.category.CategoryView;
import com.silkastory.notifications.NotificationsView;
import com.silkastory.subscriptions.SubscriptionsView;
import com.silkastory.users.UsersView;
import com.silkastory.visitor.VisitorView;

import java.util.HashMap;
import java.util.Map;

/**
 * View 객체를 생성하는 팩토리 클래스
 */
public class ViewFactory {
    private static final Map<String, View> viewCache = new HashMap<>();
    
    /**
     * 주어진 도메인에 해당하는 View 객체를 반환합니다.
     * @param domain 도메인 이름 (category, notifications, subscriptions, users, visitor)
     * @return 해당 도메인의 View 객체
     * @throws IllegalArgumentException 지원하지 않는 도메인인 경우
     */
    public static View getView(String domain) {
        if (viewCache.containsKey(domain)) {
            return viewCache.get(domain);
        }
        
        View view;
        switch (domain.toLowerCase()) {
            case "category":
                view = new CategoryView();
                break;
            case "notifications":
                view = new NotificationsView();
                break;
            case "subscriptions":
                view = new SubscriptionsView();
                break;
            case "users":
                view = new UsersView();
                break;
            case "visitor":
                view = new VisitorView();
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 도메인입니다: " + domain);
        }
        
        viewCache.put(domain, view);
        return view;
    }
    
    /**
     * 등록된 모든 View의 이름을 반환합니다.
     * @return 등록된 모든 View 이름 배열
     */
    public static String[] getAllDomains() {
        return new String[] {
            "category",
            "notifications",
            "subscriptions",
            "users",
            "visitor"
        };
    }
} 