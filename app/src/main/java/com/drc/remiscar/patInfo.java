package com.drc.remiscar;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class patInfo
{
    private String patId;
    private String patNumber;
    private String patName;
    private String patSex;
    private String patAge;
    private String patContact;
    private String patAddress;
    private String patGoWhereInt;
    private String toWhere;
    private String toWhereStr;
    private String patHeal;


    public String getPatId() {
        return patId;
    }

    public void setPatId(String patId) {
        this.patId = patId;
    }

    public String getPatNumber() {
        return patNumber;
    }

    public void setPatNumber(String patNumber) {
        this.patNumber = patNumber;
    }

    public String getPatName() {
        return patName;
    }

    public void setPatName(String patName) {
        this.patName = patName;
    }

    public String getPatSex() {
        return patSex;
    }

    public void setPatSex(String patSex) {
        this.patSex = patSex;
    }

    public String getPatAge() {
        return patAge;
    }

    public void setPatAge(String patAge) {
        this.patAge = patAge;
    }

    public String getPatContact() {
        return patContact;
    }

    public void setPatContact(String patContact) {
        this.patContact = patContact;
    }

    public String getPatAddress() {
        return patAddress;
    }

    public void setPatAddress(String patAddress) {
        this.patAddress = patAddress;
    }

    public String getPatGoWhereInt() {
        return patGoWhereInt;
    }

    public void setPatGoWhereInt(String patGoWhereInt) {
        this.patGoWhereInt = patGoWhereInt;
    }

    public String getToWhere() {
        return toWhere;
    }

    public void setToWhere(String toWhere) {
        this.toWhere = toWhere;
    }

    public String getToWhereStr() {
        return toWhereStr;
    }

    public void setToWhereStr(String toWhereStr) {
        this.toWhereStr = toWhereStr;
    }

    public String getPatHeal() {
        return patHeal;
    }

    public void setPatHeal(String patHeal) {
        this.patHeal = patHeal;
    }
}
