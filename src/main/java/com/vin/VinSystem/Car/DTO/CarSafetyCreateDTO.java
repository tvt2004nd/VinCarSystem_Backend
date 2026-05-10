package com.vin.VinSystem.Car.DTO;

public class CarSafetyCreateDTO {

    private Boolean airbags;

    private Boolean abs;

    private Boolean esc;

    private Boolean tractionControl;

    private Boolean laneKeepAssist;

    private Boolean adaptiveCruiseControl;

    private Boolean rearCamera;

    private Boolean parkingSensors;

    public CarSafetyCreateDTO() {
    }

    public CarSafetyCreateDTO(Boolean airbags, Boolean abs, Boolean esc,
                             Boolean tractionControl, Boolean laneKeepAssist,
                             Boolean adaptiveCruiseControl, Boolean rearCamera,
                             Boolean parkingSensors) {
        this.airbags = airbags;
        this.abs = abs;
        this.esc = esc;
        this.tractionControl = tractionControl;
        this.laneKeepAssist = laneKeepAssist;
        this.adaptiveCruiseControl = adaptiveCruiseControl;
        this.rearCamera = rearCamera;
        this.parkingSensors = parkingSensors;
    }

    public Boolean getAirbags() {
        return airbags;
    }

    public void setAirbags(Boolean airbags) {
        this.airbags = airbags;
    }

    public Boolean getAbs() {
        return abs;
    }

    public void setAbs(Boolean abs) {
        this.abs = abs;
    }

    public Boolean getEsc() {
        return esc;
    }

    public void setEsc(Boolean esc) {
        this.esc = esc;
    }

    public Boolean getTractionControl() {
        return tractionControl;
    }

    public void setTractionControl(Boolean tractionControl) {
        this.tractionControl = tractionControl;
    }

    public Boolean getLaneKeepAssist() {
        return laneKeepAssist;
    }

    public void setLaneKeepAssist(Boolean laneKeepAssist) {
        this.laneKeepAssist = laneKeepAssist;
    }

    public Boolean getAdaptiveCruiseControl() {
        return adaptiveCruiseControl;
    }

    public void setAdaptiveCruiseControl(Boolean adaptiveCruiseControl) {
        this.adaptiveCruiseControl = adaptiveCruiseControl;
    }

    public Boolean getRearCamera() {
        return rearCamera;
    }

    public void setRearCamera(Boolean rearCamera) {
        this.rearCamera = rearCamera;
    }

    public Boolean getParkingSensors() {
        return parkingSensors;
    }

    public void setParkingSensors(Boolean parkingSensors) {
        this.parkingSensors = parkingSensors;
    }
}
