package com.vin.VinSystem.Car.DTO;

import java.math.BigDecimal;

public class CarEvSpecsResponseDTO {

    private Long carId;

    private BigDecimal batteryCapacityKwh;

    private Integer rangeKm;

    private BigDecimal acChargingKw;

    private BigDecimal dcFastChargingKw;

    private Integer fastChargeTimeMin;

    public CarEvSpecsResponseDTO() {
    }

    public CarEvSpecsResponseDTO(Long carId, BigDecimal batteryCapacityKwh,
                                Integer rangeKm, BigDecimal acChargingKw,
                                BigDecimal dcFastChargingKw, Integer fastChargeTimeMin) {
        this.carId = carId;
        this.batteryCapacityKwh = batteryCapacityKwh;
        this.rangeKm = rangeKm;
        this.acChargingKw = acChargingKw;
        this.dcFastChargingKw = dcFastChargingKw;
        this.fastChargeTimeMin = fastChargeTimeMin;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public BigDecimal getBatteryCapacityKwh() {
        return batteryCapacityKwh;
    }

    public void setBatteryCapacityKwh(BigDecimal batteryCapacityKwh) {
        this.batteryCapacityKwh = batteryCapacityKwh;
    }

    public Integer getRangeKm() {
        return rangeKm;
    }

    public void setRangeKm(Integer rangeKm) {
        this.rangeKm = rangeKm;
    }

    public BigDecimal getAcChargingKw() {
        return acChargingKw;
    }

    public void setAcChargingKw(BigDecimal acChargingKw) {
        this.acChargingKw = acChargingKw;
    }

    public BigDecimal getDcFastChargingKw() {
        return dcFastChargingKw;
    }

    public void setDcFastChargingKw(BigDecimal dcFastChargingKw) {
        this.dcFastChargingKw = dcFastChargingKw;
    }

    public Integer getFastChargeTimeMin() {
        return fastChargeTimeMin;
    }

    public void setFastChargeTimeMin(Integer fastChargeTimeMin) {
        this.fastChargeTimeMin = fastChargeTimeMin;
    }
}
