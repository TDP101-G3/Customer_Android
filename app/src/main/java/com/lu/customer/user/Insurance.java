package com.lu.customer.user;

import java.io.Serializable;

public class Insurance implements Serializable {
    int insuranceId;
    String insuranceName;
    String expireDate;
    String situation;

    public Insurance(int insuranceId, String insuranceName, String expireDate, String situation) {
        this.insuranceId = insuranceId;
        this.insuranceName = insuranceName;
        this.expireDate = expireDate;
        this.situation = situation;
    }

    public Insurance updateInsurance(String expireDate) {
        Insurance insurance;
        this.expireDate = expireDate;
        insurance = new Insurance(insuranceId, insuranceName, expireDate, "unfinished");
        return insurance;
    }

    public int getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(int insuranceId) {
        this.insuranceId = insuranceId;
    }

    public String getInsuranceName() {
        return insuranceName;
    }

    public void setInsuranceName(String insuranceName) {
        this.insuranceName = insuranceName;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }
}
