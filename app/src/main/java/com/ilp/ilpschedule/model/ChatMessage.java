package com.ilp.ilpschedule.model;

/**
 * Created by madhur on 17/01/15.
 */
public class ChatMessage {

    private String messageText;
    private ChatType userType;
    private Status messageStatus;
    private long messageTime;
    private WeatherApiBean bean;
    private CseBean cseBean;

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setUserType(ChatType userType) {
        this.userType = userType;
    }

    public void setMessageStatus(Status messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getMessageText() {

        return messageText;
    }

    public ChatType getUserType() {
        return userType;
    }

    public Status getMessageStatus() {
        return messageStatus;
    }

    public WeatherApiBean getBean() {
        return bean;
    }

    public void setBean(WeatherApiBean bean) {
        this.bean = bean;
    }

    public CseBean getCseBean() {
        return cseBean;
    }

    public void setCseBean(CseBean cseBean) {
        this.cseBean = cseBean;
    }
}
