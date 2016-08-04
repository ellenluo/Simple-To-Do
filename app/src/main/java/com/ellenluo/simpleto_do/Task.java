package com.ellenluo.simpleto_do;

public class Task {

    private long id, due, remind;
    private String name, details, list;

    // constructors
    public Task(long id, String name, String details, long due, long remind, String list) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.due = due;
        this.remind = remind;
        this.list = list;
    }

    public Task(String name, String details, long due, long remind, String list) {
        this.name = name;
        this.details = details;
        this.due = due;
        this.remind = remind;
        this.list = list;
    }

    // getter
    public long getId() {
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

    public long getDue() {
        return this.due;
    }

    public long getRemind() {
        return this.remind;
    }

    // setter
    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setDue(long due) {
        this.due = due;
    }

    public void setRemind(long remind) {
        this.remind = remind;
    }

    public void setList(String list) {
        this.list = list;
    }
}
