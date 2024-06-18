package com.hoarp.web.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JdbcTemplate {
    private static JdbcTemplate jdbcTemplate;
    private Connection connection;

    private JdbcTemplate() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoarp", "root", "123456");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static JdbcTemplate getInstance() {
        if (jdbcTemplate == null) {
            synchronized (JdbcTemplate.class) {
                if (jdbcTemplate == null) {
                    jdbcTemplate = new JdbcTemplate();
                }
            }
        }
        return jdbcTemplate;
    }

    @FunctionalInterface
    public interface FuntionWithSQLException<T, R> {
        R apply (T t) throws SQLException;
    }

    public <T> T query(String sql, FuntionWithSQLException<ResultSet, T> mapper, Object... params) {
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapper.apply(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface RowMapper<T> {
        T mapRow(ResultSet rs) throws SQLException;
    }

    public <T> List<T> queryList(String sql, RowMapper<T> rowMapper, Object... params) {
        List<T> results = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(rowMapper.mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

//    public int update(String sql, Object... params) {
//        try {
//            PreparedStatement ps = connection.prepareStatement(sql);
//            setParameters(ps, params);
//            return ps.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }

    public long update(String sql, Object... params) {
        try {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setParameters(ps, params);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return 0;
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    // no id column
                    return 0;
                }
            }
        } catch (SQLException e) {
            if (Objects.equals(e.getSQLState(), "23000")) {
                throw new SQLDuplicateException();
            } else if (Objects.equals(e.getSQLState(), "45000")) {
                throw new SQLCustomException(e.getMessage());
            }
            e.printStackTrace();
            return 0;
        }
    }

    private void setParameters(PreparedStatement ps, Object[] args) throws SQLException {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
        }
    }
}
