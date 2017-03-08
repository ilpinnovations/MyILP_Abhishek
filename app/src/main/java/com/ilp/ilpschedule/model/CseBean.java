package com.ilp.ilpschedule.model;

/**
 * Created by 1115394 on 3/1/2017.
 */
public class CseBean {
    private String heading;
    private String link;
    private String summary;
    private String imagePath;

    public CseBean(String heading, String link, String summary, String imagePath) {
        this.heading = heading;
        this.link = link;
        this.summary = summary;
        this.imagePath = imagePath;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
