package com.tvd12.ezyfox.boot.test.ecommerce.repo;

import com.tvd12.ezydata.database.EzyDatabaseRepository;
import com.tvd12.ezyfox.boot.test.ecommerce.entity.Product;
import com.tvd12.ezyfox.database.annotation.EzyRepository;

@EzyRepository
public interface ProductRepo extends EzyDatabaseRepository<Long, Product> {}
