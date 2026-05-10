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

import com.vin.VinSystem.Car.DTO.CarComfortCreateDTO;
import com.vin.VinSystem.Car.DTO.CarComfortResponseDTO;
import com.vin.VinSystem.Car.Service.CarComfortService;
import com.vin.VinSystem.Car.Controller.exception.ValidationException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cars/{carId}/comfort")
public class CarComfortController {

    private final CarComfortService carComfortService;

    public CarComfortController(CarComfortService carComfortService) {
        this.carComfortService = carComfortService;
    }

    /**
     * CREATE/UPDATE: Thêm hoặc cập nhật Comfort cho xe
     * POST /api/cars/{carId}/comfort
     */
    @PostMapping
    public ResponseEntity<CarComfortResponseDTO> addOrUpdateCarComfort(@PathVariable Long carId,
                                                                       @Valid @RequestBody CarComfortCreateDTO comfortDTO,
                                                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new ValidationException(errorMessage);
        }

        CarComfortResponseDTO responseDTO = carComfortService.addOrUpdateCarComfort(carId, comfortDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * READ: Lấy Comfort theo CarId
     * GET /api/cars/{carId}/comfort
     */
    @GetMapping
    public ResponseEntity<CarComfortResponseDTO> getCarComfort(@PathVariable Long carId) {
        CarComfortResponseDTO responseDTO = carComfortService.getCarComfort(carId);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * DELETE: Xóa Comfort theo CarId
     * DELETE /api/cars/{carId}/comfort
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteCarComfort(@PathVariable Long carId) {
        carComfortService.deleteCarComfort(carId);
        return ResponseEntity.noContent().build();
    }
}

