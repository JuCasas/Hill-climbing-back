package com.dp1.route4d.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.internal.NotNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    int id;

    @NotNull
    private String name;

    public Person(@JsonProperty("id") int id,
                  @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }


    public Person() {
    }


    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }
}