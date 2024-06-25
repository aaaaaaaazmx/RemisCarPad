package com.drc.remiscar;

import lombok.Data;

@Data
public class Hospital {
    String hospitalId;
    String hospitalName;
    @Override
    public String toString() {

        return hospitalName;
    }
}
