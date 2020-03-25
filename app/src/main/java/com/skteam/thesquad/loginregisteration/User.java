package com.skteam.thesquad.loginregisteration;

public class User {
    private int id;
    private boolean error;
    private String number, email, password , error_msg, referal_code;
    private int wallet;

    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getError_msg() {
        return error_msg;
    }

    public boolean isError() {
        return error;
    }

    public String getReferal_code() {
        return referal_code;
    }

    public int getWallet() {
        return wallet;
    }
}
