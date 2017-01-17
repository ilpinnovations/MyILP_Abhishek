package com.ilp.ilpschedule.model;

/**
 * Created by 1115394 on 12/19/2016.
 */
public class AssociateGAS {
    private String lg;
    private String accommodation;
    private String lap;

    public AssociateGAS() {
    }

    public AssociateGAS(String lg, String accommodation, String lap) {
        this.lg = lg;
        this.accommodation = accommodation;
        this.lap = lap;
    }

    public String getLg() {
        return lg;
    }

    public void setLg(String lg) {
        this.lg = lg;
    }

    public String getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(String accommodation) {
        this.accommodation = accommodation;
    }

    public String getLap() {
        return lap;
    }

    public void setLap(String lap) {
        this.lap = lap;
    }
}
