package com.android.agzakhanty.sprints.one.models;

/**
 * Created by Aly on 06/05/2018.
 */

public class Interest {

    private String name;
    private String id;
    private Boolean isChecked = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }
}
