package com.vin.VinSystem.Car.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO dùng để tạo mới CarColor, bám sát bảng car_color.
 */
public class CarColorCreateDTO {

    @NotBlank(message = "Tên màu không được để trống")
    @Size(min = 2, max = 100, message = "Tên màu phải từ 2 đến 100 ký tự")
    private String colorName;

    @Size(max = 50, message = "Mã màu không được vượt quá 50 ký tự")
    private String colorCode;

    public CarColorCreateDTO() {
    }

    public CarColorCreateDTO(String colorName, String colorCode) {
        this.colorName = colorName;
        this.colorCode = colorCode;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }
}

