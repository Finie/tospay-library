package net.tospay.auth.ui.account.bank.model;

import java.io.Serializable;

public class VerifyD implements Serializable {

    String Phone,code;

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
