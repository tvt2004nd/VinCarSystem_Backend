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

import com.vin.VinSystem.Car.DTO.ModelColorOptionCreateDTO;
import com.vin.VinSystem.Car.DTO.ModelColorOptionResponseDTO;
import com.vin.VinSystem.Car.Service.ModelColorOptionService;
import com.vin.VinSystem.Car.Controller.exception.ValidationException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/model-color-options")
public class ModelColorOptionController {

    private final ModelColorOptionService optionService;

    public ModelColorOptionController(ModelColorOptionService optionService) {
        this.optionService = optionService;
    }

    @PostMapping
    public ResponseEntity<ModelColorOptionResponseDTO> createOrUpdate(
            @Valid @RequestBody ModelColorOptionCreateDTO dto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        ModelColorOptionResponseDTO response = optionService.addOrUpdateOption(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{modelId}/{colorId}")
    public ResponseEntity<ModelColorOptionResponseDTO> get(@PathVariable Long modelId,
                                                            @PathVariable Long colorId) {
        return ResponseEntity.ok(optionService.getOption(modelId, colorId));
    }

    @DeleteMapping("/{modelId}/{colorId}")
    public ResponseEntity<Void> delete(@PathVariable Long modelId,
                                       @PathVariable Long colorId) {
        optionService.deleteOption(modelId, colorId);
        return ResponseEntity.noContent().build();
    }
}