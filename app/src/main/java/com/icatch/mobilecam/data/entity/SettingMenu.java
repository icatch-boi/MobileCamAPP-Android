package com.icatch.mobilecam.data.entity;

public class SettingMenu {
    public int name;
    public String value;

    public SettingMenu(int name, String value) {
        this.name = name;
        this.value = value;
    }

    public int getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
