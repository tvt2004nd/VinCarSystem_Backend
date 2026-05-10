package com.vin.VinSystem.Car.DTO;

import java.math.BigDecimal;

public class CarPowertrainResponseDTO {

    private Long carId;

    private String driveType;

    private Integer maxPowerHp;

    private Integer maxTorqueNm;

    private BigDecimal acceleration0100Sec;

    private Integer topSpeedKmh;

    public CarPowertrainResponseDTO() {
    }

    public CarPowertrainResponseDTO(Long carId, String driveType, Integer maxPowerHp,
                                   Integer maxTorqueNm, BigDecimal acceleration0100Sec,
                                   Integer topSpeedKmh) {
        this.carId = carId;
        this.driveType = driveType;
        this.maxPowerHp = maxPowerHp;
        this.maxTorqueNm = maxTorqueNm;
        this.acceleration0100Sec = acceleration0100Sec;
        this.topSpeedKmh = topSpeedKmh;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getDriveType() {
        return driveType;
    }

    public void setDriveType(String driveType) {
        this.driveType = driveType;
    }

    public Integer getMaxPowerHp() {
        return maxPowerHp;
    }

    public void setMaxPowerHp(Integer maxPowerHp) {
        this.maxPowerHp = maxPowerHp;
    }

    public Integer getMaxTorqueNm() {
        return maxTorqueNm;
    }

    public void setMaxTorqueNm(Integer maxTorqueNm) {
        this.maxTorqueNm = maxTorqueNm;
    }

    public BigDecimal getAcceleration0100Sec() {
        return acceleration0100Sec;
    }

    public void setAcceleration0100Sec(BigDecimal acceleration0100Sec) {
        this.acceleration0100Sec = acceleration0100Sec;
    }

    public Integer getTopSpeedKmh() {
        return topSpeedKmh;
    }

    public void setTopSpeedKmh(Integer topSpeedKmh) {
        this.topSpeedKmh = topSpeedKmh;
    }
}
