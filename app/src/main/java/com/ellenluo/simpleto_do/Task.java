package com.ellenluo.simpleto_do;

public class Task {

    private int id;
    private String name, details;

    public Task(int id, String name, String details) {
        this.id = id;
        this.name = name;
        this.details = details;
    }

    public Task(String name, String details) {
        this.name = name;
        this.details = details;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDetails() {
        return this.details;
    }
}
