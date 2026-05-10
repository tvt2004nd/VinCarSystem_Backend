package com.vin.VinSystem.Car.DTO;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;



@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class CarCreateDTO {

    @NotNull(message = "Model ID không được để trống")
    private Long modelId;

    @NotNull(message = "Series ID không được để trống")
    private Long seriesId;

    @NotNull(message = "Danh sách màu không được để trống")
    @Size(min = 1, message = "Phải có ít nhất 1 màu")
    @JsonProperty("colorIds") // Đảm bảo JSON key là "colorIds" để map vào List<Long> colorId
    private List<Long> colorId;

    @NotBlank(message = "Tên xe không được để trống")
    @Size(min = 3, max = 255, message = "Tên xe phải từ 3 đến 255 ký tự")
    private String carName;

    @NotNull(message = "Giá không được để trống")
    @Positive(message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @NotNull(message = "Năm sản xuất không được để trống")
    @Positive(message = "Năm sản xuất phải lớn hơn 0")
    private Integer yearOfManufacture;

    @NotBlank(message = "Trạng thái không được để trống")
    private String status;

    // optional details that can be supplied at creation time
    private CarWarrantyCreateDTO warranty;
    private CarEvSpecsCreateDTO evSpecs;
    private CarPowertrainCreateDTO powertrain;
    private CarSafetyCreateDTO safety;
    private CarComfortCreateDTO comfort;

    // new optional tables
    private CarDimensionsCreateDTO dimensions;
    private SpecsCommonCreateDTO specsCommon;

    // optional model-color option data
    private List<ModelColorOptionCreateDTO> modelColorOptions;

    public CarCreateDTO() {
    }

    public CarCreateDTO(Long modelId, Long seriesId, List<Long> colorId, String carName,
                       BigDecimal price, Integer yearOfManufacture, String status) {
        this.modelId = modelId;
        this.seriesId = seriesId;
        this.colorId = colorId;
        this.carName = carName;
        this.price = price;
        this.yearOfManufacture = yearOfManufacture;
        this.status = status;
    }

    public CarCreateDTO(Long modelId, Long seriesId, List<Long> colorId, String carName,
                       BigDecimal price, Integer yearOfManufacture, String status,
                       CarWarrantyCreateDTO warranty,
                       CarEvSpecsCreateDTO evSpecs,
                       CarPowertrainCreateDTO powertrain,
                       CarSafetyCreateDTO safety,
                       CarComfortCreateDTO comfort,
                       CarDimensionsCreateDTO dimensions,
                       SpecsCommonCreateDTO specsCommon,
                       List<ModelColorOptionCreateDTO> modelColorOptions) {
        this(modelId, seriesId, colorId, carName, price, yearOfManufacture, status);
        this.warranty = warranty;
        this.evSpecs = evSpecs;
        this.powertrain = powertrain;
        this.safety = safety;
        this.comfort = comfort;
        this.dimensions = dimensions;
        this.specsCommon = specsCommon;
        this.modelColorOptions = modelColorOptions;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public Long getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(Long seriesId) {
        this.seriesId = seriesId;
    }

    public List<Long> getColorId() {
        return colorId;
    }

    public void setColorId(List<Long> colorId) {
        this.colorId = colorId;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getYearOfManufacture() {
        return yearOfManufacture;
    }

    public void setYearOfManufacture(Integer yearOfManufacture) {
        this.yearOfManufacture = yearOfManufacture;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CarWarrantyCreateDTO getWarranty() {
        return warranty;
    }

    public void setWarranty(CarWarrantyCreateDTO warranty) {
        this.warranty = warranty;
    }

    public CarEvSpecsCreateDTO getEvSpecs() {
        return evSpecs;
    }

    public void setEvSpecs(CarEvSpecsCreateDTO evSpecs) {
        this.evSpecs = evSpecs;
    }

    public CarPowertrainCreateDTO getPowertrain() {
        return powertrain;
    }

    public void setPowertrain(CarPowertrainCreateDTO powertrain) {
        this.powertrain = powertrain;
    }

    public CarSafetyCreateDTO getSafety() {
        return safety;
    }

    public void setSafety(CarSafetyCreateDTO safety) {
        this.safety = safety;
    }

    public CarComfortCreateDTO getComfort() {
        return comfort;
    }

    public void setComfort(CarComfortCreateDTO comfort) {
        this.comfort = comfort;
    }

    public CarDimensionsCreateDTO getDimensions() {
        return dimensions;
    }

    public void setDimensions(CarDimensionsCreateDTO dimensions) {
        this.dimensions = dimensions;
    }

    public SpecsCommonCreateDTO getSpecsCommon() {
        return specsCommon;
    }

    public void setSpecsCommon(SpecsCommonCreateDTO specsCommon) {
        this.specsCommon = specsCommon;
    }

    public List<ModelColorOptionCreateDTO> getModelColorOptions() {
        return modelColorOptions;
    }

    public void setModelColorOptions(List<ModelColorOptionCreateDTO> modelColorOptions) {
        this.modelColorOptions = modelColorOptions;
    }

}
