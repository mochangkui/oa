package com.imooc.oa.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Function;

public class MybatisUtils {
    // 利用static属于类不属于对象，且全局唯一
    private static SqlSessionFactory sqlSessionFactory = null;
    // 利用静态代码块在初始化时实例化sqlSessionFactory
    static {
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader("mybatis-config.xml");
            // 实例化sql会话工厂
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            // 初始化错误时，通过抛出异常ExceptionInInitializerError通知调用者
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * 执行SELECT查询SQL
     * @param func 要执行查询语句的代码块
     * @return 查询结果
     */
    public static Object executeQuery (Function<SqlSession, Object> func) {
        // 利用sql会话工厂打开一个会话
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            Object obj = func.apply(sqlSession);
            return obj;
        } finally {
            // 关闭sql会话
            sqlSession.close();
        }
    }

    /**
     * 执行INSERT/UPDATE/DELETE写操作SQL
     * @param func 要执行的写操作代码块
     * @return 写操作后返回的结果
     */
    public static Object executeUpdate (Function<SqlSession, Object> func) {
        // 利用sql会话工厂打开一个会话
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        try {
            Object obj = func.apply(sqlSession);
            sqlSession.commit();
            return obj;
        } catch (RuntimeException e) {
            sqlSession.rollback();
            throw e;
        } finally {
            // 关闭sql会话
            sqlSession.close();
        }
    }
}
