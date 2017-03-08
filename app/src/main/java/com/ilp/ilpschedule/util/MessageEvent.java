package com.ilp.ilpschedule.util;

import com.ilp.ilpschedule.model.ChatType;
import com.ilp.ilpschedule.model.CseBean;
import com.ilp.ilpschedule.model.WeatherApiBean;

/**
 * Created by 1115394 on 12/9/2016.
 */
public class MessageEvent {

    private String message=null;
    private WeatherApiBean bean;
    private ChatType type;
    private CseBean searchData;

    public MessageEvent(String message, ChatType type) {
        this.message = message;
        this.type = type;
    }

    public MessageEvent(String message, WeatherApiBean bean, ChatType type) {
        this.message = message;
        this.bean = bean;
        this.type = type;
    }

    public MessageEvent(String message, CseBean bean, ChatType type) {
        this.message = message;
        this.searchData = bean;
        this.type = type;
    }


    public WeatherApiBean getBean() {
        return bean;
    }

    public void setBean(WeatherApiBean bean) {
        this.bean = bean;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChatType getType() {
        return type;
    }

    public void setType(ChatType type) {
        this.type = type;
    }

    public CseBean getSearchData() {
        return searchData;
    }

    public void setSearchData(CseBean searchData) {
        this.searchData = searchData;
    }
}
