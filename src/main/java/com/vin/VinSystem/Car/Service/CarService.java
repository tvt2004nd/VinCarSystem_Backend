package com.vin.VinSystem.Car.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.vin.VinSystem.Car.Controller.exception.ResourceNotFoundException;
import com.vin.VinSystem.Car.Controller.exception.ValidationException;
import com.vin.VinSystem.Car.DTO.CarColorResponseDTO;
import com.vin.VinSystem.Car.DTO.CarComfortCreateDTO;
import com.vin.VinSystem.Car.DTO.CarComfortResponseDTO;
import com.vin.VinSystem.Car.DTO.CarCreateDTO;
import com.vin.VinSystem.Car.DTO.CarDimensionsCreateDTO;
import com.vin.VinSystem.Car.DTO.CarDimensionsResponseDTO;
import com.vin.VinSystem.Car.DTO.CarEvSpecsCreateDTO;
import com.vin.VinSystem.Car.DTO.CarEvSpecsResponseDTO;
import com.vin.VinSystem.Car.DTO.CarImageDTO;
import com.vin.VinSystem.Car.DTO.CarPowertrainCreateDTO;
import com.vin.VinSystem.Car.DTO.CarPowertrainResponseDTO;
import com.vin.VinSystem.Car.DTO.CarResponseDTO;
import com.vin.VinSystem.Car.DTO.CarSafetyCreateDTO;
import com.vin.VinSystem.Car.DTO.CarSafetyResponseDTO;
import com.vin.VinSystem.Car.DTO.CarUpdateDTO;
import com.vin.VinSystem.Car.DTO.CarWarrantyCreateDTO;
import com.vin.VinSystem.Car.DTO.CarWarrantyResponseDTO;
import com.vin.VinSystem.Car.DTO.ModelColorOptionCreateDTO;
import com.vin.VinSystem.Car.DTO.ModelColorOptionResponseDTO;
import com.vin.VinSystem.Car.DTO.SpecsCommonCreateDTO;
import com.vin.VinSystem.Car.DTO.SpecsCommonResponseDTO;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Car.Entity.CarColor;
import com.vin.VinSystem.Car.Entity.CarImage;
import com.vin.VinSystem.Car.Entity.CarSeries;
import com.vin.VinSystem.Car.Entity.Model;
import com.vin.VinSystem.Car.Repository.CarImageRepository;
import com.vin.VinSystem.Car.Repository.CarRepository;
import com.vin.VinSystem.Config.CloudinaryService;

@Service
@Transactional
public class CarService {

    private final CarRepository carRepository;
    private final CarImageRepository carImageRepository;
    private final ModelService modelService;
    private final CarSeriesService seriesService;
    private final CarColorService carColorService;
    private final CarWarrantyService carWarrantyService;
    private final CarEvSpecsService carEvSpecsService;
    private final CarPowertrainService carPowertrainService;
    private final CarSafetyService carSafetyService;
    private final CarComfortService carComfortService;
    private final CarDimensionsService carDimensionsService;
    private final SpecsCommonService specsCommonService;
    private final ModelColorOptionService modelColorOptionService;
    private final CloudinaryService cloudinaryService;

    public CarService(CarRepository carRepository, CarImageRepository carImageRepository,
                      ModelService modelService, CarSeriesService seriesService,
                      CarColorService carColorService,
                      CarWarrantyService carWarrantyService,
                      CarEvSpecsService carEvSpecsService,
                      CarPowertrainService carPowertrainService,
                      CarSafetyService carSafetyService,
                      CarComfortService carComfortService,
                      CarDimensionsService carDimensionsService,
                      SpecsCommonService specsCommonService,
                      ModelColorOptionService modelColorOptionService,CloudinaryService cloudinaryService) {
        this.carRepository = carRepository;
        this.carImageRepository = carImageRepository;
        this.modelService = modelService;
        this.seriesService = seriesService;
        this.carColorService = carColorService;
        this.carWarrantyService = carWarrantyService;
        this.carEvSpecsService = carEvSpecsService;
        this.carPowertrainService = carPowertrainService;
        this.carSafetyService = carSafetyService;
        this.carComfortService = carComfortService;
        this.carDimensionsService = carDimensionsService;
        this.specsCommonService = specsCommonService;
        this.modelColorOptionService = modelColorOptionService;
        this.cloudinaryService = cloudinaryService;
    }

    // =========================================================
    // CREATE
    // =========================================================
    public CarResponseDTO createCarWithImages(CarCreateDTO createDTO, List<MultipartFile> images) {
        validateCarCreateDTO(createDTO);

        Car car = new Car();
        car.setCarName(createDTO.getCarName());
        car.setPrice(createDTO.getPrice());
        car.setYearOfManufacture(createDTO.getYearOfManufacture());
        car.setStatus(createDTO.getStatus());

        Model model = modelService.getModelById(createDTO.getModelId());
        CarSeries series = seriesService.getSeriesById(createDTO.getSeriesId());
        car.setModel(model);
        car.setSeries(series);

        if (createDTO.getColorId() == null || createDTO.getColorId().isEmpty()) {
            throw new ValidationException("Phải chọn ít nhất 1 màu");
        }
Set<CarColor> colors = createDTO.getColorId().stream()
        .map(carColorService::getColorById)
        .collect(Collectors.toSet());

        Car savedCar = carRepository.save(car);

        if (images != null && !images.isEmpty()) {
            if (images.size() > 6) throw new ValidationException("Không được thêm quá 6 ảnh");
            saveImages(savedCar, images);
        }

        Long carId = savedCar.getCarId();

        CarWarrantyCreateDTO warrantyDTO = createDTO.getWarranty();
        if (warrantyDTO == null) warrantyDTO = new CarWarrantyCreateDTO();
        carWarrantyService.addOrUpdateCarWarranty(carId, warrantyDTO);

        CarEvSpecsCreateDTO evSpecsDTO = createDTO.getEvSpecs();
        if (evSpecsDTO == null) evSpecsDTO = new CarEvSpecsCreateDTO();
        carEvSpecsService.addOrUpdateCarEvSpecs(carId, evSpecsDTO);

        CarPowertrainCreateDTO powertrainDTO = createDTO.getPowertrain();
        if (powertrainDTO == null) powertrainDTO = new CarPowertrainCreateDTO();
        carPowertrainService.addOrUpdateCarPowertrain(carId, powertrainDTO);

        CarSafetyCreateDTO safetyDTO = createDTO.getSafety();
        if (safetyDTO == null) safetyDTO = new CarSafetyCreateDTO();
        carSafetyService.addOrUpdateCarSafety(carId, safetyDTO);

        CarComfortCreateDTO comfortDTO = createDTO.getComfort();
        if (comfortDTO == null) comfortDTO = new CarComfortCreateDTO();
        carComfortService.addOrUpdateCarComfort(carId, comfortDTO);

        CarDimensionsCreateDTO dimsDTO = createDTO.getDimensions();
        if (dimsDTO == null) dimsDTO = new CarDimensionsCreateDTO();
        carDimensionsService.addOrUpdateCarDimensions(carId, dimsDTO);

        SpecsCommonCreateDTO specsDTO = createDTO.getSpecsCommon();
        if (specsDTO == null) specsDTO = new SpecsCommonCreateDTO();
        specsCommonService.addOrUpdateSpecsCommon(carId, specsDTO);

        List<ModelColorOptionCreateDTO> optionDTOs = createDTO.getModelColorOptions();
        for (Long colorId : createDTO.getColorId()) {
            ModelColorOptionCreateDTO optionDTO = null;
            if (optionDTOs != null) {
                optionDTO = optionDTOs.stream()
                        .filter(o -> colorId.equals(o.getColorId()))
                        .findFirst()
                        .orElse(null);
            }
            if (optionDTO != null) {
                optionDTO.setModelId(createDTO.getModelId());
                optionDTO.setColorId(colorId);
                modelColorOptionService.addOrUpdateOption(optionDTO);
            } else {
                modelColorOptionService.ensureOptionExists(createDTO.getModelId(), colorId);
            }
        }

        Car updatedCar = carRepository.findById(carId).orElse(savedCar);
        return convertToResponseDTO(updatedCar);
    }

    // =========================================================
    // UPDATE WITH IMAGES
    // =========================================================
    public CarResponseDTO updateCarWithImages(CarUpdateDTO updateDTO, List<MultipartFile> images) {

        Car existingCar = carRepository.findById(updateDTO.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy xe với ID: " + updateDTO.getCarId()));

        if (updateDTO.getCarName() != null) existingCar.setCarName(updateDTO.getCarName());
        if (updateDTO.getPrice() != null) existingCar.setPrice(updateDTO.getPrice());
        if (updateDTO.getYearOfManufacture() != null) existingCar.setYearOfManufacture(updateDTO.getYearOfManufacture());
        if (updateDTO.getStatus() != null) existingCar.setStatus(updateDTO.getStatus());

        if (updateDTO.getModelId() != null) existingCar.setModel(modelService.getModelById(updateDTO.getModelId()));
        if (updateDTO.getSeriesId() != null) existingCar.setSeries(seriesService.getSeriesById(updateDTO.getSeriesId()));

        if (updateDTO.getColorIds() != null && !updateDTO.getColorIds().isEmpty()) {
Set<CarColor> newColors = updateDTO.getColorIds().stream()
        .map(carColorService::getColorById)
        .collect(Collectors.toSet());
            existingCar.setColors(newColors);
        }

        Car savedCar = carRepository.save(existingCar);
        Long carId = savedCar.getCarId();

        // Nếu có ảnh mới, lưu ảnh mới
if (images != null && !images.isEmpty()) {
    if (images.size() > 6) throw new ValidationException("Không được thêm quá 6 ảnh");
    // ✅ Xóa ảnh cũ trên Cloudinary + DB trước
    deleteCarImageFiles(carId);
    carImageRepository.deleteByCar_CarId(carId);
    // Rồi mới lưu ảnh mới
    saveImages(savedCar, images);
}

        if (updateDTO.getWarranty() != null) carWarrantyService.addOrUpdateCarWarranty(carId, updateDTO.getWarranty());
        if (updateDTO.getEvSpecs() != null) carEvSpecsService.addOrUpdateCarEvSpecs(carId, updateDTO.getEvSpecs());
        if (updateDTO.getPowertrain() != null) carPowertrainService.addOrUpdateCarPowertrain(carId, updateDTO.getPowertrain());
        if (updateDTO.getSafety() != null) carSafetyService.addOrUpdateCarSafety(carId, updateDTO.getSafety());
        if (updateDTO.getComfort() != null) carComfortService.addOrUpdateCarComfort(carId, updateDTO.getComfort());
        if (updateDTO.getDimensions() != null) carDimensionsService.addOrUpdateCarDimensions(carId, updateDTO.getDimensions());
        if (updateDTO.getSpecsCommon() != null) specsCommonService.addOrUpdateSpecsCommon(carId, updateDTO.getSpecsCommon());

        if (updateDTO.getModelColorOptions() != null && !updateDTO.getModelColorOptions().isEmpty()) {
            Long effectiveModelId = savedCar.getModel().getModelId();
            for (ModelColorOptionCreateDTO optionDTO : updateDTO.getModelColorOptions()) {
                optionDTO.setModelId(effectiveModelId);
                modelColorOptionService.addOrUpdateOption(optionDTO);
            }
        }

        Car updatedCar = carRepository.findById(carId).orElse(savedCar);
        return convertToResponseDTO(updatedCar);
    }

    // =========================================================
    // UPDATE (không kèm ảnh)
    // =========================================================
    public CarResponseDTO updateCar(CarUpdateDTO updateDTO) {
        validateCarUpdateDTO(updateDTO);

        Car car = carRepository.findById(updateDTO.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy xe với ID: " + updateDTO.getCarId()));

        car.setCarName(updateDTO.getCarName());
        car.setPrice(updateDTO.getPrice());
        car.setYearOfManufacture(updateDTO.getYearOfManufacture());
        car.setStatus(updateDTO.getStatus());

        Model model = modelService.getModelById(updateDTO.getModelId());
        CarSeries series = seriesService.getSeriesById(updateDTO.getSeriesId());
        car.setModel(model);
        car.setSeries(series);

        if (updateDTO.getColorIds() != null && !updateDTO.getColorIds().isEmpty()) {
 Set<CarColor> newColors = updateDTO.getColorIds().stream()
        .map(carColorService::getColorById)
        .collect(Collectors.toSet());
            car.setColors(newColors);
        }

        if (updateDTO.getCarImages() != null && !updateDTO.getCarImages().isEmpty()) {
            deleteCarImageFiles(car.getCarId());
            carImageRepository.deleteByCar_CarId(car.getCarId());
            car.getCarImages().clear();
            addCarImages(car, updateDTO.getCarImages());
        }

        Car updatedCar = carRepository.save(car);
        return convertToResponseDTO(updatedCar);
    }

    // =========================================================
    // READ
    // =========================================================
    @Transactional(readOnly = true)
    public List<CarResponseDTO> getAllCars() {
        return convertCarsToResponseDTOs(carRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<CarResponseDTO> getAllActiveCars() {
        return convertCarsToResponseDTOs(carRepository.findAllActive());
    }

    @Transactional(readOnly = true)
    public CarResponseDTO getCarById(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));
        return convertToResponseDTO(car);
    }

    @Transactional(readOnly = true)
    public List<CarResponseDTO> getCarsByModelId(Long modelId) {
        return convertCarsToResponseDTOs(carRepository.findByModel_ModelId(modelId));
    }

    @Transactional(readOnly = true)
    public List<CarResponseDTO> getCarsBySeriesId(Long seriesId) {
        return convertCarsToResponseDTOs(carRepository.findBySeries_SeriesId(seriesId));
    }

    @Transactional(readOnly = true)
    public List<CarResponseDTO> getCarsByColorId(Long colorId) {
        return convertCarsToResponseDTOs(carRepository.findByColors_ColorId(colorId));
    }

    @Transactional(readOnly = true)
    public List<CarResponseDTO> getCarsByStatus(String status) {
        return convertCarsToResponseDTOs(carRepository.findByStatus(status));
    }

    @Transactional(readOnly = true)
    public List<CarResponseDTO> searchCarByName(String carName) {
        return convertCarsToResponseDTOs(carRepository.findByCarNameContainingIgnoreCase(carName));
    }

    // =========================================================
    // DELETE
    // =========================================================
public void deleteCar(Long carId) {
    Car car = carRepository.findById(carId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe"));
    car.setStatus("INACTIVE");
    carRepository.save(car);
}

private void deleteCarImageFiles(Long carId) {
    List<CarImage> images = carImageRepository.findByCar_CarId(carId);
    for (CarImage image : images) {
        if (image.getPublicId() != null) {
            cloudinaryService.delete(image.getPublicId());
        }
    }
}

    // =========================================================
    // PRIVATE HELPERS
    // =========================================================

    /**
     * Dùng cho danh sách xe — batch load options rồi tính per-car
     */
    private List<CarResponseDTO> convertCarsToResponseDTOs(List<Car> cars) {
        if (cars == null || cars.isEmpty()) return List.of();

        List<Long> modelIds = cars.stream()
                .filter(car -> car.getModel() != null)
                .map(car -> car.getModel().getModelId())
                .distinct().collect(Collectors.toList());

        List<Long> colorIds = cars.stream()
                .filter(car -> car.getColors() != null)
                .flatMap(car -> car.getColors().stream())
                .map(CarColor::getColorId)
                .distinct().collect(Collectors.toList());

        Map<String, ModelColorOptionResponseDTO> optionsMap =
                modelColorOptionService.batchLoadOptions(modelIds, colorIds);

        return cars.stream()
                .map(car -> {
                    List<ModelColorOptionResponseDTO> optionDTOs = buildOptionDTOs(car, optionsMap);
                    return convertToResponseDTOInternal(car, optionDTOs);
                })
                .collect(Collectors.toList());
    }

    /**
     * Dùng cho 1 xe đơn lẻ (create / update / getById)
     */
    private CarResponseDTO convertToResponseDTO(Car car) {
        List<ModelColorOptionResponseDTO> optionDTOs = List.of();

        if (car.getModel() != null && car.getColors() != null && !car.getColors().isEmpty()) {
            List<Long> modelIds = List.of(car.getModel().getModelId());
            List<Long> colorIds = car.getColors().stream()
                    .map(CarColor::getColorId)
                    .collect(Collectors.toList());
            Map<String, ModelColorOptionResponseDTO> optionsMap =
                    modelColorOptionService.batchLoadOptions(modelIds, colorIds);
            optionDTOs = buildOptionDTOs(car, optionsMap);
        }

        return convertToResponseDTOInternal(car, optionDTOs);
    }

    /**
     * Tính List<ModelColorOptionResponseDTO> cho 1 xe từ optionsMap đã load sẵn
     */
    private List<ModelColorOptionResponseDTO> buildOptionDTOs(
            Car car, Map<String, ModelColorOptionResponseDTO> optionsMap) {

        if (car.getModel() == null || car.getColors() == null || car.getColors().isEmpty()) {
            return List.of();
        }

        return car.getColors().stream()
                .map(color -> {
                    String key = car.getModel().getModelId() + "_" + color.getColorId();
                    return optionsMap.containsKey(key)
                            ? optionsMap.get(key)
                            : new ModelColorOptionResponseDTO(
                                    car.getModel().getModelId(), color.getColorId(), BigDecimal.ZERO);
                })
                .collect(Collectors.toList());
    }

    /**
     * Map Car entity → CarResponseDTO (nhận optionDTOs đã tính sẵn)
     */
    private CarResponseDTO convertToResponseDTOInternal(Car car,
            List<ModelColorOptionResponseDTO> optionDTOs) {

        List<CarImageDTO> imageDTOs = car.getCarImages() != null
                ? car.getCarImages().stream()
                        .map(this::convertImageToDTO)
                        .collect(Collectors.toList())
                : List.of();

        CarComfortResponseDTO comfortDTO = null;
        if (car.getComfort() != null) {
            comfortDTO = new CarComfortResponseDTO(
                    car.getComfort().getCarId(), car.getComfort().getInfotainmentScreenInch(),
                    car.getComfort().getSpeakerCount(), car.getComfort().getClimateControl(),
                    car.getComfort().getSeatMaterial(), car.getComfort().getSunroof(),
                    car.getComfort().getWirelessCarplay());
        }

        CarSafetyResponseDTO safetyDTO = null;
        if (car.getSafety() != null) {
            safetyDTO = new CarSafetyResponseDTO(
                    car.getSafety().getCarId(), car.getSafety().getAirbags(),
                    car.getSafety().getAbs(), car.getSafety().getEsc(),
                    car.getSafety().getTractionControl(), car.getSafety().getLaneKeepAssist(),
                    car.getSafety().getAdaptiveCruiseControl(), car.getSafety().getRearCamera(),
                    car.getSafety().getParkingSensors());
        }

        CarPowertrainResponseDTO powertrainDTO = null;
        if (car.getPowertrain() != null) {
            powertrainDTO = new CarPowertrainResponseDTO(
                    car.getPowertrain().getCarId(), car.getPowertrain().getDriveType(),
                    car.getPowertrain().getMaxPowerHp(), car.getPowertrain().getMaxTorqueNm(),
                    car.getPowertrain().getAcceleration0100Sec(), car.getPowertrain().getTopSpeedKmh());
        }

        CarEvSpecsResponseDTO evSpecsDTO = null;
        if (car.getEvSpecs() != null) {
            evSpecsDTO = new CarEvSpecsResponseDTO(
                    car.getEvSpecs().getCarId(), car.getEvSpecs().getBatteryCapacityKwh(),
                    car.getEvSpecs().getRangeKm(), car.getEvSpecs().getAcChargingKw(),
                    car.getEvSpecs().getDcFastChargingKw(), car.getEvSpecs().getFastChargeTimeMin());
        }

        CarWarrantyResponseDTO warrantyDTO = null;
        if (car.getWarranty() != null) {
            warrantyDTO = new CarWarrantyResponseDTO(
                    car.getWarranty().getCarId(), car.getWarranty().getWarrantyYears(),
                    car.getWarranty().getWarrantyKm(), car.getWarranty().getBatteryWarrantyYears(),
                    car.getWarranty().getBatteryWarrantyKm());
        }

        CarDimensionsResponseDTO dimsDTO = null;
        if (car.getDimensions() != null) {
            dimsDTO = new CarDimensionsResponseDTO(
                    car.getDimensions().getCarId(), car.getDimensions().getLengthMm(),
                    car.getDimensions().getWidthMm(), car.getDimensions().getHeightMm(),
                    car.getDimensions().getWheelbaseMm(), car.getDimensions().getCurbWeightKg(),
                    car.getDimensions().getTrunkVolumeL());
        }

        SpecsCommonResponseDTO specsDTO = null;
        if (car.getSpecsCommon() != null) {
            specsDTO = new SpecsCommonResponseDTO(
                    car.getSpecsCommon().getCarId(), car.getSpecsCommon().getBodyType(),
                    car.getSpecsCommon().getSeatingCapacity(), car.getSpecsCommon().getDoors(),
                    car.getSpecsCommon().getFuelType());
        }

        List<CarColorResponseDTO> colorDTOs = car.getColors() != null
                ? car.getColors().stream()
                        .map(c -> new CarColorResponseDTO(c.getColorId(), c.getColorName(), c.getColorCode()))
                        .collect(Collectors.toList())
                : List.of();

        return new CarResponseDTO(
                car.getCarId(),
                car.getModel() != null ? car.getModel().getModelId() : null,
                car.getModel() != null ? car.getModel().getModelName() : null,
                car.getSeries() != null ? car.getSeries().getSeriesId() : null,
                car.getSeries() != null ? car.getSeries().getSeriesName() : null,
                colorDTOs,
                car.getCarName(),
                car.getPrice(),
                car.getYearOfManufacture(),
                car.getStatus(),
                imageDTOs,
                comfortDTO,
                safetyDTO,
                powertrainDTO,
                evSpecsDTO,
                warrantyDTO,
                dimsDTO,
                specsDTO,
                optionDTOs
        );
    }

    private CarImageDTO convertImageToDTO(CarImage carImage) {
        return new CarImageDTO(carImage.getImageId(), carImage.getImageUrl(),
                carImage.getIsPrimary(), carImage.getSortOrder());
    }

    private void validateCarCreateDTO(CarCreateDTO createDTO) {
        Long firstColorId = createDTO.getColorId() != null && !createDTO.getColorId().isEmpty()
                ? createDTO.getColorId().get(0) : null;
        carRepository.findByModelAndSeriesAndColorAndName(
                createDTO.getModelId(),
                createDTO.getSeriesId(),
                firstColorId,
                createDTO.getCarName()
        ).ifPresent(car -> {
            throw new ValidationException("Xe với thông tin này đã tồn tại");
        });
    }

    private void validateCarUpdateDTO(CarUpdateDTO updateDTO) {
        if (updateDTO.getCarImages() != null && updateDTO.getCarImages().size() > 6) {
            throw new ValidationException("Không được thêm quá 6 ảnh cho một chiếc xe");
        }
    }

    private void addCarImages(Car car, List<CarImageDTO> imageDTOs) {
        if (imageDTOs.size() > 6) throw new ValidationException("Không được thêm quá 6 ảnh");
        List<CarImage> carImages = imageDTOs.stream().map(imageDTO -> {
            CarImage carImage = new CarImage();
            carImage.setCar(car);
            carImage.setImageUrl(imageDTO.getImageUrl());
            carImage.setIsPrimary(imageDTO.getIsPrimary() != null ? imageDTO.getIsPrimary() : false);
            carImage.setSortOrder(imageDTO.getSortOrder() != null ? imageDTO.getSortOrder() : 0);
            return carImage;
        }).collect(Collectors.toList());
        carImageRepository.saveAll(carImages);
    }

    private void saveImages(Car car, List<MultipartFile> images) {
    int index = 0;
    for (MultipartFile file : images) {
        try {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new ValidationException("Chỉ được upload file ảnh");
            }

            Map<String, Object> result = cloudinaryService.upload(file, "vinsystem/cars");

            String imageUrl = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");

            CarImage carImage = new CarImage();
            carImage.setCar(car);
            carImage.setImageUrl(imageUrl);
            carImage.setPublicId(publicId);
            carImage.setIsPrimary(index == 0);
            carImage.setSortOrder(index);
            carImageRepository.save(carImage);
            index++;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi upload ảnh", e);
        }
    }
}

    // =========================================================
    // DELETE IMAGES
    // =========================================================
    /**
     * Xóa tất cả ảnh của một chiếc xe (có thể gọi riêng từ endpoint)
     */
    public void deleteAllCarImages(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));
        
        deleteCarImageFiles(carId);
        carImageRepository.deleteByCar_CarId(carId);
    }
}