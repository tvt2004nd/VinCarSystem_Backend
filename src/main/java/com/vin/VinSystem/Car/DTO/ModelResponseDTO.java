package com.vin.VinSystem.Car.DTO;

/**
 * DTO trả về thông tin Model cho client.
 */
public class ModelResponseDTO {

    private Long modelId;
    private String modelName;
    private String segment;

    public ModelResponseDTO() {
    }

    public ModelResponseDTO(Long modelId, String modelName, String segment) {
        this.modelId = modelId;
        this.modelName = modelName;
        this.segment = segment;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
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

