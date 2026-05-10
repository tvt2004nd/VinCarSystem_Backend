package com.vin.VinSystem.Car.DTO;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class CarPowertrainCreateDTO {

    @Size(max = 50, message = "Loại lái tối đa 50 ký tự")
    private String driveType;

    @Min(value = 0, message = "Công suất tối đa phải lớn hơn hoặc bằng 0")
    private Integer maxPowerHp;

    @Min(value = 0, message = "Moment xoắn tối đa phải lớn hơn hoặc bằng 0")
    private Integer maxTorqueNm;

    private BigDecimal acceleration0100Sec;

    @Min(value = 0, message = "Tốc độ tối đa phải lớn hơn hoặc bằng 0")
    private Integer topSpeedKmh;

    public CarPowertrainCreateDTO() {
    }

    public CarPowertrainCreateDTO(String driveType, Integer maxPowerHp,
                                 Integer maxTorqueNm, BigDecimal acceleration0100Sec,
                                 Integer topSpeedKmh) {
        this.driveType = driveType;
        this.maxPowerHp = maxPowerHp;
        this.maxTorqueNm = maxTorqueNm;
        this.acceleration0100Sec = acceleration0100Sec;
        this.topSpeedKmh = topSpeedKmh;
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
