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
@Table(name = "car_powertrain")
public class CarPowertrain {

    @Id
    @Column(name = "car_id")
    private Long carId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "car_id")
    private Car car;

    @Column(name = "drive_type", length = 50)
    private String driveType;

    @Column(name = "max_power_hp")
    private Integer maxPowerHp;

    @Column(name = "max_torque_nm")
    private Integer maxTorqueNm;

    @Column(name = "acceleration_0_100_sec", precision = 10, scale = 2)
    private BigDecimal acceleration0100Sec;

    @Column(name = "top_speed_kmh")
    private Integer topSpeedKmh;

    // Constructor rỗng (bắt buộc cho JPA)
    public CarPowertrain() {
    }

    // Constructor đầy đủ
    public CarPowertrain(Long carId, Car car,
                         String driveType,
                         Integer maxPowerHp,
                         Integer maxTorqueNm,
                         BigDecimal acceleration0100Sec,
                         Integer topSpeedKmh) {
        this.carId = carId;
        this.car = car;
        this.driveType = driveType;
        this.maxPowerHp = maxPowerHp;
        this.maxTorqueNm = maxTorqueNm;
        this.acceleration0100Sec = acceleration0100Sec;
        this.topSpeedKmh = topSpeedKmh;
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