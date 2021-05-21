package com.tvd12.ezyfox.boot.test.mongodb;

import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

import java.util.Properties;
import java.util.Set;

import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.boot.mongodb.MongoConfiguration;
import com.tvd12.test.util.RandomUtil;

public class MongoConfigurationTest {

	@Test
	public void autoConfigSuccess() {
		// given
		EzySingletonFactory singletonFactory = mock(EzySingletonFactory.class);
		Set<String> packagesToScan = RandomUtil.randomSet(8, String.class);
		Properties properties = new Properties();
		
		MongoConfiguration sut = new MongoConfiguration();
		sut.setPackagesToScan(packagesToScan);
		sut.setProperties(properties);
		sut.setSingletonFactory(singletonFactory);
		
		// when
		sut.autoConfig();
	}
	
}
