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
@Table(name = "car_dimensions")
public class CarDimensions {

    @Id
    @Column(name = "car_id")
    private Long carId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "car_id")
    private Car car;

    @Column(name = "length_mm")
    private Integer lengthMm;

    @Column(name = "width_mm")
    private Integer widthMm;

    @Column(name = "height_mm")
    private Integer heightMm;

    @Column(name = "wheelbase_mm")
    private Integer wheelbaseMm;

    @Column(name = "curb_weight_kg", precision = 10, scale = 2)
    private BigDecimal curbWeightKg;

    @Column(name = "trunk_volume_l")
    private Integer trunkVolumeL;

    // Constructor rỗng (bắt buộc cho JPA)
    public CarDimensions() {
    }

    // Constructor đầy đủ
    public CarDimensions(Long carId, Car car,
                         Integer lengthMm, Integer widthMm, Integer heightMm,
                         Integer wheelbaseMm, BigDecimal curbWeightKg,
                         Integer trunkVolumeL) {
        this.carId = carId;
        this.car = car;
        this.lengthMm = lengthMm;
        this.widthMm = widthMm;
        this.heightMm = heightMm;
        this.wheelbaseMm = wheelbaseMm;
        this.curbWeightKg = curbWeightKg;
        this.trunkVolumeL = trunkVolumeL;
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