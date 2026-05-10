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

import com.vin.VinSystem.Car.DTO.CarWarrantyCreateDTO;
import com.vin.VinSystem.Car.DTO.CarWarrantyResponseDTO;
import com.vin.VinSystem.Car.Service.CarWarrantyService;
import com.vin.VinSystem.Car.Controller.exception.ValidationException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cars/{carId}/warranty")
public class CarWarrantyController {

    private final CarWarrantyService carWarrantyService;

    public CarWarrantyController(CarWarrantyService carWarrantyService) {
        this.carWarrantyService = carWarrantyService;
    }

    /**
     * CREATE/UPDATE: Thêm hoặc cập nhật Warranty cho xe
     * POST /api/cars/{carId}/warranty
     */
    @PostMapping
    public ResponseEntity<CarWarrantyResponseDTO> addOrUpdateCarWarranty(@PathVariable Long carId,
                                                                         @Valid @RequestBody CarWarrantyCreateDTO warrantyDTO,
                                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new ValidationException(errorMessage);
        }

        CarWarrantyResponseDTO responseDTO = carWarrantyService.addOrUpdateCarWarranty(carId, warrantyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * READ: Lấy Warranty theo CarId
     * GET /api/cars/{carId}/warranty
     */
    @GetMapping
    public ResponseEntity<CarWarrantyResponseDTO> getCarWarranty(@PathVariable Long carId) {
        CarWarrantyResponseDTO responseDTO = carWarrantyService.getCarWarranty(carId);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * DELETE: Xóa Warranty theo CarId
     * DELETE /api/cars/{carId}/warranty
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteCarWarranty(@PathVariable Long carId) {
        carWarrantyService.deleteCarWarranty(carId);
        return ResponseEntity.noContent().build();
    }
}

