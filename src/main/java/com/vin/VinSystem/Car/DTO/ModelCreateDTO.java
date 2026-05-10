package com.vin.VinSystem.Car.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO dùng để tạo mới Model, bám sát với bảng model trong CSDL.
 */
public class ModelCreateDTO {

    @NotBlank(message = "Tên model không được để trống")
    @Size(min = 2, max = 255, message = "Tên model phải từ 2 đến 255 ký tự")
    private String modelName;

    @Size(max = 100, message = "Segment không được vượt quá 100 ký tự")
    private String segment;

    public ModelCreateDTO() {
    }

    public ModelCreateDTO(String modelName, String segment) {
        this.modelName = modelName;
        this.segment = segment;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }
}
