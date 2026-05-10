package com.vin.VinSystem.Car.Entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "car_ev_specs")
public class CarEvSpecs {

    @Id
    @Column(name = "car_id")
    private Long carId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "car_id")
    private Car car;

    @Column(name = "battery_capacity_kwh", precision = 10, scale = 2)
    private BigDecimal batteryCapacityKwh;

    @Column(name = "range_km")
    private Integer rangeKm;

    @Column(name = "ac_charging_kw", precision = 10, scale = 2)
    private BigDecimal acChargingKw;

    @Column(name = "dc_fast_charging_kw", precision = 10, scale = 2)
    private BigDecimal dcFastChargingKw;

    @Column(name = "fast_charge_time_min")
    private Integer fastChargeTimeMin;

    // Constructor rỗng (bắt buộc cho JPA)
    public CarEvSpecs() {
    }

    // Constructor đầy đủ
    public CarEvSpecs(Long carId, Car car,
                      BigDecimal batteryCapacityKwh,
                      Integer rangeKm,
                      BigDecimal acChargingKw,
                      BigDecimal dcFastChargingKw,
                      Integer fastChargeTimeMin) {
        this.carId = carId;
        this.car = car;
        this.batteryCapacityKwh = batteryCapacityKwh;
        this.rangeKm = rangeKm;
        this.acChargingKw = acChargingKw;
        this.dcFastChargingKw = dcFastChargingKw;
        this.fastChargeTimeMin = fastChargeTimeMin;
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