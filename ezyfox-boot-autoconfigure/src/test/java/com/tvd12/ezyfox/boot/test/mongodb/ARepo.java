package com.tvd12.ezyfox.boot.test.mongodb;

import com.tvd12.ezydata.mongodb.EzyMongoRepository;
import com.tvd12.ezyfox.database.annotation.EzyRepository;

@EzyRepository
public interface ARepo extends EzyMongoRepository<Integer, A> {
}
