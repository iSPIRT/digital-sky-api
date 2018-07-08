package com.ispirit.digitalsky.domain;

import org.springframework.data.annotation.Id;

public class DroneCategory {

    @Id
    private String id;
    private String payload;
    private float size;
    private float weight;


    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

}
