package com.vin.VinSystem.Car.DTO;

import java.math.BigDecimal;

public class CarDimensionsResponseDTO {
    private Long carId;
    private Integer lengthMm;
    private Integer widthMm;
    private Integer heightMm;
    private Integer wheelbaseMm;
    private BigDecimal curbWeightKg;
    private Integer trunkVolumeL;

    public CarDimensionsResponseDTO() {
    }

    public CarDimensionsResponseDTO(Long carId, Integer lengthMm, Integer widthMm, Integer heightMm,
                                    Integer wheelbaseMm, BigDecimal curbWeightKg, Integer trunkVolumeL) {
        this.carId = carId;
        this.lengthMm = lengthMm;
        this.widthMm = widthMm;
        this.heightMm = heightMm;
        this.wheelbaseMm = wheelbaseMm;
        this.curbWeightKg = curbWeightKg;
        this.trunkVolumeL = trunkVolumeL;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
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