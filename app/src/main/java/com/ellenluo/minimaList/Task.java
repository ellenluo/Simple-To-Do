package com.ellenluo.minimaList;

/**
 * Task
 * Created by Ellen Luo
 * Class to represent a Task object with id, name, details, due date, reminder and list parameters.
 */

public class Task {

    // parameters
    private long id, due, remind, list, repeat, nextRemind;
    private String name, details;

    // constructors
    public Task(long id, String name, String details, long due, long remind, long repeat, long nextRemind, long list) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.due = due;
        this.remind = remind;
        this.repeat = repeat;
        this.nextRemind = nextRemind;
        this.list = list;
    }

    public Task(String name, String details, long due, long remind, long repeat, long nextRemind, long list) {
        this.name = name;
        this.details = details;
        this.due = due;
        this.remind = remind;
        this.repeat = repeat;
        this.nextRemind = nextRemind;
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

    long getRepeat() {
        return this.repeat;
    }

    long getRemind() {
        return this.remind;
    }

    long getNextRemind() {
        return this.nextRemind;
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

    void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    void setNextRemind(long nextRemind) {
        this.nextRemind = nextRemind;
    }

    public void setList(long list) {
        this.list = list;
    }
}
