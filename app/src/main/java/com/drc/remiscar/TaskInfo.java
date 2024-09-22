package com.drc.remiscar;

import java.util.ArrayList;

import lombok.Data;

@Data
public class TaskInfo
{
    private Long id;
    private boolean isEnd;
    private String alterNumber;
    private String taskNumber;
    private String emptyReason;
    private String destHospital;
    private String destHospitalId;
    private String carOutTime;
    private String arriveSceneTime;
    private String patOnCarTime;
    private String carBackTime;
    private String carNumber;
    private String carLic;
    private String sceneAddress;
    private java.util.ArrayList<patInfo> patInfos = new ArrayList<patInfo>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public String getAlterNumber() {
        return alterNumber;
    }

    public void setAlterNumber(String alterNumber) {
        this.alterNumber = alterNumber;
    }

    public String getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    public String getEmptyReason() {
        return emptyReason;
    }

    public void setEmptyReason(String emptyReason) {
        this.emptyReason = emptyReason;
    }

    public String getDestHospital() {
        return destHospital;
    }

    public void setDestHospital(String destHospital) {
        this.destHospital = destHospital;
    }

    public String getDestHospitalId() {
        return destHospitalId;
    }

    public void setDestHospitalId(String destHospitalId) {
        this.destHospitalId = destHospitalId;
    }

    public String getCarOutTime() {
        return carOutTime;
    }

    public void setCarOutTime(String carOutTime) {
        this.carOutTime = carOutTime;
    }

    public String getArriveSceneTime() {
        return arriveSceneTime;
    }

    public void setArriveSceneTime(String arriveSceneTime) {
        this.arriveSceneTime = arriveSceneTime;
    }

    public String getPatOnCarTime() {
        return patOnCarTime;
    }

    public void setPatOnCarTime(String patOnCarTime) {
        this.patOnCarTime = patOnCarTime;
    }

    public String getCarBackTime() {
        return carBackTime;
    }

    public void setCarBackTime(String carBackTime) {
        this.carBackTime = carBackTime;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getCarLic() {
        return carLic;
    }

    public void setCarLic(String carLic) {
        this.carLic = carLic;
    }

    public String getSceneAddress() {
        return sceneAddress;
    }

    public void setSceneAddress(String sceneAddress) {
        this.sceneAddress = sceneAddress;
    }

    public ArrayList<patInfo> getPatInfos() {
        return patInfos;
    }

    public void setPatInfos(ArrayList<patInfo> patInfos) {
        this.patInfos = patInfos;
    }
}


