package com.vin.VinSystem.Car.DTO;

/**
 * DTO trả về thông tin CarSeries cho client.
 */
public class CarSeriesResponseDTO {

    private Long seriesId;
    private Long modelId;
    private String seriesCode;
    private String seriesName;
    private String description;
    private Integer sortOrder;
    private String status;

    public CarSeriesResponseDTO() {
    }

    public CarSeriesResponseDTO(Long seriesId, Long modelId, String seriesCode, String seriesName,
                                String description, Integer sortOrder, String status) {
        this.seriesId = seriesId;
        this.modelId = modelId;
        this.seriesCode = seriesCode;
        this.seriesName = seriesName;
        this.description = description;
        this.sortOrder = sortOrder;
        this.status = status;
    }

    public Long getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(Long seriesId) {
        this.seriesId = seriesId;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getSeriesCode() {
        return seriesCode;
    }

    public void setSeriesCode(String seriesCode) {
        this.seriesCode = seriesCode;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

