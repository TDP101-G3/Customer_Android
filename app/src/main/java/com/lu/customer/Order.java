package com.lu.customer;

import java.io.Serializable;

public class Order implements Serializable {
    private int order_id;
    private int customer_id;
    private int driver_id;
    private String order_start;
    private String order_end;
    private double driver_score;
    private double customer_score;
    private double order_money;
    private String order_time;



    public Order(int order_id, double driver_score) {
        this.order_id = order_id;
        this.driver_score = driver_score;
    }

    public Order(int customer_id, double customer_score, double driver_score) {
        this.customer_id = customer_id;
        this.customer_score = customer_score;
        this.driver_score = driver_score;
    }

    public Order(int customer_id, int driver_id, String order_start, String order_end){
        this.customer_id = customer_id;
        this.driver_id = driver_id;
        this.order_start = order_start;
        this.order_end = order_end;
    }

    public Order(int order_id, int customer_id, int driver_id, String order_start, String order_end, double driver_score, double customer_score, double order_money, String order_time) {
        this.order_id = order_id;
        this.customer_id = customer_id;
        this.driver_id = driver_id;
        this.order_start = order_start;
        this.order_end = order_end;
        this.driver_score = driver_score;
        this.customer_score = customer_score;
        this.order_money = order_money;
        this.order_time = order_time;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public double getOrder_money() {
        return order_money;
    }

    public void setOrder_money(double order_money) {
        this.order_money = order_money;
    }


    public int getOrder_id() {
        return order_id;
    }
    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }
    public int getCustomer_id() {
        return customer_id;
    }
    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }
    public int getDriver_id() {
        return driver_id;
    }
    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }

    public String getOrder_start() {
        return order_start;
    }

    public void setOrder_start(String order_start) {
        this.order_start = order_start;
    }

    public String getOrder_end() {
        return order_end;
    }

    public void setOrder_end(String order_end) {
        this.order_end = order_end;
    }

    public double getCustomer_score() {
        return customer_score;
    }
    public void setCustomer_score(double customer_score) {
        this.customer_score = customer_score;
    }
    public double getDriver_score() {
        return driver_score;
    }
    public void setDriver_score(double driver_score) {
        this.driver_score = driver_score;
    }
}
