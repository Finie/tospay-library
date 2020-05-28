package net.tospay.auth.model;

public class MarketPlace {

    private String logo;
    private String name;
    private String accountNo;

    public MarketPlace(String logo, String name, String accountNo) {
        this.logo = logo;
        this.name = name;
        this.accountNo = accountNo;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
}
