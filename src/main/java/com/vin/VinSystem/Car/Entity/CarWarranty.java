package com.vin.VinSystem.Car.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "car_warranty")
public class CarWarranty {

    @Id
    @Column(name = "car_id")
    private Long carId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "car_id")
    private Car car;

    @Column(name = "warranty_years")
    private Integer warrantyYears;

    @Column(name = "warranty_km")
    private Integer warrantyKm;

    @Column(name = "battery_warranty_years")
    private Integer batteryWarrantyYears;

    @Column(name = "battery_warranty_km")
    private Integer batteryWarrantyKm;

    // Constructor rỗng (bắt buộc cho JPA)
    public CarWarranty() {
    }

    // Constructor đầy đủ
    public CarWarranty(Long carId, Car car,
                       Integer warrantyYears, Integer warrantyKm,
                       Integer batteryWarrantyYears, Integer batteryWarrantyKm) {
        this.carId = carId;
        this.car = car;
        this.warrantyYears = warrantyYears;
        this.warrantyKm = warrantyKm;
        this.batteryWarrantyYears = batteryWarrantyYears;
        this.batteryWarrantyKm = batteryWarrantyKm;
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