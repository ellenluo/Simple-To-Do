package com.ellenluo.simpleto_do;

public class List {

    private int id;
    private String name;

    // constructors
    public List(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public List(String name) {
        this.name = name;
    }

    // getters
    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

}
