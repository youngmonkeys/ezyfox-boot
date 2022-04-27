package com.tvd12.ezyfox.boot.test.redis;

import com.tvd12.ezydata.redis.EzyRedisClientPool;
import com.tvd12.ezyfox.boot.redis.EzyRedisConfiguration;

import static org.mockito.Mockito.mock;

public class EzyRedisConfigForTest extends EzyRedisConfiguration {

    protected EzyRedisClientPool newClientPool() {
        EzyRedisClientPool clientPool = mock(EzyRedisClientPool.class);
        return clientPool;
    }
}
