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

import com.vin.VinSystem.Car.DTO.CarDimensionsCreateDTO;
import com.vin.VinSystem.Car.DTO.CarDimensionsResponseDTO;
import com.vin.VinSystem.Car.Service.CarDimensionsService;
import com.vin.VinSystem.Car.Controller.exception.ValidationException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cars/{carId}/dimensions")
public class CarDimensionsController {

    private final CarDimensionsService carDimensionsService;

    public CarDimensionsController(CarDimensionsService carDimensionsService) {
        this.carDimensionsService = carDimensionsService;
    }

    @PostMapping
    public ResponseEntity<CarDimensionsResponseDTO> addOrUpdate(
            @PathVariable Long carId,
            @Valid @RequestBody CarDimensionsCreateDTO dto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String error = bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new ValidationException(error);
        }
        CarDimensionsResponseDTO response = carDimensionsService.addOrUpdateCarDimensions(carId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<CarDimensionsResponseDTO> get(@PathVariable Long carId) {
        return ResponseEntity.ok(carDimensionsService.getCarDimensions(carId));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable Long carId) {
        carDimensionsService.deleteCarDimensions(carId);
        return ResponseEntity.noContent().build();
    }
}
