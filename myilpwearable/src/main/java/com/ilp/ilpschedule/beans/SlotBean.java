package com.ilp.ilpschedule.beans;

/**
 * Created by 1007546 on 18-10-2016.
 */
public class SlotBean {
    private String title;
    private String location;
    private String slot;
    private String batch;

    public SlotBean() {
    }

    public SlotBean(String title, String location, String slot) {
        this.title = title;
        this.location = location;
        this.slot = slot;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }
}
