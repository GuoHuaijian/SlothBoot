package com.sloth.boot.common.test;

import com.sloth.boot.common.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * MockMvc 测试基类。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@AutoConfigureMockMvc
public abstract class BaseMockMvcTest extends BaseSpringBootTest {

    @Autowired
    protected MockMvc mockMvc;

    /**
     * 发送 GET 请求。
     *
     * @param url 请求地址
     * @return 执行结果
     * @throws Exception 执行异常
     */
    protected ResultActions get(String url) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get(url));
    }

    /**
     * 发送 POST 请求。
     *
     * @param url  请求地址
     * @param body 请求体
     * @return 执行结果
     * @throws Exception 执行异常
     */
    protected ResultActions post(String url, Object body) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(body)));
    }

    /**
     * 发送 PUT 请求。
     *
     * @param url  请求地址
     * @param body 请求体
     * @return 执行结果
     * @throws Exception 执行异常
     */
    protected ResultActions put(String url, Object body) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.toJson(body)));
    }

    /**
     * 发送 DELETE 请求。
     *
     * @param url 请求地址
     * @return 执行结果
     * @throws Exception 执行异常
     */
    protected ResultActions delete(String url) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.delete(url));
    }
}
