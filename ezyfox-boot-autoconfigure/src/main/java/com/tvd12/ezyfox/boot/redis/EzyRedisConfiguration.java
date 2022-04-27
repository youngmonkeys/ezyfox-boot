package com.tvd12.ezyfox.boot.redis;

import com.tvd12.ezydata.redis.EzyRedisClientPool;
import com.tvd12.ezydata.redis.EzyRedisProxy;
import com.tvd12.ezydata.redis.EzyRedisProxyFactory;
import com.tvd12.ezydata.redis.loader.EzyJedisClientPoolLoader;
import com.tvd12.ezyfox.bean.EzyBeanAutoConfig;
import com.tvd12.ezyfox.bean.EzyPackagesToScanAware;
import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.bean.EzySingletonFactoryAware;
import com.tvd12.ezyfox.bean.annotation.EzyConfigurationBefore;
import com.tvd12.ezyfox.util.EzyPropertiesAware;
import lombok.Setter;

import java.util.Properties;
import java.util.Set;

@Setter
@EzyConfigurationBefore
public class EzyRedisConfiguration implements
    EzyBeanAutoConfig,
    EzyPropertiesAware,
    EzySingletonFactoryAware,
    EzyPackagesToScanAware {

    private Properties properties;
    private Set<String> packagesToScan;
    private EzySingletonFactory singletonFactory;

    @Override
    public void autoConfig() {
        singletonFactory.addSingleton("redisProxy", newRedisProxy());
    }

    private EzyRedisProxy newRedisProxy() {
        return EzyRedisProxyFactory.builder()
            .properties(properties)
            .scan(packagesToScan)
            .clientPool(newClientPool())
            .build()
            .newRedisProxy();
    }

    protected EzyRedisClientPool newClientPool() {
        return new EzyJedisClientPoolLoader()
            .properties(properties)
            .load();
    }
}
