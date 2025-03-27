package com.silkastory.common;

import com.silkastory.infrastructure.database.JDBCConnection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MethodQueryHandler implements InvocationHandler {
    private final MiniJpa<?, ?> miniJpa;
    private final Class<?> entityClass;
    private final List<Method> repoMethods;

    public MethodQueryHandler(Class<?> entityClass, Class<?> repoInterface) {
        this.entityClass = entityClass;
        this.miniJpa = new MiniJpa<>(entityClass);
        this.repoMethods = Arrays.asList(repoInterface.getMethods());
    }

    @Override
    // 메서드 실행 분기 처리
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Method matched = findMatchingMethod(method);
        if (isJpaBaseMethod(method)) return method.invoke(miniJpa, args);
        if (isMethodQuery(method)) return handleFindByMethod(method, args);
        throw new UnsupportedOperationException("지원하지 않는 메서드: " + method.getName());
    }

    // 메서드 유효성 검사 및 오버로딩 대응
    private Method findMatchingMethod(Method method) {
        return repoMethods.stream()
                .filter(m -> m.getName().equals(method.getName()) &&
                        Arrays.equals(m.getParameterTypes(), method.getParameterTypes()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("인터페이스에 선언되지 않은 메서드입니다: " + method.getName()));
    }

    // 기본 CRUD인지 판별
    private boolean isJpaBaseMethod(Method method) {
        return Arrays.stream(MiniJPARepository.class.getMethods())
                .anyMatch(m -> m.getName().equals(method.getName()));
    }

    // findBy 등 메서드 쿼리 여부 판별
    private boolean isMethodQuery(Method method) {
        String name = method.getName();
        return name.startsWith("findBy") || name.startsWith("findAllBy") || name.startsWith("findTop") || name.startsWith("findFirst");
    }

    // 메서드 이름 기반 쿼리(Method Query) 처리
    private Object handleFindByMethod(Method method, Object[] args) throws Exception {
        String methodName = normalizeMethodName(method.getName());
        String[] split = methodName.split("OrderBy");
        String conditionPart = split[0];
        String orderByPart = (split.length > 1) ? split[1] : null;

        QueryParts queryParts = parseConditions(conditionPart, args);
        String tableName = resolveTableName();

        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(tableName);
        if (!queryParts.conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", queryParts.conditions));
        }

        if (orderByPart != null) {
            sql.append(" ORDER BY ").append(parseOrderBy(orderByPart));
        }

        boolean isListReturn = method.getReturnType().equals(List.class);
        boolean isTopOne = method.getName().startsWith("findTop") || method.getName().startsWith("findFirst");
        if (!isListReturn && !isTopOne) sql.append(" LIMIT 1");

        System.out.println("🧪 SQL: " + sql);
        System.out.println("🧪 Params: " + queryParts.values);

        return executeQuery(sql.toString(), queryParts.values, isListReturn || isTopOne);
    }

    // 메서드 이름 정리 (Top/First/All 제거 등)
    private String normalizeMethodName(String methodName) {
        if (methodName.startsWith("findTop") || methodName.startsWith("findFirst")) {
            return methodName.replaceFirst("find(Top|First)(\\d*)By", "findBy");
        } else if (methodName.startsWith("findAllBy")) {
            return methodName.replaceFirst("findAllBy", "");
        } else {
            return methodName.replaceFirst("findBy", "");
        }
    }

    // WHERE 조건 파싱
    private QueryParts parseConditions(String conditionPart, Object[] args) throws Exception {
        List<String> conditions = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        Matcher matcher = Pattern.compile("([A-Z][a-zA-Z0-9]+?)(Like|In|Between)?(And|$)").matcher(conditionPart);
        int paramIndex = 0;
        while (matcher.find()) {
            String field = matcher.group(1);
            String op = matcher.group(2);
            String fieldName = field.substring(0, 1).toLowerCase() + field.substring(1);
            Field f = entityClass.getDeclaredField(fieldName);
            Column columnAnn = f.getAnnotation(Column.class);
            if (columnAnn == null) continue;
            String column = columnAnn.name();

            if ("Like".equals(op)) {
                conditions.add(column + " LIKE ?");
                values.add(args[paramIndex++]);
            } else if ("In".equals(op)) {
                @SuppressWarnings("unchecked")
                List<?> list = (List<?>) args[paramIndex++];
                String placeholders = list.stream().map(e -> "?").collect(Collectors.joining(", "));
                conditions.add(column + " IN (" + placeholders + ")");
                values.addAll(list);
            } else if ("Between".equals(op)) {
                conditions.add(column + " BETWEEN ? AND ?");
                values.add(args[paramIndex++]);
                values.add(args[paramIndex++]);
            } else {
                conditions.add(column + " = ?");
                values.add(args[paramIndex++]);
            }
        }

        return new QueryParts(conditions, values);
    }

    // ORDER BY 구문 파싱
    private String parseOrderBy(String orderByPart) throws Exception {
        Matcher obMatcher = Pattern.compile("([A-Z][a-zA-Z0-9]+?)(Asc|Desc)?").matcher(orderByPart);
        List<String> orderBys = new ArrayList<>();
        while (obMatcher.find()) {
            String field = obMatcher.group(1);
            String direction = obMatcher.group(2);
            String fieldName = field.substring(0, 1).toLowerCase() + field.substring(1);
            Field f = entityClass.getDeclaredField(fieldName);
            Column columnAnn = f.getAnnotation(Column.class);
            if (columnAnn == null) continue;
            String column = columnAnn.name();
            orderBys.add(column + " " + ("Desc".equals(direction) ? "DESC" : "ASC"));
        }
        return String.join(", ", orderBys);
    }

    // @Table 어노테이션에서 테이블명 추출
    private String resolveTableName() {
        Table tableAnn = entityClass.getAnnotation(Table.class);
        if (tableAnn == null) {
            throw new IllegalStateException("@Table 어노테이션이 누락된 엔티티입니다: " + entityClass.getSimpleName());
        }
        return tableAnn.name();
    }

    // 쿼리 실행 및 결과 매핑
    private Object executeQuery(String sql, List<Object> values, boolean returnList) throws Exception {
        try (
                Connection conn = JDBCConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            for (int i = 0; i < values.size(); i++) {
                pstmt.setObject(i + 1, values.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            List<Object> result = new ArrayList<>();

            while (rs.next()) {
                Object entity = entityClass.getDeclaredConstructor().newInstance();
                for (Field field : entityClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    Column column = field.getAnnotation(Column.class);
                    if (column != null) {
                        Object value = rs.getObject(column.name());
                        field.set(entity, value);
                    }
                }
                result.add(entity);
                if (!returnList) break;
            }

            return returnList ? result : (result.isEmpty() ? null : result.get(0));
        }
    }

    // 조건/값 리스트 캡슐화용 DTO
    private static class QueryParts {
        List<String> conditions;
        List<Object> values;

        public QueryParts(List<String> conditions, List<Object> values) {
            this.conditions = conditions;
            this.values = values;
        }
    }
}
