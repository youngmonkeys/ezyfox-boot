package com.tvd12.ezyfox.boot.test.jpa;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class User {
    @Id
    private int id;
    private String name;
}
