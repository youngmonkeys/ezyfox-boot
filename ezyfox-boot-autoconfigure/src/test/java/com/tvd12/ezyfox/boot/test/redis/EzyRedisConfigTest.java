package com.tvd12.ezyfox.boot.test.redis;

import com.tvd12.ezydata.redis.EzyRedisClient;
import com.tvd12.ezydata.redis.EzyRedisClientPool;
import com.tvd12.ezydata.redis.EzyRedisProxy;
import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.bean.impl.EzyBeanKey;
import com.tvd12.ezyfox.boot.redis.EzyRedisConfiguration;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertNotNull;

public class EzyRedisConfigTest {

    @Test
    public void autoConfigWithSharedRedisProxy() {
        // given
        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        EzyRedisProxy redisProxy = mock(EzyRedisProxy.class);
        when(singletonFactory.getSingleton(EzyBeanKey.of(
            "sharedRedisProxy",
            EzyRedisProxy.class
        ))).thenReturn(redisProxy);

        EzyRedisConfiguration sut = new EzyRedisConfiguration();
        sut.setSingletonFactory(singletonFactory);

        // when
        sut.autoConfig();

        // then
        verify(singletonFactory).addSingleton("redisProxy", redisProxy);
        verify(singletonFactory, never()).getSingleton(EzyBeanKey.of(
            "sharedRedisClientPool",
            EzyRedisClientPool.class
        ));
    }

    @Test
    public void autoConfigWithSharedRedisClientPool() {
        // given
        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        EzyRedisClientPool clientPool = mock(EzyRedisClientPool.class);
        when(clientPool.getClient()).thenReturn(mock(EzyRedisClient.class));
        when(singletonFactory.getSingleton(EzyBeanKey.of(
            "sharedRedisClientPool",
            EzyRedisClientPool.class
        ))).thenReturn(clientPool);

        EzyRedisConfiguration sut = new EzyRedisConfiguration();
        sut.setPackagesToScan(Collections.emptySet());
        sut.setProperties(new Properties());
        sut.setSingletonFactory(singletonFactory);

        // when
        sut.autoConfig();

        // then
        verify(clientPool).getClient();
        verify(singletonFactory).addSingleton(
            eq("redisProxy"),
            any(EzyRedisProxy.class)
        );
    }

    @Test
    public void newClientPoolWithoutSharedRedisClientPool() throws IOException {
        // given
        RedisConfiguration sut = new RedisConfiguration();
        sut.setProperties(new Properties());
        sut.setSingletonFactory(mock(EzySingletonFactory.class));

        // when
        EzyRedisClientPool actual = sut.loadClientPool();

        // then
        assertNotNull(actual);
        actual.close();
    }

    @Test
    public void autoConfigSuccess() {
        // given
        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);

        Set<String> packagesToScan = RandomUtil.randomSet(8, String.class);
        packagesToScan.add("com.tvd12.ezyfox.boot.test.redis");
        Properties properties = new Properties();

        EzyRedisConfiguration sut = new EzyRedisConfigForTest();
        sut.setPackagesToScan(packagesToScan);
        sut.setProperties(properties);
        sut.setSingletonFactory(singletonFactory);

        // when
        sut.autoConfig();

        // then
        verify(
            singletonFactory,
            times(1)
        ).addSingleton(eq("redisProxy"), any(EzyRedisProxy.class));
    }

    private static class RedisConfiguration extends EzyRedisConfiguration {

        private EzyRedisClientPool loadClientPool() {
            return newClientPool();
        }
    }
}
