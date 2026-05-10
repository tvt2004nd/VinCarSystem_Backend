package com.vin.VinSystem.Car.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vin.VinSystem.Car.Controller.exception.ResourceNotFoundException;
import com.vin.VinSystem.Car.Controller.exception.ValidationException;
import com.vin.VinSystem.Car.DTO.CarSeriesCreateDTO;
import com.vin.VinSystem.Car.DTO.CarSeriesResponseDTO;
import com.vin.VinSystem.Car.Entity.CarSeries;
import com.vin.VinSystem.Car.Entity.Model;
import com.vin.VinSystem.Car.Repository.CarRepository;
import com.vin.VinSystem.Car.Repository.CarSeriesRepository;

@Service
@Transactional
public class CarSeriesService {

    private final CarSeriesRepository carSeriesRepository;
    private final ModelService modelService;
    private final CarRepository carRepository;

    public CarSeriesService(CarSeriesRepository carSeriesRepository, ModelService modelService,CarRepository carRepository) {
        this.carSeriesRepository = carSeriesRepository;
        this.modelService = modelService;
        this.carRepository=carRepository;
    }

    /**
     * CREATE: Tạo mới series
     */
    public CarSeriesResponseDTO createSeries(CarSeriesCreateDTO createDTO) {
        if (createDTO.getSeriesCode() != null && !createDTO.getSeriesCode().isBlank()) {
            if (carSeriesRepository.existsBySeriesCode(createDTO.getSeriesCode())) {
                throw new ValidationException("Series code đã tồn tại");
            }
        }

        carSeriesRepository.findBySeriesNameIgnoreCase(createDTO.getSeriesName())
                .ifPresent(existing -> {
                    throw new ValidationException("Series với tên này đã tồn tại");
                });

        Model model = modelService.getModelById(createDTO.getModelId());

        CarSeries series = new CarSeries();
        series.setModel(model);
        series.setSeriesCode(createDTO.getSeriesCode());
        series.setSeriesName(createDTO.getSeriesName());
        series.setDescription(createDTO.getDescription());
        series.setSortOrder(createDTO.getSortOrder());
        series.setStatus(createDTO.getStatus());

        CarSeries savedSeries = carSeriesRepository.save(series);
        return convertToResponseDTO(savedSeries);
    }

    /**
     * READ: Lấy tất cả series
     */
    public List<CarSeriesResponseDTO> getAllSeries() {
        return carSeriesRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * READ: Lấy series theo ID (Entity dùng nội bộ)
     */
    public CarSeries getSeriesById(Long seriesId) {
        return carSeriesRepository.findById(seriesId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy series với ID: " + seriesId));
    }

    /**
     * READ: Lấy series theo ID (DTO trả về client)
     */
    public CarSeriesResponseDTO getSeriesDetailById(Long seriesId) {
        CarSeries series = getSeriesById(seriesId);
        return convertToResponseDTO(series);
    }

    /**
     * DELETE: Xóa series theo ID
     */
public void deleteSeries(Long seriesId) {
    CarSeries series = getSeriesById(seriesId);
    if (carRepository.existsBySeries_SeriesId(seriesId)) {
        throw new ValidationException("Không thể xóa series đang có xe liên kết");
    }
    carSeriesRepository.delete(series);
}

    private CarSeriesResponseDTO convertToResponseDTO(CarSeries series) {
        return new CarSeriesResponseDTO(
                series.getSeriesId(),
                series.getModel() != null ? series.getModel().getModelId() : null,
                series.getSeriesCode(),
                series.getSeriesName(),
                series.getDescription(),
                series.getSortOrder(),
                series.getStatus()
        );
    }
}
