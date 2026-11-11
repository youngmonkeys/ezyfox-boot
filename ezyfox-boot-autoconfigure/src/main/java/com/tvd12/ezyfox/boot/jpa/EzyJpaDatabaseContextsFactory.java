package com.tvd12.ezyfox.boot.jpa;

import com.tvd12.ezydata.database.EzyDatabaseContext;
import com.tvd12.ezyfox.bean.EzySingletonFactory;

import java.util.List;
import java.util.Properties;
import java.util.Set;

public interface EzyJpaDatabaseContextsFactory {

    List<EzyDatabaseContext> createDatabaseContexts(
        Properties properties,
        Set<String> packagesToScan,
        EzySingletonFactory singletonFactory
    );
}
