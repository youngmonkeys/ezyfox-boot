package com.tvd12.ezyfox.boot.test.mongodb;

import com.tvd12.ezyfox.annotation.EzyId;
import com.tvd12.ezyfox.database.annotation.EzyCollection;
import lombok.Data;

@Data
@EzyCollection
public class A {
	
	@EzyId
	int id;
}
