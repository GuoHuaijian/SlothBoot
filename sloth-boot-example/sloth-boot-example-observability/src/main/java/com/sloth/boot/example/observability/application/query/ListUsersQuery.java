package com.sloth.boot.example.observability.application.query;

import com.sloth.boot.example.observability.application.model.convert.UserConvert;
import com.sloth.boot.example.observability.application.model.vo.UserVO;
import com.sloth.boot.example.observability.infrastructure.repository.mapper.DemoUserMapper;
import com.sloth.boot.example.observability.application.helper.MetricsSupport;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.Meter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 用户列表查询（读操作）。
 *
 * @author sloth-boot
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ListUsersQuery {

    private final DemoUserMapper userMapper;
    private final UserConvert userConvert;
    private final Meter meter;

    private final MetricsSupport.DoubleHistogramHolder userListLatency = new MetricsSupport.DoubleHistogramHolder();

    private DoubleHistogram userListLatency() {
        return MetricsSupport.lazyHistogram(meter, userListLatency,
                "demo.user.list.latency", "User list query latency");
    }

    /**
     * 执行用户列表查询。
     *
     * @return 用户列表
     */
    public List<UserVO> execute() {
        log.info("Listing users");
        long start = System.currentTimeMillis();
        List<UserVO> users = userConvert.toVOList(userMapper.selectAllOrdered());
        userListLatency().record(System.currentTimeMillis() - start + ThreadLocalRandom.current().nextInt(5, 20));
        return users;
    }
}
