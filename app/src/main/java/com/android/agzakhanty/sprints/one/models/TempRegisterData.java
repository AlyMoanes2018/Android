package com.android.agzakhanty.sprints.one.models;

/**
 * Created by Aly on 01/12/2018.
 */

public class TempRegisterData {

    private String name;
    private String email;
    private String password;
    private String mobile;
    private String brthdate;
    private int sex;

    public TempRegisterData(String name, String email, String password, String mobile, String brthdate, int sex) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.mobile = mobile;
        this.brthdate = brthdate;
        this.sex = sex;
    }
}
