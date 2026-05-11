package com.vin.VinSystem.Car.Controller;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vin.VinSystem.Car.Controller.exception.ValidationException;
import com.vin.VinSystem.Car.DTO.CarColorCreateDTO;
import com.vin.VinSystem.Car.DTO.CarColorResponseDTO;
import com.vin.VinSystem.Car.Service.CarColorService;
import com.vin.VinSystem.Common.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/colors")
public class CarColorController {

    private final CarColorService carColorService;

    public CarColorController(CarColorService carColorService) {
        this.carColorService = carColorService;
    }

    /**
     * CREATE: Tạo mới màu xe
     * POST /api/colors
     */
    @PostMapping
    public ApiResponse<CarColorResponseDTO> createColor(@Valid @RequestBody CarColorCreateDTO createDTO,
                                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new ValidationException(errorMessage);
        }

        CarColorResponseDTO responseDTO = carColorService.createColor(createDTO);
        return ApiResponse.success(responseDTO, "Tạo màu xe thành công");
    }

    /**
     * READ: Lấy tất cả màu xe
     * GET /api/colors
     */
    @GetMapping
    public ApiResponse<List<CarColorResponseDTO>> getAllColors() {
        List<CarColorResponseDTO> colors = carColorService.getAllColors();
        return ApiResponse.success(colors);
    }

    /**
     * READ: Lấy màu theo ID
     * GET /api/colors/{colorId}
     */
    @GetMapping("/{colorId}")
    public ApiResponse<CarColorResponseDTO> getColorById(@PathVariable Long colorId) {
        CarColorResponseDTO responseDTO = carColorService.getColorDetailById(colorId);
        return ApiResponse.success(responseDTO);
    }
}

