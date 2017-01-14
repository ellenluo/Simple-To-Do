package com.ellenluo.minimaList;

public class Task {

    // parameters
    private long id, due, remind, list;
    private String name, details;

    // constructors
    public Task(long id, String name, String details, long due, long remind, long list) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.due = due;
        this.remind = remind;
        this.list = list;
    }

    public Task(String name, String details, long due, long remind, long list) {
        this.name = name;
        this.details = details;
        this.due = due;
        this.remind = remind;
        this.list = list;
    }

    // getters
    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDetails() {
        return this.details;
    }

    public long getList() {
        return this.list;
    }

    long getDue() {
        return this.due;
    }

    long getRemind() {
        return this.remind;
    }

    // setters
    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    void setDue(long due) {
        this.due = due;
    }

    void setRemind(long remind) {
        this.remind = remind;
    }

    public void setList(long list) {
        this.list = list;
    }
}
