package com.ellenluo.minimaList;

public class List {

    private long id;
    private String name;

    // constructors
    public List(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public List(String name) {
        this.name = name;
    }

    // setters
    public void setName(String name) {
        this.name = name;
    }

    // getters
    public String getName() {
        return this.name;
    }

    public long getId() {
        return this.id;
    }

}
