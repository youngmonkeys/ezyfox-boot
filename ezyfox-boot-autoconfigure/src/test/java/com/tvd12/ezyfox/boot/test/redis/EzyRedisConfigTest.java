package com.tvd12.ezyfox.boot.test.redis;

import com.tvd12.ezydata.redis.EzyRedisProxy;
import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.boot.redis.EzyRedisConfiguration;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.util.Properties;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class EzyRedisConfigTest {

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
}
