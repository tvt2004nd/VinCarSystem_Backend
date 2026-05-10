package com.vin.VinSystem.Car.Entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ModelColorOptionId implements Serializable {

    @Column(name = "model_id")
    private Long modelId;

    @Column(name = "color_id")
    private Long colorId;

    // Constructor rỗng (bắt buộc cho JPA)
    public ModelColorOptionId() {
    }

    // Constructor đầy đủ
    public ModelColorOptionId(Long modelId, Long colorId) {
        this.modelId = modelId;
        this.colorId = colorId;
    }

    // Getter & Setter
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

    // equals & hashCode (rất quan trọng cho composite key)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ModelColorOptionId)) return false;
        ModelColorOptionId that = (ModelColorOptionId) o;
        return Objects.equals(modelId, that.modelId) &&
               Objects.equals(colorId, that.colorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelId, colorId);
    }
}