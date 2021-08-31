package com.tvd12.ezyfox.boot.test.jpa;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Properties;
import java.util.Set;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.boot.mongodb.EzyJpaConfiguration;
import com.tvd12.properties.file.reader.BaseFileReader;
import com.tvd12.test.util.RandomUtil;

public class EzyJpaConfigurationTest {
	
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
		verify(singletonFactory, times(1)).addSingleton(eq("userRepo"), any(UserRepo.class));
	}
	
}
