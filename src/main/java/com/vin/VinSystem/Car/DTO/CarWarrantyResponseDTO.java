package com.vin.VinSystem.Car.DTO;

public class CarWarrantyResponseDTO {

    private Long carId;

    private Integer warrantyYears;

    private Integer warrantyKm;

    private Integer batteryWarrantyYears;

    private Integer batteryWarrantyKm;

    public CarWarrantyResponseDTO() {
    }

    public CarWarrantyResponseDTO(Long carId, Integer warrantyYears, Integer warrantyKm,
                                 Integer batteryWarrantyYears, Integer batteryWarrantyKm) {
        this.carId = carId;
        this.warrantyYears = warrantyYears;
        this.warrantyKm = warrantyKm;
        this.batteryWarrantyYears = batteryWarrantyYears;
        this.batteryWarrantyKm = batteryWarrantyKm;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
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
