package com.vin.VinSystem.Car.DTO;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class CarUpdateDTO {

    @NotNull(message = "Car ID không được để trống")
    private Long carId;

    @NotNull(message = "Model ID không được để trống")
    private Long modelId;

    @NotNull(message = "Series ID không được để trống")
    private Long seriesId;

    // ✅ Đổi Long colorId → List<Long> colorIds
    @Size(min = 1, message = "Phải có ít nhất 1 màu")
    private List<Long> colorIds;

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

    @Size(max = 6, message = "Không được thêm quá 6 ảnh")
    private List<CarImageDTO> carImages;

    private CarWarrantyCreateDTO warranty;
    private CarEvSpecsCreateDTO evSpecs;
    private CarPowertrainCreateDTO powertrain;
    private CarSafetyCreateDTO safety;
    private CarComfortCreateDTO comfort;
    private CarDimensionsCreateDTO dimensions;
    private SpecsCommonCreateDTO specsCommon;
    private List<ModelColorOptionCreateDTO> modelColorOptions;

    public CarUpdateDTO() {}

    public CarUpdateDTO(Long carId, Long modelId, Long seriesId, List<Long> colorIds,
                        String carName, BigDecimal price, Integer yearOfManufacture,
                        String status, List<CarImageDTO> carImages) {
        this.carId = carId;
        this.modelId = modelId;
        this.seriesId = seriesId;
        this.colorIds = colorIds;
        this.carName = carName;
        this.price = price;
        this.yearOfManufacture = yearOfManufacture;
        this.status = status;
        this.carImages = carImages;
    }

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }

    public Long getModelId() { return modelId; }
    public void setModelId(Long modelId) { this.modelId = modelId; }

    public Long getSeriesId() { return seriesId; }
    public void setSeriesId(Long seriesId) { this.seriesId = seriesId; }

    // ✅ Getter/Setter mới
    public List<Long> getColorIds() { return colorIds; }
    public void setColorIds(List<Long> colorIds) { this.colorIds = colorIds; }

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

    public CarWarrantyCreateDTO getWarranty() { return warranty; }
    public void setWarranty(CarWarrantyCreateDTO warranty) { this.warranty = warranty; }

    public CarEvSpecsCreateDTO getEvSpecs() { return evSpecs; }
    public void setEvSpecs(CarEvSpecsCreateDTO evSpecs) { this.evSpecs = evSpecs; }

    public CarPowertrainCreateDTO getPowertrain() { return powertrain; }
    public void setPowertrain(CarPowertrainCreateDTO powertrain) { this.powertrain = powertrain; }

    public CarSafetyCreateDTO getSafety() { return safety; }
    public void setSafety(CarSafetyCreateDTO safety) { this.safety = safety; }

    public CarComfortCreateDTO getComfort() { return comfort; }
    public void setComfort(CarComfortCreateDTO comfort) { this.comfort = comfort; }

    public CarDimensionsCreateDTO getDimensions() { return dimensions; }
    public void setDimensions(CarDimensionsCreateDTO dimensions) { this.dimensions = dimensions; }

    public SpecsCommonCreateDTO getSpecsCommon() { return specsCommon; }
    public void setSpecsCommon(SpecsCommonCreateDTO specsCommon) { this.specsCommon = specsCommon; }

    public List<ModelColorOptionCreateDTO> getModelColorOptions() { return modelColorOptions; }
    public void setModelColorOptions(List<ModelColorOptionCreateDTO> modelColorOptions) { this.modelColorOptions = modelColorOptions; }
}