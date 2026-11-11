package com.tvd12.ezyfox.boot.test.ezyplatform.repo;

import com.tvd12.ezydata.database.EzyDatabaseRepository;
import com.tvd12.ezyfox.boot.test.ezyplatform.entity.User;
import com.tvd12.ezyfox.database.annotation.EzyRepository;

@EzyRepository
public interface UserRepo extends EzyDatabaseRepository<Long, User> {}
