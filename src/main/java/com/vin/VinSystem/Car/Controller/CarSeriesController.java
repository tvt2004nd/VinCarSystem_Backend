package com.vin.VinSystem.Car.Controller;

import java.util.List;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vin.VinSystem.Car.DTO.CarSeriesCreateDTO;
import com.vin.VinSystem.Car.DTO.CarSeriesResponseDTO;
import com.vin.VinSystem.Car.Service.CarSeriesService;
import com.vin.VinSystem.Car.Controller.exception.ValidationException;
import com.vin.VinSystem.Common.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/series")
public class CarSeriesController {

    private final CarSeriesService carSeriesService;

    public CarSeriesController(CarSeriesService carSeriesService) {
        this.carSeriesService = carSeriesService;
    }

    /**
     * CREATE: Tạo mới series
     * POST /api/series
     */
    @PostMapping
    public ApiResponse<CarSeriesResponseDTO> createSeries(@Valid @RequestBody CarSeriesCreateDTO createDTO,
                                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new ValidationException(errorMessage);
        }

        CarSeriesResponseDTO responseDTO = carSeriesService.createSeries(createDTO);
        return ApiResponse.success(responseDTO, "Tạo series thành công");
    }

    /**
     * READ: Lấy tất cả series
     * GET /api/series
     */
    @GetMapping
    public ApiResponse<List<CarSeriesResponseDTO>> getAllSeries() {
        List<CarSeriesResponseDTO> seriesList = carSeriesService.getAllSeries();
        return ApiResponse.success(seriesList);
    }

    /**
     * READ: Lấy series theo ID
     * GET /api/series/{seriesId}
     */
    @GetMapping("/{seriesId}")
    public ApiResponse<CarSeriesResponseDTO> getSeriesById(@PathVariable Long seriesId) {
        CarSeriesResponseDTO responseDTO = carSeriesService.getSeriesDetailById(seriesId);
        return ApiResponse.success(responseDTO);
    }

    /**
     * DELETE: Xóa series theo ID
     * DELETE /api/series/{seriesId}
     */
    @DeleteMapping("/{seriesId}")
    public ApiResponse<Void> deleteSeries(@PathVariable Long seriesId) {
        carSeriesService.deleteSeries(seriesId);
        return ApiResponse.success(null, "Xóa series thành công");
    }
}
