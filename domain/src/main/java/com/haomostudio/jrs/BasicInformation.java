package com.haomostudio.jrs;

import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.log.SysoCounter;
import org.apache.lucene.index.Fields;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by yide on 17/6/6.
 */
public class BasicInformation {
    private String height;
    private String weight;
    private String bmi;
    private String waist;
    private String hip;
    private String waistHip;
    private String bpmin;
    private String bpmax;
    private String bloodType;
    private String bloodOther;
    private String isAllergy;
    private String allergyName;
    private String isDisease;
    private String culturalLevel;
    private String maritalStatus;

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
    }

    public void setWaist(String waist) {
        this.waist = waist;
    }

    public void setHip(String hip) {
        this.hip = hip;
    }

    public void setWaistHip(String waistHip) {
        this.waistHip = waistHip;
    }

    public void setBpmin(String bpmin) {
        this.bpmin = bpmin;
    }

    public void setBpmax(String bpmax) {
        this.bpmax = bpmax;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public void setBloodOther(String bloodOther) {
        this.bloodOther = bloodOther;
    }

    public void setIsAllergy(String isAllergy) {
        this.isAllergy = isAllergy;
    }

    public void setAllergyName(String allergyName) {
        this.allergyName = allergyName;
    }

    public void setIsDisease(String isDisease) {
        this.isDisease = isDisease;
    }

    public void setCulturalLevel(String culturalLevel) {
        this.culturalLevel = culturalLevel;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getHeight() {
        return height;
    }

    public String getWeight() {
        return weight;
    }

    public String getBmi() {
        return bmi;
    }

    public String getWaist() {
        return waist;
    }

    public String getHip() {
        return hip;
    }

    public String getWaistHip() {
        return waistHip;
    }

    public String getBpmin() {
        return bpmin;
    }

    public String getBpmax() {
        return bpmax;
    }

    public String getBloodType() {
        return bloodType;
    }

    public String getBloodOther() {
        return bloodOther;
    }

    public String getIsAllergy() {
        return isAllergy;
    }

    public String getAllergyName() {
        return allergyName;
    }

    public String getIsDisease() {
        return isDisease;
    }

    public String getCulturalLevel() {
        return culturalLevel;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }



}
