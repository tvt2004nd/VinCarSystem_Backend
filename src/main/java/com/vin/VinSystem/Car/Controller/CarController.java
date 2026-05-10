package com.vin.VinSystem.Car.Controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vin.VinSystem.Car.DTO.CarCreateDTO;
import com.vin.VinSystem.Car.DTO.CarResponseDTO;
import com.vin.VinSystem.Car.DTO.CarUpdateDTO;
import com.vin.VinSystem.Car.Controller.exception.ValidationException;
import com.vin.VinSystem.Car.Service.CarService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    /**
     * CREATE: Tạo xe mới
     * POST /api/cars
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CarResponseDTO> createCar(
            @RequestPart("data") String data,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        CarCreateDTO createDTO = mapper.readValue(data, CarCreateDTO.class);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(carService.createCarWithImages(createDTO, images));
    }
    /**
     * READ: Lấy tất cả xe
     * GET /api/cars
     */
    @GetMapping
    public ResponseEntity<List<CarResponseDTO>> getAllCars() {
        List<CarResponseDTO> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    /**
     * READ: Lấy các xe đang hoạt động
     * GET /api/cars/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<CarResponseDTO>> getAllActiveCars() {
        List<CarResponseDTO> cars = carService.getAllActiveCars();
        return ResponseEntity.ok(cars);
    }

    /**
     * READ: Lấy xe theo ID
     * GET /api/cars/{carId}
     */
    @GetMapping("/{carId}")
    public ResponseEntity<CarResponseDTO> getCarById(@PathVariable Long carId) {
        CarResponseDTO carResponseDTO = carService.getCarById(carId);
        return ResponseEntity.ok(carResponseDTO);
    }

    /**
     * READ: Tìm xe theo Model ID
     * GET /api/cars/by-model/{modelId}
     */
    @GetMapping("/by-model/{modelId}")
    public ResponseEntity<List<CarResponseDTO>> getCarsByModelId(@PathVariable Long modelId) {
        List<CarResponseDTO> cars = carService.getCarsByModelId(modelId);
        return ResponseEntity.ok(cars);
    }

    /**
     * READ: Tìm xe theo Series ID
     * GET /api/cars/by-series/{seriesId}
     */
    @GetMapping("/by-series/{seriesId}")
    public ResponseEntity<List<CarResponseDTO>> getCarsBySeriesId(@PathVariable Long seriesId) {
        List<CarResponseDTO> cars = carService.getCarsBySeriesId(seriesId);
        return ResponseEntity.ok(cars);
    }

    /**
     * READ: Tìm xe theo Color ID
     * GET /api/cars/by-color/{colorId}
     */
    @GetMapping("/by-color/{colorId}")
    public ResponseEntity<List<CarResponseDTO>> getCarsByColorId(@PathVariable Long colorId) {
        List<CarResponseDTO> cars = carService.getCarsByColorId(colorId);
        return ResponseEntity.ok(cars);
    }

    /**
     * READ: Tìm xe theo Status
     * GET /api/cars/by-status?status=ACTIVE
     */
    @GetMapping("/by-status")
    public ResponseEntity<List<CarResponseDTO>> getCarsByStatus(@RequestParam String status) {
        List<CarResponseDTO> cars = carService.getCarsByStatus(status);
        return ResponseEntity.ok(cars);
    }

    /**
     * READ: Tìm xe theo tên
     * GET /api/cars/search?name=Toyota
     */
    @GetMapping("/search")
    public ResponseEntity<List<CarResponseDTO>> searchCarByName(@RequestParam String name) {
        List<CarResponseDTO> cars = carService.searchCarByName(name);
        return ResponseEntity.ok(cars);
    }

    /**
     * UPDATE: Cập nhật thông tin xe
     * PUT /api/cars/{carId}
     */

    @PutMapping(value = "/{carId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<CarResponseDTO> updateCar(
                @PathVariable Long carId,
                @RequestPart("data") String data,
                @RequestPart(value = "images", required = false) List<MultipartFile> images
         ) throws Exception {

            ObjectMapper mapper = new ObjectMapper();
            CarUpdateDTO updateDTO = mapper.readValue(data, CarUpdateDTO.class);

            // Kiểm tra carId khớp nhau
            if (!updateDTO.getCarId().equals(carId)) {
                throw new ValidationException("Car ID trong URL và request body không khớp");
            }

            CarResponseDTO carResponseDTO = carService.updateCarWithImages(updateDTO, images);
            return ResponseEntity.ok(carResponseDTO);
    }

    /**
     * DELETE: Xóa xe
     * DELETE /api/cars/{carId}
     */
    @DeleteMapping("/{carId}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long carId) {
        carService.deleteCar(carId);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE: Xóa tất cả ảnh của một chiếc xe
     * DELETE /api/cars/{carId}/images
     */
    @DeleteMapping("/{carId}/images")
    public ResponseEntity<Void> deleteCarImages(@PathVariable Long carId) {
        carService.deleteAllCarImages(carId);
        return ResponseEntity.noContent().build();
    }
}