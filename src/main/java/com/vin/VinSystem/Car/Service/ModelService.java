package com.vin.VinSystem.Car.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vin.VinSystem.Car.DTO.ModelCreateDTO;
import com.vin.VinSystem.Car.DTO.ModelResponseDTO;
import com.vin.VinSystem.Car.Repository.ModelRepository;
import com.vin.VinSystem.Car.Controller.exception.ResourceNotFoundException;
import com.vin.VinSystem.Car.Controller.exception.ValidationException;
import com.vin.VinSystem.Car.Entity.Model;
import com.vin.VinSystem.Car.Repository.CarRepository;

@Service
@Transactional
public class ModelService {

    private final ModelRepository modelRepository;
    private final CarRepository carRepository;

    public ModelService(ModelRepository modelRepository,CarRepository carRepository) {
        this.modelRepository = modelRepository;
        this.carRepository=carRepository;
    }

    /**
     * CREATE: Tạo mới model
     */
    public ModelResponseDTO createModel(ModelCreateDTO createDTO) {
        modelRepository.findByModelNameIgnoreCase(createDTO.getModelName())
                .ifPresent(existing -> {
                    throw new ValidationException("Model với tên này đã tồn tại");
                });

        Model model = new Model();
        model.setModelName(createDTO.getModelName());
        model.setSegment(createDTO.getSegment());

        Model savedModel = modelRepository.save(model);
        return convertToResponseDTO(savedModel);
    }

    /**
     * READ: Lấy danh sách tất cả model
     */
    public List<ModelResponseDTO> getAllModels() {
        return modelRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * READ: Lấy model theo ID (trả về Entity để dùng nội bộ)
     */
    public Model getModelById(Long modelId) {
        return modelRepository.findById(modelId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy model với ID: " + modelId));
    }

    /**
     * READ: Lấy model theo ID (trả về DTO cho client)
     */
    public ModelResponseDTO getModelDetailById(Long modelId) {
        Model model = getModelById(modelId);
        return convertToResponseDTO(model);
    }

    /**
     * DELETE: Xóa model theo ID
     */
public void deleteModel(Long modelId) {
    Model model = getModelById(modelId);
    if (carRepository.existsByModel_ModelId(modelId)) {
        throw new ValidationException("Model đang có xe, không thể xóa");
    }
    modelRepository.delete(model);
}

    /**
     * Chuyển đổi Model Entity sang ModelResponseDTO
     */
    private ModelResponseDTO convertToResponseDTO(Model model) {
        return new ModelResponseDTO(
                model.getModelId(),
                model.getModelName(),
                model.getSegment()
        );
    }
}
