package com.tvd12.ezyfox.boot.test.domain.plugin;

import com.tvd12.ezyfox.bean.EzyBeanAutoConfig;
import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.bean.EzySingletonFactoryAware;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzyConfigurationBefore;
import lombok.Setter;

@Setter
@EzyConfigurationBefore(priority = Integer.MIN_VALUE)
public class DomainDatabaseConfiguration implements
    EzyBeanAutoConfig,
    EzySingletonFactoryAware {

    @EzyAutoBind
    private EzySingletonFactory singletonFactory;

    @Override
    public void autoConfig() {
        singletonFactory.addSingleton(
            "sharedDatabaseContextsFactory",
            new DomainDatabaseContextsFactory()
        );
    }
}
