package com.vin.VinSystem.Car.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vin.VinSystem.Car.Controller.exception.ResourceNotFoundException;
import com.vin.VinSystem.Car.Controller.exception.ValidationException;
import com.vin.VinSystem.Car.DTO.CarColorCreateDTO;
import com.vin.VinSystem.Car.DTO.CarColorResponseDTO;
import com.vin.VinSystem.Car.Entity.CarColor;
import com.vin.VinSystem.Car.Repository.CarColorRepository;

@Service
@Transactional
public class CarColorService {

    private final CarColorRepository carColorRepository;

    public CarColorService(CarColorRepository carColorRepository) {
        this.carColorRepository = carColorRepository;
    }

    /**
     * CREATE: Tạo mới màu xe
     */
    public CarColorResponseDTO createColor(CarColorCreateDTO createDTO) {
        carColorRepository.findByColorNameIgnoreCase(createDTO.getColorName())
                .ifPresent(existing -> {
                    throw new ValidationException("Màu với tên này đã tồn tại");
                });

        if (createDTO.getColorCode() != null && !createDTO.getColorCode().isBlank()) {
            carColorRepository.findByColorCode(createDTO.getColorCode())
                    .ifPresent(existing -> {
                        throw new ValidationException("Mã màu đã tồn tại");
                    });
        }

        CarColor color = new CarColor();
        color.setColorName(createDTO.getColorName());
        color.setColorCode(createDTO.getColorCode());

        CarColor savedColor = carColorRepository.save(color);
        return convertToResponseDTO(savedColor);
    }

    /**
     * READ: Lấy tất cả màu xe
     */
    public List<CarColorResponseDTO> getAllColors() {
        return carColorRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * READ: Lấy màu theo ID (Entity dùng nội bộ)
     */
    public CarColor getColorById(Long colorId) {
        return carColorRepository.findById(colorId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy màu với ID: " + colorId));
    }

    /**
     * READ: Lấy màu theo ID (DTO trả về client)
     */
    public CarColorResponseDTO getColorDetailById(Long colorId) {
        CarColor color = getColorById(colorId);
        return convertToResponseDTO(color);
    }

    private CarColorResponseDTO convertToResponseDTO(CarColor color) {
        return new CarColorResponseDTO(
                color.getColorId(),
                color.getColorName(),
                color.getColorCode()
        );
    }
}
