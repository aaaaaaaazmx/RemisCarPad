package com.drc.remiscarmini;

public class Car {
    String Key;
    String Value;

    public String getKey() {
        return Key;
    }

    public String getValue() {
        return Value;
    }

    public void setKey(String key) {
        this.Key = key;
    }

    public void setValue(String value) {
        this.Value = value;
    }

    @Override
    public String toString() {
        return Value;
    }
}
