package com.vin.VinSystem.Car.DTO;

import java.math.BigDecimal;
import java.util.List;

public class CarResponseDTO {
    private Long carId;
    private Long modelId;
    private String modelName;
    private Long seriesId;
    private String seriesName;

    // ✅ Thay colorId + colorName đơn lẻ bằng List
    private List<CarColorResponseDTO> colors;

    private String carName;
    private BigDecimal price;
    private Integer yearOfManufacture;
    private String status;
    private List<CarImageDTO> carImages;
    private CarComfortResponseDTO comfort;
    private CarSafetyResponseDTO safety;
    private CarPowertrainResponseDTO powertrain;
    private CarEvSpecsResponseDTO evSpecs;
    private CarWarrantyResponseDTO warranty;
    private CarDimensionsResponseDTO dimensions;
    private SpecsCommonResponseDTO specsCommon;
    private List<ModelColorOptionResponseDTO> modelColorOptions;


    public CarResponseDTO() {
    }

    // ✅ Constructor cập nhật — bỏ colorId/colorName, thêm colors
    public CarResponseDTO(Long carId, Long modelId, String modelName, Long seriesId, String seriesName,
                          List<CarColorResponseDTO> colors, String carName, BigDecimal price,
                          Integer yearOfManufacture, String status, List<CarImageDTO> carImages,
                          CarComfortResponseDTO comfort, CarSafetyResponseDTO safety,
                          CarPowertrainResponseDTO powertrain, CarEvSpecsResponseDTO evSpecs,
                          CarWarrantyResponseDTO warranty, CarDimensionsResponseDTO dimensions,
                          SpecsCommonResponseDTO specsCommon,
                          List<ModelColorOptionResponseDTO> modelColorOptions) {
        this.carId = carId;
        this.modelId = modelId;
        this.modelName = modelName;
        this.seriesId = seriesId;
        this.seriesName = seriesName;
        this.colors = colors;
        this.carName = carName;
        this.price = price;
        this.yearOfManufacture = yearOfManufacture;
        this.status = status;
        this.carImages = carImages;
        this.comfort = comfort;
        this.safety = safety;
        this.powertrain = powertrain;
        this.evSpecs = evSpecs;
        this.warranty = warranty;
        this.dimensions = dimensions;
        this.specsCommon = specsCommon;
        this.modelColorOptions = modelColorOptions;
    }

    // --- Getters & Setters ---

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }

    public Long getModelId() { return modelId; }
    public void setModelId(Long modelId) { this.modelId = modelId; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public Long getSeriesId() { return seriesId; }
    public void setSeriesId(Long seriesId) { this.seriesId = seriesId; }

    public String getSeriesName() { return seriesName; }
    public void setSeriesName(String seriesName) { this.seriesName = seriesName; }

    // ✅ Getter/Setter cho colors
    public List<CarColorResponseDTO> getColors() { return colors; }
    public void setColors(List<CarColorResponseDTO> colors) { this.colors = colors; }

    public String getCarName() { return carName; }
    public void setCarName(String carName) { this.carName = carName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getYearOfManufacture() { return yearOfManufacture; }
    public void setYearOfManufacture(Integer yearOfManufacture) { this.yearOfManufacture = yearOfManufacture; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<CarImageDTO> getCarImages() { return carImages; }
    public void setCarImages(List<CarImageDTO> carImages) { this.carImages = carImages; }

    public CarComfortResponseDTO getComfort() { return comfort; }
    public void setComfort(CarComfortResponseDTO comfort) { this.comfort = comfort; }

    public CarSafetyResponseDTO getSafety() { return safety; }
    public void setSafety(CarSafetyResponseDTO safety) { this.safety = safety; }

    public CarPowertrainResponseDTO getPowertrain() { return powertrain; }
    public void setPowertrain(CarPowertrainResponseDTO powertrain) { this.powertrain = powertrain; }

    public CarEvSpecsResponseDTO getEvSpecs() { return evSpecs; }
    public void setEvSpecs(CarEvSpecsResponseDTO evSpecs) { this.evSpecs = evSpecs; }

    public CarWarrantyResponseDTO getWarranty() { return warranty; }
    public void setWarranty(CarWarrantyResponseDTO warranty) { this.warranty = warranty; }

    public CarDimensionsResponseDTO getDimensions() { return dimensions; }
    public void setDimensions(CarDimensionsResponseDTO dimensions) { this.dimensions = dimensions; }

    public SpecsCommonResponseDTO getSpecsCommon() { return specsCommon; }
    public void setSpecsCommon(SpecsCommonResponseDTO specsCommon) { this.specsCommon = specsCommon; }

    public List<ModelColorOptionResponseDTO> getModelColorOptions() { return modelColorOptions; }
    public void setModelColorOptions(List<ModelColorOptionResponseDTO> modelColorOptions) { this.modelColorOptions = modelColorOptions;    }
}