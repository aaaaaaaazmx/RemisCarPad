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

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }
}
