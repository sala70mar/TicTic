package com.qboxus.tictic.activitesfragments.walletandwithdraw;

import java.io.Serializable;

public class WalletModel implements Serializable {

    String id ;
    String image ;
    String coins ;
    String price ;

    public WalletModel(String id,String image, String coins, String price) {
        this.id = id;
        this.image = image;
        this.coins = coins;
        this.price = price;
    }

    public WalletModel() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
