package com.vin.VinSystem.Car.DTO;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public class ModelColorOptionCreateDTO {

    @NotNull(message = "Model ID không được để trống")
    private Long modelId;

    @NotNull(message = "Color ID không được để trống")
    private Long colorId;

    @PositiveOrZero(message = "Giá thêm phải lớn hơn hoặc bằng 0")
    private BigDecimal extraPrice;

    public ModelColorOptionCreateDTO() {
    }

    public ModelColorOptionCreateDTO(Long modelId, Long colorId, BigDecimal extraPrice) {
        this.modelId = modelId;
        this.colorId = colorId;
        this.extraPrice = extraPrice;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public Long getColorId() {
        return colorId;
    }

    public void setColorId(Long colorId) {
        this.colorId = colorId;
    }

    public BigDecimal getExtraPrice() {
        return extraPrice;
    }

    public void setExtraPrice(BigDecimal extraPrice) {
        this.extraPrice = extraPrice;
    }
}