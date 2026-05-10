package com.vin.VinSystem.Car.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vin.VinSystem.Car.DTO.ModelColorOptionCreateDTO;
import com.vin.VinSystem.Car.DTO.ModelColorOptionResponseDTO;
import com.vin.VinSystem.Car.Entity.CarColor;
import com.vin.VinSystem.Car.Entity.Model;
import com.vin.VinSystem.Car.Entity.ModelColorOption;
import com.vin.VinSystem.Car.Entity.ModelColorOptionId;
import com.vin.VinSystem.Car.Repository.ModelColorOptionRepository;
import com.vin.VinSystem.Car.Controller.exception.ResourceNotFoundException;

@Service
@Transactional
public class ModelColorOptionService {

    private final ModelColorOptionRepository repository;
    private final ModelService modelService;
    private final CarColorService colorService;

    public ModelColorOptionService(ModelColorOptionRepository repository,
                                   ModelService modelService,
                                   CarColorService colorService) {
        this.repository = repository;
        this.modelService = modelService;
        this.colorService = colorService;
    }

    public ModelColorOptionResponseDTO addOrUpdateOption(ModelColorOptionCreateDTO dto) {
        Model model = modelService.getModelById(dto.getModelId());
        CarColor color = colorService.getColorById(dto.getColorId());

        ModelColorOptionId id = new ModelColorOptionId(dto.getModelId(), dto.getColorId());
        ModelColorOption option = repository.findById(id).orElse(new ModelColorOption());
        option.setId(id);
        option.setModel(model);
        option.setColor(color);
        option.setExtraPrice(dto.getExtraPrice() != null ? dto.getExtraPrice() : BigDecimal.ZERO);

        ModelColorOption saved = repository.save(option);
        return convertToResponseDTO(saved);
    }

    public ModelColorOptionResponseDTO getOption(Long modelId, Long colorId) {
        ModelColorOptionId id = new ModelColorOptionId(modelId, colorId);
        ModelColorOption option = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy option cho modelId=" + modelId + ", colorId=" + colorId));
        return convertToResponseDTO(option);
    }

    /**
     * Batch load all model-color options for given model and color IDs
     * Prevents N+1 query problem
     */
    @Transactional(readOnly = true)
    public Map<String, ModelColorOptionResponseDTO> batchLoadOptions(List<Long> modelIds, List<Long> colorIds) {
        if (modelIds == null || colorIds == null || modelIds.isEmpty() || colorIds.isEmpty()) {
            return new HashMap<>();
        }

        // Batch load all options for given model and color IDs
        List<ModelColorOption> options = repository.findAllByModelColorIds(modelIds, colorIds);

        // Convert to map with key "modelId_colorId"
        Map<String, ModelColorOptionResponseDTO> result = new HashMap<>();
        for (ModelColorOption mco : options) {
            String key = mco.getId().getModelId() + "_" + mco.getId().getColorId();
            result.put(key, convertToResponseDTO(mco));
        }
        return result;
    }

    /**
     * Get option for single model-color pair, with fallback to default (zero extra price)
     */
    @Transactional(readOnly = true)
    public ModelColorOptionResponseDTO getOptionWithDefault(Long modelId, Long colorId) {
        ModelColorOptionId id = new ModelColorOptionId(modelId, colorId);
        return repository.findById(id)
                .map(this::convertToResponseDTO)
                .orElse(new ModelColorOptionResponseDTO(modelId, colorId, BigDecimal.ZERO));
    }

    public void deleteOption(Long modelId, Long colorId) {
        ModelColorOptionId id = new ModelColorOptionId(modelId, colorId);
        repository.deleteById(id);
    }

    public void ensureOptionExists(Long modelId, Long colorId) {
        ModelColorOptionId id = new ModelColorOptionId(modelId, colorId);
        if (!repository.existsById(id)) {
            Model model = modelService.getModelById(modelId);
            CarColor color = colorService.getColorById(colorId);
            ModelColorOption option = new ModelColorOption();
            option.setId(id);
            option.setModel(model);
            option.setColor(color);
            option.setExtraPrice(BigDecimal.ZERO);
            repository.save(option);
        }
    }

    private ModelColorOptionResponseDTO convertToResponseDTO(ModelColorOption option) {
        return new ModelColorOptionResponseDTO(
                option.getModel().getModelId(),
                option.getColor().getColorId(),
                option.getExtraPrice()
        );
    }
}