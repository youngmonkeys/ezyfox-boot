package com.tvd12.ezyfox.boot.mongodb;

import java.util.Properties;
import java.util.Set;

import com.tvd12.ezyfox.bean.EzyBeanAutoConfig;
import com.tvd12.ezyfox.bean.EzyPackagesToScanAware;
import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.bean.EzySingletonFactoryAware;
import com.tvd12.ezyfox.bean.annotation.EzyConfigurationBefore;
import com.tvd12.ezyfox.util.EzyPropertiesAware;

import lombok.Setter;

@EzyConfigurationBefore
public class MongoConfiguration implements 
		EzyBeanAutoConfig,
		EzyPropertiesAware,
		EzySingletonFactoryAware,
		EzyPackagesToScanAware {

	@Setter
	private Properties properties;
	
	@Setter
	private Set<String> packagesToScan;
	
	@Setter
	private EzySingletonFactory singletonFactory;
	
	@Override
	public void autoConfig() {
		
	}
	
}
