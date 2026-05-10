package com.vin.VinSystem.Car.DTO;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;

public class CarEvSpecsCreateDTO {

    @Min(value = 0, message = "Dung lượng pin phải lớn hơn 0")
    private BigDecimal batteryCapacityKwh;

    @Min(value = 0, message = "Phạm vi phải lớn hơn 0")
    private Integer rangeKm;

    @Min(value = 0, message = "Công suất sạc AC phải lớn hơn 0")
    private BigDecimal acChargingKw;

    @Min(value = 0, message = "Công suất sạc DC phải lớn hơn 0")
    private BigDecimal dcFastChargingKw;

    @Min(value = 0, message = "Thời gian sạc nhanh phải lớn hơn 0")
    private Integer fastChargeTimeMin;

    public CarEvSpecsCreateDTO() {
    }

    public CarEvSpecsCreateDTO(BigDecimal batteryCapacityKwh, Integer rangeKm,
                              BigDecimal acChargingKw, BigDecimal dcFastChargingKw,
                              Integer fastChargeTimeMin) {
        this.batteryCapacityKwh = batteryCapacityKwh;
        this.rangeKm = rangeKm;
        this.acChargingKw = acChargingKw;
        this.dcFastChargingKw = dcFastChargingKw;
        this.fastChargeTimeMin = fastChargeTimeMin;
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
