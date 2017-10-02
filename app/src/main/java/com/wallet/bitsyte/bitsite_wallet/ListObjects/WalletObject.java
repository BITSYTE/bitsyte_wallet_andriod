package com.wallet.bitsyte.bitsite_wallet.ListObjects;

/**
 * Created by rafita78 on 29/09/2017.
 */

public class WalletObject {
    String walletName="";
    String walletMoney="";
    String walletImage="";
    public WalletObject(String walletName,String walletMoney,String walletImage){
        this.walletMoney = walletMoney;
        this.walletName = walletName;
        this.walletImage = walletImage;
    }

    public String getWalletMoney() {
        return walletMoney;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletMoney(String walletMoney) {
        this.walletMoney = walletMoney;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getWalletImage() {
        return walletImage;
    }

    public void setWalletImage(String walletImage) {
        this.walletImage = walletImage;
    }
}
