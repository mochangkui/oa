package test.com.imooc.oa.utils;

import com.imooc.oa.utils.MybatisUtils;
import org.junit.jupiter.api.Test;

public class MybatisUtilsTestor {
    @Test
    public void testcase1 () {
        Object result = MybatisUtils.executeQuery(sqlSession -> {
            Object out = sqlSession.selectOne("sample.test");
            return out;
        });
        System.out.println(result);
    }
}
