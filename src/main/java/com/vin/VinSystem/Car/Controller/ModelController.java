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

import com.vin.VinSystem.Car.DTO.ModelCreateDTO;
import com.vin.VinSystem.Car.DTO.ModelResponseDTO;
import com.vin.VinSystem.Car.Service.ModelService;
import com.vin.VinSystem.Car.Controller.exception.ValidationException;
import com.vin.VinSystem.Common.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/models")
public class ModelController {

    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * CREATE: Tạo mới model
     * POST /api/models
     */
    @PostMapping
    public ApiResponse<ModelResponseDTO> createModel(@Valid @RequestBody ModelCreateDTO createDTO,
                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new ValidationException(errorMessage);
        }

        ModelResponseDTO responseDTO = modelService.createModel(createDTO);
        return ApiResponse.success(responseDTO, "Tạo model thành công");
    }

    /**
     * READ: Lấy tất cả model
     * GET /api/models
     */
    @GetMapping
    public ApiResponse<List<ModelResponseDTO>> getAllModels() {
        List<ModelResponseDTO> models = modelService.getAllModels();
        return ApiResponse.success(models);
    }

    /**
     * READ: Lấy model theo ID
     * GET /api/models/{modelId}
     */
    @GetMapping("/{modelId}")
    public ApiResponse<ModelResponseDTO> getModelById(@PathVariable Long modelId) {
        ModelResponseDTO responseDTO = modelService.getModelDetailById(modelId);
        return ApiResponse.success(responseDTO);
    }

    /**
     * DELETE: Xóa model theo ID
     * DELETE /api/models/{modelId}
     */
    @DeleteMapping("/{modelId}")
    public ApiResponse<Void> deleteModel(@PathVariable Long modelId) {
        modelService.deleteModel(modelId);
        return ApiResponse.success(null, "Xóa model thành công");
    }
}

