package com.tvd12.ezyfox.boot.test.jpa;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class User {

    @Id
    private int id;
    private String name;
}
