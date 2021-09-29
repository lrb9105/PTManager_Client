package com.teamnova.ptmanager.model.changehistory.inbody;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class InBodyForGraph implements Serializable {
    @SerializedName("inBodyIdList")
    private ArrayList<String> inBodyIdList;
    @SerializedName("inBodyDateList")
    private ArrayList<String> inBodyDateList;
    @SerializedName("inBodyDateTimeList")
    private ArrayList<String> inBodyDateTimeList;
    @SerializedName("weightList")
    private ArrayList<Float> weightList;
    @SerializedName("heightList")
    private ArrayList<Float> heightList;
    @SerializedName("bodyFatList")
    private ArrayList<Float> bodyFatList;
    @SerializedName("muscleMassList")
    private ArrayList<Float> muscleMassList;
    @SerializedName("bmiList")
    private ArrayList<Float> bmiList;
    @SerializedName("bodyFatPercentList")
    private ArrayList<Float> bodyFatPercentList;
    @SerializedName("basalMetabolicRateList")
    private ArrayList<Float> basalMetabolicRateList;
    @SerializedName("fatLevelList")
    private ArrayList<Integer> fatLevelList;

    public InBodyForGraph(ArrayList<String> inBodyIdList, ArrayList<String> inBodyDateList, ArrayList<String> inBodyDateTimeList, ArrayList<Float> weightList, ArrayList<Float> heightList, ArrayList<Float> bodyFatList, ArrayList<Float> muscleMassList, ArrayList<Float> bmiList, ArrayList<Float> bodyFatPercentList, ArrayList<Float> basalMetabolicRateList, ArrayList<Integer> fatLevelList) {
        this.inBodyIdList = inBodyIdList;
        this.inBodyDateList = inBodyDateList;
        this.inBodyDateTimeList = inBodyDateTimeList;
        this.weightList = weightList;
        this.heightList = heightList;
        this.bodyFatList = bodyFatList;
        this.muscleMassList = muscleMassList;
        this.bmiList = bmiList;
        this.bodyFatPercentList = bodyFatPercentList;
        this.basalMetabolicRateList = basalMetabolicRateList;
        this.fatLevelList = fatLevelList;
    }

    public ArrayList<String> getInBodyIdList() {
        return inBodyIdList;
    }

    public void setInBodyIdList(ArrayList<String> inBodyIdList) {
        this.inBodyIdList = inBodyIdList;
    }

    public ArrayList<String> getInBodyDateList() {
        return inBodyDateList;
    }

    public void setInBodyDateList(ArrayList<String> inBodyDateList) {
        this.inBodyDateList = inBodyDateList;
    }

    public ArrayList<String> getInBodyDateTimeList() {
        return inBodyDateTimeList;
    }

    public void setInBodyDateTimeList(ArrayList<String> inBodyDateTimeList) {
        this.inBodyDateTimeList = inBodyDateTimeList;
    }

    public ArrayList<Float> getWeightList() {
        return weightList;
    }

    public void setWeightList(ArrayList<Float> weightList) {
        this.weightList = weightList;
    }

    public ArrayList<Float> getHeightList() {
        return heightList;
    }

    public void setHeightList(ArrayList<Float> heightList) {
        this.heightList = heightList;
    }

    public ArrayList<Float> getBodyFatList() {
        return bodyFatList;
    }

    public void setBodyFatList(ArrayList<Float> bodyFatList) {
        this.bodyFatList = bodyFatList;
    }

    public ArrayList<Float> getMuscleMassList() {
        return muscleMassList;
    }

    public void setMuscleMassList(ArrayList<Float> muscleMassList) {
        this.muscleMassList = muscleMassList;
    }

    public ArrayList<Float> getBmiList() {
        return bmiList;
    }

    public void setBmiList(ArrayList<Float> bmiList) {
        this.bmiList = bmiList;
    }

    public ArrayList<Float> getBodyFatPercentList() {
        return bodyFatPercentList;
    }

    public void setBodyFatPercentList(ArrayList<Float> bodyFatPercentList) {
        this.bodyFatPercentList = bodyFatPercentList;
    }

    public ArrayList<Float> getBasalMetabolicRateList() {
        return basalMetabolicRateList;
    }

    public void setBasalMetabolicRateList(ArrayList<Float> basalMetabolicRateList) {
        this.basalMetabolicRateList = basalMetabolicRateList;
    }

    public ArrayList<Integer> getFatLevelList() {
        return fatLevelList;
    }

    public void setFatLevelList(ArrayList<Integer> fatLevelList) {
        this.fatLevelList = fatLevelList;
    }
}
