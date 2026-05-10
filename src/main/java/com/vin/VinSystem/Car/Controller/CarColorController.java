package com.vin.VinSystem.Car.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CarColorResponseDTO> createColor(@Valid @RequestBody CarColorCreateDTO createDTO,
                                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            throw new ValidationException(errorMessage);
        }

        CarColorResponseDTO responseDTO = carColorService.createColor(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * READ: Lấy tất cả màu xe
     * GET /api/colors
     */
    @GetMapping
    public ResponseEntity<List<CarColorResponseDTO>> getAllColors() {
        List<CarColorResponseDTO> colors = carColorService.getAllColors();
        return ResponseEntity.ok(colors);
    }

    /**
     * READ: Lấy màu theo ID
     * GET /api/colors/{colorId}
     */
    @GetMapping("/{colorId}")
    public ResponseEntity<CarColorResponseDTO> getColorById(@PathVariable Long colorId) {
        CarColorResponseDTO responseDTO = carColorService.getColorDetailById(colorId);
        return ResponseEntity.ok(responseDTO);
    }
}

