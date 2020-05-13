package com.lu.customer.signup_and_login;

import java.io.Serializable;

public class Creditcard implements Serializable {
    private String customer_creditcard_type;
    private String customer_creditcard_number;
    private String customer_creditcard_date;
    private String customer_creditcard_secure_code;

    public Creditcard(String customer_creditcard_type, String customer_creditcard_number, String customer_creditcard_date, String customer_creditcard_secure_code) {
        this.customer_creditcard_type = customer_creditcard_type;
        this.customer_creditcard_number = customer_creditcard_number;
        this.customer_creditcard_date = customer_creditcard_date;
        this.customer_creditcard_secure_code = customer_creditcard_secure_code;
    }

    public String getCustomer_creditcard_type() {
        return customer_creditcard_type;
    }

    public void setCustomer_creditcard_type(String customer_creditcard_type) {
        this.customer_creditcard_type = customer_creditcard_type;
    }

    public String getCustomer_creditcard_number() {
        return customer_creditcard_number;
    }

    public void setCustomer_creditcard_number(String customer_creditcard_number) {
        this.customer_creditcard_number = customer_creditcard_number;
    }

    public String getCustomer_creditcard_date() {
        return customer_creditcard_date;
    }

    public void setCustomer_creditcard_date(String customer_creditcard_date) {
        this.customer_creditcard_date = customer_creditcard_date;
    }

    public String getCustomer_creditcard_secure_code() {
        return customer_creditcard_secure_code;
    }

    public void setCustomer_creditcard_secure_code(String customer_creditcard_secure_code) {
        this.customer_creditcard_secure_code = customer_creditcard_secure_code;
    }
}
