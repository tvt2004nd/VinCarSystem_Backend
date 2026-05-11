package com.vin.VinSystem.Car.Controller;

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
import com.vin.VinSystem.Common.ApiResponse;

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
    public ApiResponse<CarWarrantyResponseDTO> addOrUpdateCarWarranty(@PathVariable Long carId,
                                                                      @Valid @RequestBody CarWarrantyCreateDTO warrantyDTO,
                                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new ValidationException(errorMessage);
        }

        CarWarrantyResponseDTO responseDTO = carWarrantyService.addOrUpdateCarWarranty(carId, warrantyDTO);
        return ApiResponse.success(responseDTO, "Cập nhật bảo hành thành công");
    }

    /**
     * READ: Lấy Warranty theo CarId
     * GET /api/cars/{carId}/warranty
     */
    @GetMapping
    public ApiResponse<CarWarrantyResponseDTO> getCarWarranty(@PathVariable Long carId) {
        CarWarrantyResponseDTO responseDTO = carWarrantyService.getCarWarranty(carId);
        return ApiResponse.success(responseDTO);
    }

    /**
     * DELETE: Xóa Warranty theo CarId
     * DELETE /api/cars/{carId}/warranty
     */
    @DeleteMapping
    public ApiResponse<Void> deleteCarWarranty(@PathVariable Long carId) {
        carWarrantyService.deleteCarWarranty(carId);
        return ApiResponse.success(null, "Xóa bảo hành thành công");
    }
}

