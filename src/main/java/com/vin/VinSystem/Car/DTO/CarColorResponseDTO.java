package com.vin.VinSystem.Car.DTO;

/**
 * DTO trả về thông tin CarColor cho client.
 */
public class CarColorResponseDTO {

    private Long colorId;
    private String colorName;
    private String colorCode;

    public CarColorResponseDTO() {
    }

    public CarColorResponseDTO(Long colorId, String colorName, String colorCode) {
        this.colorId = colorId;
        this.colorName = colorName;
        this.colorCode = colorCode;
    }

    public Long getColorId() {
        return colorId;
    }

    public void setColorId(Long colorId) {
        this.colorId = colorId;
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

