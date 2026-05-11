package com.vin.VinSystem.Car.Controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vin.VinSystem.Car.DTO.SpecsCommonCreateDTO;
import com.vin.VinSystem.Car.DTO.SpecsCommonResponseDTO;
import com.vin.VinSystem.Car.Service.SpecsCommonService;
import com.vin.VinSystem.Car.Controller.exception.ValidationException;
import com.vin.VinSystem.Common.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cars/{carId}/specs")
public class SpecsCommonController {
    private final SpecsCommonService specsCommonService;

    public SpecsCommonController(SpecsCommonService specsCommonService) {
        this.specsCommonService = specsCommonService;
    }

    @PostMapping
    public ApiResponse<SpecsCommonResponseDTO> addOrUpdate(
            @PathVariable Long carId,
            @Valid @RequestBody SpecsCommonCreateDTO dto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        SpecsCommonResponseDTO response = specsCommonService.addOrUpdateSpecsCommon(carId, dto);
        return ApiResponse.success(response, "Cập nhật thông số thành công");
    }

    @GetMapping
    public ApiResponse<SpecsCommonResponseDTO> get(@PathVariable Long carId) {
        return ApiResponse.success(specsCommonService.getSpecsCommon(carId));
    }

    @DeleteMapping
    public ApiResponse<Void> delete(@PathVariable Long carId) {
        specsCommonService.deleteSpecsCommon(carId);
        return ApiResponse.success(null, "Xóa thông số thành công");
    }
}
