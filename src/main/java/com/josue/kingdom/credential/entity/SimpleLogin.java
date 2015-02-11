/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.credential.entity;

/**
 *
 * @author Josue
 */
public class SimpleLogin {

    public static enum LoginType {

        BASIC
    }

    private LoginType type;
    private String data;

    public LoginType getType() {
        return type;
    }

    public void setType(LoginType type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String value) {
        this.data = value;
    }

}
