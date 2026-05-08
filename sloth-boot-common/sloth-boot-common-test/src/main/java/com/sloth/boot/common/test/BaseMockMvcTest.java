package com.sloth.boot.common.test;

import com.sloth.boot.common.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    /**
     * 发送 GET 请求并携带查询参数。
     *
     * @param url    请求地址
     * @param params 参数键值对（key1, value1, key2, value2, ...）
     * @return 执行结果
     * @throws Exception 执行异常
     */
    protected ResultActions get(String url, Object... params) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get(url).params(toMultiValueMap(params)));
    }

    /**
     * 发送 POST 请求（JSON 内容类型，无请求体）。
     *
     * @param url 请求地址
     * @return 执行结果
     * @throws Exception 执行异常
     */
    protected ResultActions post(String url) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON));
    }

    /**
     * 断言响应成功（code = 0）。
     *
     * @param result 执行结果
     * @return 执行结果（支持链式调用）
     * @throws Exception 断言异常
     */
    protected ResultActions assertSuccess(ResultActions result) throws Exception {
        return result.andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    /**
     * 断言响应失败（code != 0）。
     *
     * @param result 执行结果
     * @return 执行结果（支持链式调用）
     * @throws Exception 断言异常
     */
    protected ResultActions assertFail(ResultActions result) throws Exception {
        return result.andExpect(jsonPath("$.code").isNumber())
                .andExpect(jsonPath("$.code").value(org.hamcrest.Matchers.not(0)));
    }

    /**
     * 将参数键值对转换为 MultiValueMap。
     */
    private static org.springframework.util.MultiValueMap<String, String> toMultiValueMap(Object... params) {
        org.springframework.util.LinkedMultiValueMap<String, String> map = new org.springframework.util.LinkedMultiValueMap<>();
        for (int i = 0; i < params.length - 1; i += 2) {
            map.add(params[i].toString(), params[i + 1].toString());
        }
        return map;
    }
}
