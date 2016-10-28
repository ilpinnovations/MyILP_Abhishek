package com.ilp.ilpschedule.beans;

/**
 * Created by 1007546 on 18-10-2016.
 */
public class ContactsBean {
    private String contactName;
    private String contactNumber;

    public ContactsBean() {
    }

    public ContactsBean(String contactName, String contactNumber) {
        this.contactName = contactName;
        this.contactNumber = contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
