package com.tvd12.ezyfox.boot.test.jpa;

import com.tvd12.ezydata.database.EzyDatabaseRepository;
import com.tvd12.ezyfox.database.annotation.EzyRepository;

@EzyRepository
public interface UserRepo extends EzyDatabaseRepository<Integer, User> {
}
