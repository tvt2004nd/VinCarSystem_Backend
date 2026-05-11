package com.vin.VinSystem.Car.Controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vin.VinSystem.Car.DTO.CarPowertrainCreateDTO;
import com.vin.VinSystem.Car.DTO.CarPowertrainResponseDTO;
import com.vin.VinSystem.Car.Service.CarPowertrainService;
import com.vin.VinSystem.Car.Controller.exception.ValidationException;
import com.vin.VinSystem.Common.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cars/{carId}/powertrain")
public class CarPowertrainController {

    private final CarPowertrainService carPowertrainService;

    public CarPowertrainController(CarPowertrainService carPowertrainService) {
        this.carPowertrainService = carPowertrainService;
    }

    /**
     * CREATE/UPDATE: Thêm hoặc cập nhật Powertrain cho xe
     * POST /api/cars/{carId}/powertrain
     */
    @PostMapping
    public ApiResponse<CarPowertrainResponseDTO> addOrUpdateCarPowertrain(@PathVariable Long carId,
                                                                          @Valid @RequestBody CarPowertrainCreateDTO powertrainDTO,
                                                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new ValidationException(errorMessage);
        }

        CarPowertrainResponseDTO responseDTO = carPowertrainService.addOrUpdateCarPowertrain(carId, powertrainDTO);
        return ApiResponse.success(responseDTO, "Cập nhật hệ truyền động thành công");
    }

    /**
     * READ: Lấy Powertrain theo CarId
     * GET /api/cars/{carId}/powertrain
     */
    @GetMapping
    public ApiResponse<CarPowertrainResponseDTO> getCarPowertrain(@PathVariable Long carId) {
        CarPowertrainResponseDTO responseDTO = carPowertrainService.getCarPowertrain(carId);
        return ApiResponse.success(responseDTO);
    }

    /**
     * DELETE: Xóa Powertrain theo CarId
     * DELETE /api/cars/{carId}/powertrain
     */
    @DeleteMapping
    public ApiResponse<Void> deleteCarPowertrain(@PathVariable Long carId) {
        carPowertrainService.deleteCarPowertrain(carId);
        return ApiResponse.success(null, "Xóa hệ truyền động thành công");
    }
}

