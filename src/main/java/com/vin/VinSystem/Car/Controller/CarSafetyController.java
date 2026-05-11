package com.vin.VinSystem.Car.Controller;

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
import com.vin.VinSystem.Common.ApiResponse;

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
    public ApiResponse<CarSafetyResponseDTO> addOrUpdateCarSafety(@PathVariable Long carId,
                                                                  @Valid @RequestBody CarSafetyCreateDTO safetyDTO,
                                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new ValidationException(errorMessage);
        }

        CarSafetyResponseDTO responseDTO = carSafetyService.addOrUpdateCarSafety(carId, safetyDTO);
        return ApiResponse.success(responseDTO, "Cập nhật an toàn thành công");
    }

    /**
     * READ: Lấy Safety theo CarId
     * GET /api/cars/{carId}/safety
     */
    @GetMapping
    public ApiResponse<CarSafetyResponseDTO> getCarSafety(@PathVariable Long carId) {
        CarSafetyResponseDTO responseDTO = carSafetyService.getCarSafety(carId);
        return ApiResponse.success(responseDTO);
    }

    /**
     * DELETE: Xóa Safety theo CarId
     * DELETE /api/cars/{carId}/safety
     */
    @DeleteMapping
    public ApiResponse<Void> deleteCarSafety(@PathVariable Long carId) {
        carSafetyService.deleteCarSafety(carId);
        return ApiResponse.success(null, "Xóa an toàn thành công");
    }
}

