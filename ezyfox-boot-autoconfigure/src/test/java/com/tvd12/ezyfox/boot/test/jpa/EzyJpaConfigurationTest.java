package com.tvd12.ezyfox.boot.test.jpa;

import com.tvd12.ezydata.database.EzyDatabaseContext;
import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.bean.impl.EzyBeanKey;
import com.tvd12.ezyfox.boot.jpa.EzyJpaConfiguration;
import com.tvd12.properties.file.reader.BaseFileReader;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertSame;

public class EzyJpaConfigurationTest {

    @Test
    public void dataSourceWithSharedDataSource() throws Exception {
        // given
        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        DataSource dataSource = mock(DataSource.class);
        when(singletonFactory.getSingleton(EzyBeanKey.of(
            "sharedDataSource",
            DataSource.class
        ))).thenReturn(dataSource);

        EzyJpaConfiguration sut = new EzyJpaConfiguration();
        sut.setSingletonFactory(singletonFactory);

        Method method = EzyJpaConfiguration.class
            .getDeclaredMethod("dataSource");
        method.setAccessible(true);

        // when
        DataSource actual = (DataSource) method.invoke(sut);

        // then
        assertSame(actual, dataSource);
    }

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

        EzyJpaConfiguration sut = new EzyJpaConfiguration();
        sut.setSingletonFactory(singletonFactory);

        // when
        sut.config();

        // then
        verify(databaseContext).getRepositories();
        verify(singletonFactory, never()).getSingleton(EzyBeanKey.of(
            "sharedEntityManagerFactory",
            EntityManagerFactory.class
        ));
    }

    @Test
    public void autoConfigWithSharedEntityManagerFactory() {
        // given
        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
        EntityManagerFactory entityManagerFactory = mock(EntityManagerFactory.class);
        when(singletonFactory.getSingleton(EzyBeanKey.of(
            "sharedEntityManagerFactory",
            EntityManagerFactory.class
        ))).thenReturn(entityManagerFactory);

        EzyJpaConfiguration sut = new EzyJpaConfiguration();
        sut.setPackagesToScan(Collections.emptySet());
        sut.setProperties(new Properties());
        sut.setSingletonFactory(singletonFactory);

        // when
        sut.config();

        // then
        verify(singletonFactory).getSingleton(EzyBeanKey.of(
            "sharedEntityManagerFactory",
            EntityManagerFactory.class
        ));
        verify(singletonFactory, never()).getSingleton(EzyBeanKey.of(
            "sharedDataSource",
            javax.sql.DataSource.class
        ));
    }

    @Test
    public void autoConfigSuccess() {
        // given
        EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);

        Set<String> packagesToScan = RandomUtil.randomSet(8, String.class);
        packagesToScan.add("com.tvd12.ezyfox.boot.test.jpa");
        Properties properties = new BaseFileReader()
            .read("application.properties");

        EzyJpaConfiguration sut = new EzyJpaConfiguration();
        sut.setPackagesToScan(packagesToScan);
        sut.setProperties(properties);
        sut.setSingletonFactory(singletonFactory);

        // when
        sut.config();

        // then
        verify(
            singletonFactory,
            times(1)
        ).addSingleton(eq("userRepo"), any(UserRepo.class));
    }
}
