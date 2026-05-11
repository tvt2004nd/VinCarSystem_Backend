package com.vin.VinSystem.Car.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vin.VinSystem.Common.ApiResponse;
import com.vin.VinSystem.Car.DTO.*;
import com.vin.VinSystem.Car.Service.CarService;
import com.vin.VinSystem.Car.Controller.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CarResponseDTO> createCar(
            @RequestPart("data") String data,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        CarCreateDTO createDTO = mapper.readValue(data, CarCreateDTO.class);
        CarResponseDTO response = carService.createCarWithImages(createDTO, images);
        return ApiResponse.success(response, "Tạo xe thành công");
    }

    @GetMapping
    public ApiResponse<List<CarResponseDTO>> getAllCars() {
        return ApiResponse.success(carService.getAllCars());
    }

    @GetMapping("/active")
    public ApiResponse<List<CarResponseDTO>> getAllActiveCars() {
        return ApiResponse.success(carService.getAllActiveCars());
    }

    @GetMapping("/{carId}")
    public ApiResponse<CarResponseDTO> getCarById(@PathVariable Long carId) {
        return ApiResponse.success(carService.getCarById(carId));
    }

    @GetMapping("/by-model/{modelId}")
    public ApiResponse<List<CarResponseDTO>> getCarsByModelId(@PathVariable Long modelId) {
        return ApiResponse.success(carService.getCarsByModelId(modelId));
    }

    @GetMapping("/by-series/{seriesId}")
    public ApiResponse<List<CarResponseDTO>> getCarsBySeriesId(@PathVariable Long seriesId) {
        return ApiResponse.success(carService.getCarsBySeriesId(seriesId));
    }

    @GetMapping("/by-color/{colorId}")
    public ApiResponse<List<CarResponseDTO>> getCarsByColorId(@PathVariable Long colorId) {
        return ApiResponse.success(carService.getCarsByColorId(colorId));
    }

    @GetMapping("/by-status")
    public ApiResponse<List<CarResponseDTO>> getCarsByStatus(@RequestParam String status) {
        return ApiResponse.success(carService.getCarsByStatus(status));
    }

    @GetMapping("/search")
    public ApiResponse<List<CarResponseDTO>> searchCarByName(@RequestParam String name) {
        return ApiResponse.success(carService.searchCarByName(name));
    }

    @PutMapping(value = "/{carId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CarResponseDTO> updateCar(
            @PathVariable Long carId,
            @RequestPart("data") String data,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        CarUpdateDTO updateDTO = mapper.readValue(data, CarUpdateDTO.class);

        if (!updateDTO.getCarId().equals(carId)) {
            throw new ValidationException("Car ID không khớp");
        }

        CarResponseDTO response = carService.updateCarWithImages(updateDTO, images);
        return ApiResponse.success(response, "Cập nhật xe thành công");
    }

    @DeleteMapping("/{carId}")
    public ApiResponse<Void> deleteCar(@PathVariable Long carId) {
        carService.deleteCar(carId);
        return ApiResponse.success(null, "Xóa xe thành công");
    }

    @DeleteMapping("/{carId}/images")
    public ApiResponse<Void> deleteCarImages(@PathVariable Long carId) {
        carService.deleteAllCarImages(carId);
        return ApiResponse.success(null, "Xóa toàn bộ ảnh thành công");
    }
}