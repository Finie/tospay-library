package net.tospay.auth.remote.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import net.tospay.auth.model.Account;
import net.tospay.auth.model.Wallet;

import java.util.List;

public class AccountResponse {

    @SerializedName("mobile")
    @Expose
    private List<Account> mobile = null;

    @SerializedName("wallet")
    @Expose
    private List<Wallet> wallet = null;

    @SerializedName("bank")
    @Expose
    private List<Account> bank = null;

    @SerializedName("card")
    @Expose
    private List<Account> card = null;

    public List<Account> getMobile() {
        return mobile;
    }

    public void setMobile(List<Account> mobile) {
        this.mobile = mobile;
    }

    public List<Wallet> getWallet() {
        return wallet;
    }

    public void setWallet(List<Wallet> wallet) {
        this.wallet = wallet;
    }

    public List<Account> getBank() {
        return bank;
    }

    public void setBank(List<Account> bank) {
        this.bank = bank;
    }

    public List<Account> getCard() {
        return card;
    }

    public void setCard(List<Account> card) {
        this.card = card;
    }

}
