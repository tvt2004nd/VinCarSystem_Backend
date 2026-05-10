package com.vin.VinSystem.Car.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vin.VinSystem.Car.DTO.CarSafetyCreateDTO;
import com.vin.VinSystem.Car.DTO.CarSafetyResponseDTO;
import com.vin.VinSystem.Car.Service.CarSafetyService;
import com.vin.VinSystem.Car.Controller.exception.ValidationException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cars/{carId}/safety")
public class CarSafetyController {

    private final CarSafetyService carSafetyService;

    public CarSafetyController(CarSafetyService carSafetyService) {
        this.carSafetyService = carSafetyService;
    }

    /**
     * CREATE/UPDATE: Thêm hoặc cập nhật Safety cho xe
     * POST /api/cars/{carId}/safety
     */
    @PostMapping
    public ResponseEntity<CarSafetyResponseDTO> addOrUpdateCarSafety(@PathVariable Long carId,
                                                                     @Valid @RequestBody CarSafetyCreateDTO safetyDTO,
                                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new ValidationException(errorMessage);
        }

        CarSafetyResponseDTO responseDTO = carSafetyService.addOrUpdateCarSafety(carId, safetyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * READ: Lấy Safety theo CarId
     * GET /api/cars/{carId}/safety
     */
    @GetMapping
    public ResponseEntity<CarSafetyResponseDTO> getCarSafety(@PathVariable Long carId) {
        CarSafetyResponseDTO responseDTO = carSafetyService.getCarSafety(carId);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * DELETE: Xóa Safety theo CarId
     * DELETE /api/cars/{carId}/safety
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteCarSafety(@PathVariable Long carId) {
        carSafetyService.deleteCarSafety(carId);
        return ResponseEntity.noContent().build();
    }
}

