package com.lu.customer;

import java.io.Serializable;

public class Customer implements Serializable {
    private int customer_id;
    private String customer_name;
    private String customer_phone;
    private String customer_email;
    private String customer_number_plate;
    private String customer_car_model;
    private String customer_car_color;
    private String customer_password;



    public Customer(int customer_id, String customer_name, String customer_phone, String customer_email, String customer_number_plate,
                    String customer_car_model, String customer_car_color) {
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.customer_phone = customer_phone;
        this.customer_email = customer_email;
        this.customer_number_plate = customer_number_plate;
        this.customer_car_model = customer_car_model;
        this.customer_car_color = customer_car_color;
    }

    public Customer(String customer_name, String customer_email, String customer_password, String customer_phone,String customer_number_plate, String customer_car_model,String customer_car_color) {
        this.customer_name = customer_name;
        this.customer_phone = customer_phone;
        this.customer_password = customer_password;
        this.customer_email = customer_email;
        this.customer_number_plate = customer_number_plate;
        this.customer_car_model = customer_car_model;
        this.customer_car_color = customer_car_color;
    }

    public Customer updateCustomer(String customer_name, String customer_phone, String customer_email) {
        Customer customer;
        this.customer_name = customer_name;
        this.customer_phone = customer_phone;
        this.customer_email = customer_email;
        customer = new Customer(customer_id, customer_name, customer_phone, customer_email, customer_number_plate, customer_car_model, customer_car_color);
        return customer;
    }

    public Customer updateCar(String customer_number_plate, String customer_car_model, String customer_car_color) {
        Customer customer;
        this.customer_number_plate = customer_number_plate;
        this.customer_car_model = customer_car_model;
        this.customer_car_color = customer_car_color;
        customer = new Customer(customer_id, customer_name, customer_phone, customer_email, customer_number_plate, customer_car_model, customer_car_color);
        return customer;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public String getCustomer_password() {
        return customer_password;
    }

    public void setCustomer_password(String customer_password) {
        this.customer_password = customer_password;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getCustomer_number_plate() {
        return customer_number_plate;
    }

    public void setCustomer_number_plate(String customer_number_plate) {
        this.customer_number_plate = customer_number_plate;
    }

    public String getCustomer_car_model() {
        return customer_car_model;
    }

    public void setCustomer_car_model(String customer_car_model) {
        this.customer_car_model = customer_car_model;
    }

    public String getCustomer_car_color() {
        return customer_car_color;
    }

    public void setCustomer_car_color(String customer_car_color) {
        this.customer_car_color = customer_car_color;
    }


}
