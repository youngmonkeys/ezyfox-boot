package com.tvd12.ezyfox.boot.test.ezyplatform.repo;

import com.tvd12.ezydata.database.EzyDatabaseRepository;
import com.tvd12.ezyfox.boot.test.ezyplatform.entity.DataIndex;
import com.tvd12.ezyfox.database.annotation.EzyRepository;

@EzyRepository
public interface DataIndexRepo extends EzyDatabaseRepository<Long, DataIndex> {}
