package com.teamnova.ptmanager.test;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class TestDTO {
    @SerializedName("id")
    private String id;

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    public String getId() {
        return id;
    }
}
