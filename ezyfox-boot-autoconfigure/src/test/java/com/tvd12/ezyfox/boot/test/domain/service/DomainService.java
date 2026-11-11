package com.tvd12.ezyfox.boot.test.domain.service;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfox.boot.test.ecommerce.entity.Product;
import com.tvd12.ezyfox.boot.test.ecommerce.entity.ProductBook;
import com.tvd12.ezyfox.boot.test.ecommerce.repo.ProductBookRepo;
import com.tvd12.ezyfox.boot.test.ecommerce.repo.ProductRepo;
import com.tvd12.ezyfox.boot.test.ezyplatform.entity.DataIndex;
import com.tvd12.ezyfox.boot.test.ezyplatform.entity.User;
import com.tvd12.ezyfox.boot.test.ezyplatform.repo.DataIndexRepo;
import com.tvd12.ezyfox.boot.test.ezyplatform.repo.UserRepo;
import lombok.Setter;

@Setter
@EzySingleton
public class DomainService {

    @EzyAutoBind
    private UserRepo userRepo;

    @EzyAutoBind
    private DataIndexRepo dataIndexRepo;

    @EzyAutoBind
    private ProductRepo productRepo;

    @EzyAutoBind
    private ProductBookRepo productBookRepo;

    public User saveUser(User user) {
        userRepo.save(user);
        return user;
    }

    public User getUserById(long id) {
        return userRepo.findById(id);
    }

    public Product saveProduct(Product product) {
        productRepo.save(product);
        return product;
    }

    public Product getProductById(long id) {
        return productRepo.findById(id);
    }

    public ProductBook saveProductBook(ProductBook productBook) {
        productBookRepo.save(productBook);
        return productBook;
    }

    public ProductBook getProductBookByProductId(long productId) {
        return productBookRepo.findById(productId);
    }

    public DataIndex saveDataIndex(DataIndex dataIndex) {
        dataIndexRepo.save(dataIndex);
        return dataIndex;
    }

    public DataIndex getDataIndexById(long id) {
        return dataIndexRepo.findById(id);
    }
}
