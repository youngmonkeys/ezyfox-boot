package com.tvd12.ezyfox.boot.test.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.tvd12.ezyfox.boot.mongodb.EzyMongoConfiguration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EzyMongoConfigForTest extends EzyMongoConfiguration {
	
	@Override
	protected MongoClient newMongoClient() {
		MongoClient mongoClient = mock(MongoClient.class);
		MongoDatabase mongoDatabase = mock(MongoDatabase.class);
		when(mongoClient.getDatabase("mock_db")).thenReturn(
				mongoDatabase
		);
		
		MongoCollection mongoCollection = mock(MongoCollection.class);
		when(mongoDatabase.getCollection("A")).thenReturn(
				mongoCollection
		);
		return mongoClient;
	}
}
