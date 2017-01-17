package com.ilp.ilpschedule.model;

import java.util.ArrayList;

/**
 * Created by 1115394 on 12/19/2016.
 */
public class TimingsGAS {
    private ArrayList<String> timings;

    public TimingsGAS() {
    }

    public TimingsGAS(ArrayList<String> timings) {
        this.timings = timings;
    }

    public ArrayList<String> getTimings() {
        return timings;
    }

    public void setTimings(ArrayList<String> timings) {
        this.timings = timings;
    }
}
