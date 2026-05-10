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
@Table(name = "specs_common")
public class SpecsCommon {

    @Id
    @Column(name = "car_id")
    private Long carId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "car_id")
    private Car car;

    @Column(name = "body_type", length = 100)
    private String bodyType;

    @Column(name = "seating_capacity")
    private Integer seatingCapacity;

    @Column(name = "doors")
    private Integer doors;

    @Column(name = "fuel_type", length = 100)
    private String fuelType;

    // Constructor rỗng (bắt buộc cho JPA)
    public SpecsCommon() {
    }

    // Constructor đầy đủ
    public SpecsCommon(Long carId, Car car, String bodyType,
                       Integer seatingCapacity, Integer doors, String fuelType) {
        this.carId = carId;
        this.car = car;
        this.bodyType = bodyType;
        this.seatingCapacity = seatingCapacity;
        this.doors = doors;
        this.fuelType = fuelType;
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

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public Integer getDoors() {
        return doors;
    }

    public void setDoors(Integer doors) {
        this.doors = doors;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }
}