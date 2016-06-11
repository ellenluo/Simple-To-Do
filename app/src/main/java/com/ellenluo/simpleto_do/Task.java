package com.ellenluo.simpleto_do;

public class Task {

    private int id;
    private String name, details, list;

    // constructors
    public Task(int id, String name, String details, String list) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.list = list;
    }

    public Task(String name, String details, String list) {
        this.name = name;
        this.details = details;
        this.list = list;
    }

    // getter
    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDetails() {
        return this.details;
    }

    public String getList() {
        return this.list;
    }

    // setter
    public void setName(String name) {
        this.name = name;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setList(String list) {
        this.list = list;
    }
}
