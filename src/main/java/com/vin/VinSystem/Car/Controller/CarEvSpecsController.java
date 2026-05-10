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

import com.vin.VinSystem.Car.DTO.CarEvSpecsCreateDTO;
import com.vin.VinSystem.Car.DTO.CarEvSpecsResponseDTO;
import com.vin.VinSystem.Car.Service.CarEvSpecsService;
import com.vin.VinSystem.Car.Controller.exception.ValidationException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cars/{carId}/ev-specs")
public class CarEvSpecsController {

    private final CarEvSpecsService carEvSpecsService;

    public CarEvSpecsController(CarEvSpecsService carEvSpecsService) {
        this.carEvSpecsService = carEvSpecsService;
    }

    /**
     * CREATE/UPDATE: Thêm hoặc cập nhật EV Specs cho xe
     * POST /api/cars/{carId}/ev-specs
     */
    @PostMapping
    public ResponseEntity<CarEvSpecsResponseDTO> addOrUpdateCarEvSpecs(@PathVariable Long carId,
                                                                        @Valid @RequestBody CarEvSpecsCreateDTO evSpecsDTO,
                                                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new ValidationException(errorMessage);
        }

        CarEvSpecsResponseDTO responseDTO = carEvSpecsService.addOrUpdateCarEvSpecs(carId, evSpecsDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * READ: Lấy EV Specs theo CarId
     * GET /api/cars/{carId}/ev-specs
     */
    @GetMapping
    public ResponseEntity<CarEvSpecsResponseDTO> getCarEvSpecs(@PathVariable Long carId) {
        CarEvSpecsResponseDTO responseDTO = carEvSpecsService.getCarEvSpecs(carId);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * DELETE: Xóa EV Specs theo CarId
     * DELETE /api/cars/{carId}/ev-specs
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteCarEvSpecs(@PathVariable Long carId) {
        carEvSpecsService.deleteCarEvSpecs(carId);
        return ResponseEntity.noContent().build();
    }
}

