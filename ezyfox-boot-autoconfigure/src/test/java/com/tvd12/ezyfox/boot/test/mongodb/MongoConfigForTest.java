package com.tvd12.ezyfox.boot.test.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.tvd12.ezydata.database.EzyDatabaseContext;
import com.tvd12.ezydata.mongodb.loader.EzySimpleMongoClientLoader;
import com.tvd12.ezyfox.boot.mongodb.MongoConfiguration;
import org.bson.BsonDocument;

import static org.mockito.Mockito.*;

public class MongoConfigForTest extends MongoConfiguration {
	
	@Override
	protected MongoClient newMongoClient() {
		MongoClient mongoClient = mock(MongoClient.class);
		MongoDatabase mongoDatabase = mock(MongoDatabase.class);
		when(mongoClient.getDatabase("chattutorial")).thenReturn(
				mongoDatabase
		);
		
		MongoCollection mongoCollection = mock(MongoCollection.class);
		when(mongoDatabase.getCollection("A")).thenReturn(
				mongoCollection
		);
		return mongoClient;
	}
}
