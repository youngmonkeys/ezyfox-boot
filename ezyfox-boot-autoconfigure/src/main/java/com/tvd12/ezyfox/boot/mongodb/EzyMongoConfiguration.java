package com.tvd12.ezyfox.boot.mongodb;

import com.mongodb.MongoClient;
import com.tvd12.ezydata.database.EzyDatabaseContext;
import com.tvd12.ezydata.mongodb.EzyMongoDatabaseContextBuilder;
import com.tvd12.ezydata.mongodb.loader.EzySimpleMongoClientLoader;
import com.tvd12.ezyfox.annotation.EzyProperty;
import com.tvd12.ezyfox.bean.EzyBeanAutoConfig;
import com.tvd12.ezyfox.bean.EzyPackagesToScanAware;
import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.bean.EzySingletonFactoryAware;
import com.tvd12.ezyfox.bean.annotation.EzyConfigurationBefore;
import com.tvd12.ezyfox.util.EzyPropertiesAware;
import lombok.Setter;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Setter
@EzyConfigurationBefore
public class EzyMongoConfiguration implements
		EzyBeanAutoConfig,
		EzyPropertiesAware,
		EzySingletonFactoryAware,
		EzyPackagesToScanAware {
	
	
	@EzyProperty("database.mongo.database")
	private String databaseName;
	
	private Properties properties;
	
	private Set<String> packagesToScan;
	
	private EzySingletonFactory singletonFactory;
	
	@Override
	public void autoConfig() {
		EzyDatabaseContext databaseContext = newMongodbDatabaseContext();
		Map<String, Object> repos = databaseContext.getRepositoriesByName();
		for (String repoName : repos.keySet()) {
			singletonFactory.addSingleton(repoName, repos.get(repoName));
		}
	}
	
	private EzyDatabaseContext newMongodbDatabaseContext() {
		EzyMongoDatabaseContextBuilder builder = new EzyMongoDatabaseContextBuilder()
				.properties(properties)
				.mongoClient(newMongoClient())
				.databaseName(databaseName);
		
		for (String p : packagesToScan) {
			builder.scan(p);
		}
		return builder.build();
	}
	
	protected MongoClient newMongoClient() {
		return EzySimpleMongoClientLoader.load(properties);
	}
	
}

