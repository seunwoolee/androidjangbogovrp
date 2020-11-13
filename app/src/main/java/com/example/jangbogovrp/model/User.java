package com.example.jangbogovrp.model;

import io.realm.RealmObject;

public class User extends RealmObject {
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
