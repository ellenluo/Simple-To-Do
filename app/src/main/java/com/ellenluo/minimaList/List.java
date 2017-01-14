package com.ellenluo.minimaList;

class List {

    // parameters
    private long id;
    private String name;

    // constructors
    List(long id, String name) {
        this.id = id;
        this.name = name;
    }

    List(String name) {
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
