package com.vin.VinSystem.Car.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO dùng để tạo mới CarSeries, bám sát bảng car_series.
 */
public class CarSeriesCreateDTO {

    @NotNull(message = "Model ID không được để trống")
    private Long modelId;

    @Size(max = 50, message = "Series code khô ng được vượt quá 50 ký tự")
    private String seriesCode;

    @NotBlank(message = "Tên series không được để trống")
    @Size(min = 2, max = 255, message = "Tên series phải từ 2 đến 255 ký tự")
    private String seriesName;

    private String description;

    private Integer sortOrder = 0;

    @Size(max = 50, message = "Trạng thái không được vượt quá 50 ký tự")
    private String status;

    public CarSeriesCreateDTO() {
    }

    public CarSeriesCreateDTO(Long modelId, String seriesCode, String seriesName,
                              String description, Integer sortOrder, String status) {
        this.modelId = modelId;
        this.seriesCode = seriesCode;
        this.seriesName = seriesName;
        this.description = description;
        this.sortOrder = sortOrder;
        this.status = status;
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

