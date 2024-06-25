package com.drc.remiscar;

import lombok.Data;

@Data
public class SysEnum {
    String Id;
    String enumName;

    @Override
    public String toString() {
        return enumName;
    }
}
