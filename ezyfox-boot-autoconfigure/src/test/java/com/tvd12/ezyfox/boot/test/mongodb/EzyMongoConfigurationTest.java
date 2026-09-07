package com.tvd12.ezyfox.boot.test.mongodb;

import com.mongodb.client.MongoClient;
import com.tvd12.ezydata.database.EzyDatabaseContext;
import com.tvd12.ezydata.mongodb.repository.EzyMongoMaxIdRepository;
import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.bean.impl.EzyBeanKey;
import com.tvd12.ezyfox.boot.mongodb.EzyMongoConfiguration;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

public class EzyMongoConfigurationTest {

    @Test
    public void autoConfigWithSharedDatabaseContext() {
        // given
        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        EzyDatabaseContext databaseContext = mock(EzyDatabaseContext.class);
        when(databaseContext.getRepositories()).thenReturn(Collections.emptyMap());
        when(singletonFactory.getSingleton(EzyBeanKey.of(
            "sharedDatabaseContext",
            EzyDatabaseContext.class
        ))).thenReturn(databaseContext);

        EzyMongoConfiguration sut = new EzyMongoConfiguration();
        sut.setSingletonFactory(singletonFactory);

        // when
        sut.autoConfig();

        // then
        verify(databaseContext).getRepositories();
        verify(singletonFactory, never()).getSingleton(EzyBeanKey.of(
            "sharedMongoClient",
            MongoClient.class
        ));
    }

    @Test
    public void newMongoClientWithSharedMongoClient() {
        // given
        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        MongoClient mongoClient = mock(MongoClient.class);
        when(singletonFactory.getSingleton(EzyBeanKey.of(
            "sharedMongoClient",
            MongoClient.class
        ))).thenReturn(mongoClient);

        MongoConfiguration sut = new MongoConfiguration();
        sut.setSingletonFactory(singletonFactory);

        // when
        MongoClient actual = sut.loadMongoClient();

        // then
        assertSame(actual, mongoClient);
    }

    @Test
    public void newMongoClientWithoutSharedMongoClient() {
        // given
        MongoConfiguration sut = new MongoConfiguration();
        Properties properties = new Properties();
        properties.setProperty(
            "database.mongo.uri",
            "mongodb://127.0.0.1:27017"
        );
        sut.setProperties(properties);
        sut.setSingletonFactory(mock(EzySingletonFactory.class));

        // when
        MongoClient actual = sut.loadMongoClient();

        // then
        assertNotNull(actual);
        actual.close();
    }

    @Test
    public void autoConfigSuccess() {
        // given
        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);

        Set<String> packagesToScan = RandomUtil.randomSet(8, String.class);
        packagesToScan.add("com.tvd12.ezyfox.boot.test.mongodb");
        Properties properties = new Properties();

        EzyMongoConfiguration sut = new EzyMongoConfigForTest();
        sut.setPackagesToScan(packagesToScan);
        sut.setProperties(properties);
        sut.setSingletonFactory(singletonFactory);
        sut.setDatabaseName("mock_db");

        // when
        sut.autoConfig();

        // then
        verify(
            singletonFactory,
            times(1)
        ).addSingleton(eq("ezyMaxIdRepository"), any(EzyMongoMaxIdRepository.class));
        verify(
            singletonFactory,
            times(1)
        ).addSingleton(eq("aRepo"), any(ARepo.class));
    }

    private static class MongoConfiguration extends EzyMongoConfiguration {

        private MongoClient loadMongoClient() {
            return newMongoClient();
        }
    }
}
