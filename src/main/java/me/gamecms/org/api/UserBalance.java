package me.gamecms.org.api;

public class UserBalance {

    String paid;
    String virtual;
    String total;

    public UserBalance(String paid, String virtual, String total){
        this.paid = paid;
        this.virtual = virtual;
        this.total = total;
    }

    public String getPaid(){
        return this.paid;
    }

    public String getVirtual(){
        return this.virtual;
    }
    public String getTotal(){
        return this.total;
    }
}
