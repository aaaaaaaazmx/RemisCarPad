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

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getEnumName() {
        return enumName;
    }

    public void setEnumName(String enumName) {
        this.enumName = enumName;
    }
}
