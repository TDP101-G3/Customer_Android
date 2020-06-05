package com.lu.customer;

import java.io.Serializable;

public class Opinion implements Serializable {
    private int customer_opinion_id;
    private int customer_id;
    private String customer_opinion_question;
    private String customer_opinion_answer;

    public Opinion(int customer_opinion_id, int customer_id, String customer_opinion_question, String customer_opinion_answer) {
        this.customer_opinion_id = customer_opinion_id;
        this.customer_id = customer_id;
        this.customer_opinion_question = customer_opinion_question;
        this.customer_opinion_answer = customer_opinion_answer;
    }

    public Opinion(int customer_id, String driver_opinion_question) {
        this.customer_id = customer_id;
        this.customer_opinion_question = driver_opinion_question;
    }

    public Opinion(String customer_opinion_question, String customer_opinion_answer) {
        this.customer_opinion_question = customer_opinion_question;
        this.customer_opinion_answer = customer_opinion_answer;
    }

    public int getCustomer_opinion_id() {
        return customer_opinion_id;
    }

    public void setCustomer_opinion_id(int customer_opinion_id) {
        this.customer_opinion_id = customer_opinion_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_opinion_question() {
        return customer_opinion_question;
    }

    public void setCustomer_opinion_question(String customer_opinion_question) {
        this.customer_opinion_question = customer_opinion_question;
    }

    public String getCustomer_opinion_answer() {
        return customer_opinion_answer;
    }

    public void setCustomer_opinion_answer(String customer_opinion_answer) {
        this.customer_opinion_answer = customer_opinion_answer;
    }

}
