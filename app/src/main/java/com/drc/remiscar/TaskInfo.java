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

}


