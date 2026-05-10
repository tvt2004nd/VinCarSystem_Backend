package com.vin.VinSystem.Car.DTO;

import jakarta.validation.constraints.Min;

public class CarWarrantyCreateDTO {

    @Min(value = 0, message = "Năm bảo hành phải lớn hơn hoặc bằng 0")
    private Integer warrantyYears;

    @Min(value = 0, message = "Km bảo hành phải lớn hơn hoặc bằng 0")
    private Integer warrantyKm;

    @Min(value = 0, message = "Năm bảo hành pin phải lớn hơn hoặc bằng 0")
    private Integer batteryWarrantyYears;

    @Min(value = 0, message = "Km bảo hành pin phải lớn hơn hoặc bằng 0")
    private Integer batteryWarrantyKm;

    public CarWarrantyCreateDTO() {
    }

    public CarWarrantyCreateDTO(Integer warrantyYears, Integer warrantyKm,
                               Integer batteryWarrantyYears, Integer batteryWarrantyKm) {
        this.warrantyYears = warrantyYears;
        this.warrantyKm = warrantyKm;
        this.batteryWarrantyYears = batteryWarrantyYears;
        this.batteryWarrantyKm = batteryWarrantyKm;
    }

    public Integer getWarrantyYears() {
        return warrantyYears;
    }

    public void setWarrantyYears(Integer warrantyYears) {
        this.warrantyYears = warrantyYears;
    }

    public Integer getWarrantyKm() {
        return warrantyKm;
    }

    public void setWarrantyKm(Integer warrantyKm) {
        this.warrantyKm = warrantyKm;
    }

    public Integer getBatteryWarrantyYears() {
        return batteryWarrantyYears;
    }

    public void setBatteryWarrantyYears(Integer batteryWarrantyYears) {
        this.batteryWarrantyYears = batteryWarrantyYears;
    }

    public Integer getBatteryWarrantyKm() {
        return batteryWarrantyKm;
    }

    public void setBatteryWarrantyKm(Integer batteryWarrantyKm) {
        this.batteryWarrantyKm = batteryWarrantyKm;
    }
}
