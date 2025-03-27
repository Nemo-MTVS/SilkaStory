package com.silkastory.common;

import com.silkastory.infrastructure.database.JDBCConnection;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MiniJpa<T, ID> implements MiniJPARepository<T, ID> {

    private final Class<T> clazz;

    public MiniJpa(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T save(T entity) {
        Object id = getIdValue(entity);
        if (id == null || (id instanceof Number && ((Number) id).longValue() == 0)) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }

    @Override
    public void insert(T entity) {
        String tableName = clazz.getAnnotation(Table.class).name();
        List<String> columns = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        Field idField = null;

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getAnnotation(Id.class) != null) {
                idField = field;
                continue;
            }
            Column col = field.getAnnotation(Column.class);
            if (col != null) {
                String columnName = col.name().isEmpty() ? field.getName() : col.name();
                columns.add(columnName);
                try {
                    values.add(field.get(entity));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        String placeholders = columns.stream().map(c -> "?").collect(Collectors.joining(", "));
        String sql = "INSERT INTO " + tableName + " (" + String.join(", ", columns) + ") VALUES (" + placeholders + ")";

        try (
                Connection conn = JDBCConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            for (int i = 0; i < values.size(); i++) {
                pstmt.setObject(i + 1, values.get(i));
            }
            pstmt.executeUpdate();

            if (idField != null) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    Long generatedId = rs.getLong(1);
                    idField.setAccessible(true);
                    idField.set(entity, generatedId);
                }
            }
        } catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(T entity) {
        String tableName = clazz.getAnnotation(Table.class).name();
        List<String> sets = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        Object idValue = null;
        String idColumn = null;

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getAnnotation(Id.class) != null) {
                try {
                    idValue = field.get(entity);
                    idColumn = field.getName();
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }
            Column col = field.getAnnotation(Column.class);
            if (col != null) {
                String columnName = col.name().isEmpty() ? field.getName() : col.name();
                sets.add(columnName + " = ?");
                try {
                    values.add(field.get(entity));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        String sql = "UPDATE " + tableName + " SET " + String.join(", ", sets) + " WHERE " + idColumn + " = ?";

        try (
                Connection conn = JDBCConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            for (int i = 0; i < values.size(); i++) {
                pstmt.setObject(i + 1, values.get(i));
            }
            pstmt.setObject(values.size() + 1, idValue);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T findById(ID id) {
        String tableName = clazz.getAnnotation(Table.class).name();
        Field idField = getIdField(clazz);
        String idColumn = idField.getName();

        String sql = "SELECT * FROM " + tableName + " WHERE " + idColumn + " = ?";

        try (
                Connection conn = JDBCConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setObject(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                T instance = clazz.getDeclaredConstructor().newInstance();
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    String columnName = field.isAnnotationPresent(Column.class)
                            ? field.getAnnotation(Column.class).name()
                            : field.getName();
                    Object value = rs.getObject(columnName);
                    field.set(instance, value);
                }
                return instance;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<T> findAll() {
        String tableName = clazz.getAnnotation(Table.class).name();
        String sql = "SELECT * FROM " + tableName;
        List<T> result = new ArrayList<>();

        try (
                Connection conn = JDBCConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                T instance = clazz.getDeclaredConstructor().newInstance();
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    String columnName = field.isAnnotationPresent(Column.class)
                            ? field.getAnnotation(Column.class).name()
                            : field.getName();
                    Object value = rs.getObject(columnName);
                    field.set(instance, value);
                }
                result.add(instance);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public void deleteById(ID id) {
        String tableName = clazz.getAnnotation(Table.class).name();
        Field idField = getIdField(clazz);
        String idColumn = idField.getName();

        String sql = "DELETE FROM " + tableName + " WHERE " + idColumn + " = ?";

        try (
                Connection conn = JDBCConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setObject(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Field getIdField(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                return field;
            }
        }
        throw new RuntimeException("ID 필드를 찾을 수 없습니다.");
    }

    private Object getIdValue(T entity) {
        Field idField = getIdField(entity.getClass());
        try {
            return idField.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}