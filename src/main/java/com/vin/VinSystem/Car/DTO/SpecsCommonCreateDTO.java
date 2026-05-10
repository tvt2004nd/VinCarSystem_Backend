package com.vin.VinSystem.Car.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class SpecsCommonCreateDTO {

    @Size(max = 100, message = "Kiểu thân xe tối đa 100 ký tự")
    private String bodyType;

    @Min(value = 0, message = "Sức chứa phải lớn hơn hoặc bằng 0")
    private Integer seatingCapacity;

    @Min(value = 0, message = "Số cửa phải lớn hơn hoặc bằng 0")
    private Integer doors;

    @Size(max = 100, message = "Loại nhiên liệu tối đa 100 ký tự")
    private String fuelType;

    public SpecsCommonCreateDTO() {
    }

    public SpecsCommonCreateDTO(String bodyType, Integer seatingCapacity, Integer doors, String fuelType) {
        this.bodyType = bodyType;
        this.seatingCapacity = seatingCapacity;
        this.doors = doors;
        this.fuelType = fuelType;
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