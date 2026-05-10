package com.vin.VinSystem.Car.DTO;

import java.math.BigDecimal;

public class ModelColorOptionResponseDTO {
    private Long modelId;
    private Long colorId;
    private BigDecimal extraPrice;

    public ModelColorOptionResponseDTO() {
    }

    public ModelColorOptionResponseDTO(Long modelId, Long colorId, BigDecimal extraPrice) {
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