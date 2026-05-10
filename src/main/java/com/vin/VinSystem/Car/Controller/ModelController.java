package com.vin.VinSystem.Car.Controller;

import java.util.List;

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

import com.vin.VinSystem.Car.DTO.ModelCreateDTO;
import com.vin.VinSystem.Car.DTO.ModelResponseDTO;
import com.vin.VinSystem.Car.Service.ModelService;
import com.vin.VinSystem.Car.Controller.exception.ValidationException;

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
    public ResponseEntity<ModelResponseDTO> createModel(@Valid @RequestBody ModelCreateDTO createDTO,
                                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new ValidationException(errorMessage);
        }

        ModelResponseDTO responseDTO = modelService.createModel(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * READ: Lấy tất cả model
     * GET /api/models
     */
    @GetMapping
    public ResponseEntity<List<ModelResponseDTO>> getAllModels() {
        List<ModelResponseDTO> models = modelService.getAllModels();
        return ResponseEntity.ok(models);
    }

    /**
     * READ: Lấy model theo ID
     * GET /api/models/{modelId}
     */
    @GetMapping("/{modelId}")
    public ResponseEntity<ModelResponseDTO> getModelById(@PathVariable Long modelId) {
        ModelResponseDTO responseDTO = modelService.getModelDetailById(modelId);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * DELETE: Xóa model theo ID
     * DELETE /api/models/{modelId}
     */
    @DeleteMapping("/{modelId}")
    public ResponseEntity<Void> deleteModel(@PathVariable Long modelId) {
        modelService.deleteModel(modelId);
        return ResponseEntity.noContent().build();
    }
}

