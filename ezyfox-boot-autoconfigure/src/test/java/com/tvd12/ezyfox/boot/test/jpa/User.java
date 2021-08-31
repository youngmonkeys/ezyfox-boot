package com.tvd12.ezyfox.boot.test.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class User {
	
	@Id
	private int id;
	private String name;
}
