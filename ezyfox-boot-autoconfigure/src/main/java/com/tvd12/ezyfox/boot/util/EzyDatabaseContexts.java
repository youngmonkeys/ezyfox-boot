package com.tvd12.ezyfox.boot.util;

import static com.tvd12.ezyfox.bean.impl.EzyBeanNameParser.getBeanName;

import com.tvd12.ezydata.database.EzyDatabaseContext;
import com.tvd12.ezyfox.bean.EzySingletonFactory;

public class EzyDatabaseContexts {

    private EzyDatabaseContexts() {}
    
    public static void addRepositoriesFromDatabaseContextToSingletonFactory(
        EzyDatabaseContext databaseContext,
        EzySingletonFactory singletonFactory
    ) {
        databaseContext
            .getRepositories()
            .forEach((repoType, repo) ->
                singletonFactory.addSingleton(getBeanName(repoType), repo)
            );
    }
    
}
