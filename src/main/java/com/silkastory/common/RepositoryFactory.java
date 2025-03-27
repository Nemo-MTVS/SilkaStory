package com.silkastory.common;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

public class RepositoryFactory {

    public static <T> T create(Class<T> repositoryInterface) {
        Class<?> entityClass = extractEntityClass(repositoryInterface);
        return (T) Proxy.newProxyInstance(
                repositoryInterface.getClassLoader(),
                new Class[]{repositoryInterface},
                new MethodQueryHandler(entityClass, repositoryInterface)
        );
    }

    private static Class<?> extractEntityClass(Class<?> repoInterface) {
        for (Type type : repoInterface.getGenericInterfaces()) {
            if (type instanceof ParameterizedType pt) {
                return (Class<?>) pt.getActualTypeArguments()[0];
            }
        }
        throw new RuntimeException("엔티티 타입 추출 실패");
    }
}
