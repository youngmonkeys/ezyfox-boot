package com.tvd12.ezyfox.boot.test.ecommerce.repo;

import com.tvd12.ezydata.database.EzyDatabaseRepository;
import com.tvd12.ezyfox.boot.test.ecommerce.entity.ProductBook;
import com.tvd12.ezyfox.database.annotation.EzyRepository;

@EzyRepository
public interface ProductBookRepo extends EzyDatabaseRepository<Long, ProductBook> {}
