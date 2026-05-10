package com.vin.VinSystem.Car.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "car_safety")
public class CarSafety {

    @Id
    @Column(name = "car_id")
    private Long carId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "car_id")
    private Car car;

    @Column(name = "airbags")
    private Boolean airbags;

    @Column(name = "abs")
    private Boolean abs;

    @Column(name = "esc")
    private Boolean esc;

    @Column(name = "traction_control")
    private Boolean tractionControl;

    @Column(name = "lane_keep_assist")
    private Boolean laneKeepAssist;

    @Column(name = "adaptive_cruise_control")
    private Boolean adaptiveCruiseControl;

    @Column(name = "rear_camera")
    private Boolean rearCamera;

    @Column(name = "parking_sensors")
    private Boolean parkingSensors;

    // Constructor rỗng (bắt buộc cho JPA)
    public CarSafety() {
    }

    // Constructor đầy đủ
    public CarSafety(Long carId,
                     Car car,
                     Boolean airbags,
                     Boolean abs,
                     Boolean esc,
                     Boolean tractionControl,
                     Boolean laneKeepAssist,
                     Boolean adaptiveCruiseControl,
                     Boolean rearCamera,
                     Boolean parkingSensors) {
        this.carId = carId;
        this.car = car;
        this.airbags = airbags;
        this.abs = abs;
        this.esc = esc;
        this.tractionControl = tractionControl;
        this.laneKeepAssist = laneKeepAssist;
        this.adaptiveCruiseControl = adaptiveCruiseControl;
        this.rearCamera = rearCamera;
        this.parkingSensors = parkingSensors;
    }

    // Getter & Setter

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
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