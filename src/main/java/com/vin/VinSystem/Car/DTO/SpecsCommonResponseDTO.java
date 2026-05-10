package com.vin.VinSystem.Car.DTO;

public class SpecsCommonResponseDTO {
    private Long carId;
    private String bodyType;
    private Integer seatingCapacity;
    private Integer doors;
    private String fuelType;

    public SpecsCommonResponseDTO() {
    }

    public SpecsCommonResponseDTO(Long carId, String bodyType, Integer seatingCapacity,
                                  Integer doors, String fuelType) {
        this.carId = carId;
        this.bodyType = bodyType;
        this.seatingCapacity = seatingCapacity;
        this.doors = doors;
        this.fuelType = fuelType;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
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