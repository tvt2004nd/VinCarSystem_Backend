package com.vin.VinSystem.Car.DTO;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;

public class CarDimensionsCreateDTO {

    @Min(value = 0, message = "Chiều dài phải lớn hơn hoặc bằng 0")
    private Integer lengthMm;

    @Min(value = 0, message = "Chiều rộng phải lớn hơn hoặc bằng 0")
    private Integer widthMm;

    @Min(value = 0, message = "Chiều cao phải lớn hơn hoặc bằng 0")
    private Integer heightMm;

    @Min(value = 0, message = "Chiều dài cơ sở phải lớn hơn hoặc bằng 0")
    private Integer wheelbaseMm;

    private BigDecimal curbWeightKg;

    @Min(value = 0, message = "Thể tích cốp phải lớn hơn hoặc bằng 0")
    private Integer trunkVolumeL;

    public CarDimensionsCreateDTO() {
    }

    public CarDimensionsCreateDTO(Integer lengthMm, Integer widthMm, Integer heightMm,
                                  Integer wheelbaseMm, BigDecimal curbWeightKg,
                                  Integer trunkVolumeL) {
        this.lengthMm = lengthMm;
        this.widthMm = widthMm;
        this.heightMm = heightMm;
        this.wheelbaseMm = wheelbaseMm;
        this.curbWeightKg = curbWeightKg;
        this.trunkVolumeL = trunkVolumeL;
    }

    public Integer getLengthMm() {
        return lengthMm;
    }

    public void setLengthMm(Integer lengthMm) {
        this.lengthMm = lengthMm;
    }

    public Integer getWidthMm() {
        return widthMm;
    }

    public void setWidthMm(Integer widthMm) {
        this.widthMm = widthMm;
    }

    public Integer getHeightMm() {
        return heightMm;
    }

    public void setHeightMm(Integer heightMm) {
        this.heightMm = heightMm;
    }

    public Integer getWheelbaseMm() {
        return wheelbaseMm;
    }

    public void setWheelbaseMm(Integer wheelbaseMm) {
        this.wheelbaseMm = wheelbaseMm;
    }

    public BigDecimal getCurbWeightKg() {
        return curbWeightKg;
    }

    public void setCurbWeightKg(BigDecimal curbWeightKg) {
        this.curbWeightKg = curbWeightKg;
    }

    public Integer getTrunkVolumeL() {
        return trunkVolumeL;
    }

    public void setTrunkVolumeL(Integer trunkVolumeL) {
        this.trunkVolumeL = trunkVolumeL;
    }
}