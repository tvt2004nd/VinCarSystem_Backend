package com.vin.VinSystem.Car.Service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vin.VinSystem.Car.Controller.exception.ResourceNotFoundException;
import com.vin.VinSystem.Car.Controller.exception.ValidationException;
import com.vin.VinSystem.Car.DTO.ModelCreateDTO;
import com.vin.VinSystem.Car.DTO.ModelResponseDTO;
import com.vin.VinSystem.Car.Entity.Model;
import com.vin.VinSystem.Car.Repository.CarRepository;
import com.vin.VinSystem.Car.Repository.ModelRepository;

class ModelServiceTest {

    ModelRepository modelRepository = mock(ModelRepository.class);
    CarRepository   carRepository   = mock(CarRepository.class);
    ModelService    service;

    @BeforeEach
    void setUp() {
        service = new ModelService(modelRepository, carRepository);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Model mockModel() {
        Model m = new Model();
        m.setModelId(1L);
        m.setModelName("VF8");
        m.setSegment("SUV");
        return m;
    }

    private ModelCreateDTO validRequest() {
        ModelCreateDTO dto = new ModelCreateDTO();
        dto.setModelName("VF8");
        dto.setSegment("SUV");
        return dto;
    }

    // ── createModel ───────────────────────────────────────────────────────

    @Test
    void createModel_success() {
        when(modelRepository.findByModelNameIgnoreCase("VF8")).thenReturn(Optional.empty());
        when(modelRepository.save(any())).thenReturn(mockModel());

        ModelResponseDTO res = service.createModel(validRequest());

        assertThat(res.getModelName()).isEqualTo("VF8");
        assertThat(res.getSegment()).isEqualTo("SUV");
        verify(modelRepository).save(any());
    }

    @Test
    void createModel_fail_duplicateName() {
        when(modelRepository.findByModelNameIgnoreCase("VF8"))
            .thenReturn(Optional.of(mockModel()));

        assertThatThrownBy(() -> service.createModel(validRequest()))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("đã tồn tại");
    }

    // ── getAllModels ───────────────────────────────────────────────────────

    @Test
    void getAllModels_success() {
        when(modelRepository.findAll()).thenReturn(List.of(mockModel()));

        List<ModelResponseDTO> result = service.getAllModels();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getModelName()).isEqualTo("VF8");
    }

    @Test
    void getAllModels_empty() {
        when(modelRepository.findAll()).thenReturn(List.of());

        assertThat(service.getAllModels()).isEmpty();
    }

    // ── getModelById ──────────────────────────────────────────────────────

    @Test
    void getModelById_found() {
        when(modelRepository.findById(1L)).thenReturn(Optional.of(mockModel()));

        Model result = service.getModelById(1L);

        assertThat(result.getModelId()).isEqualTo(1L);
        assertThat(result.getSegment()).isEqualTo("SUV");
    }

    @Test
    void getModelById_notFound() {
        when(modelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getModelById(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── getModelDetailById ────────────────────────────────────────────────

    @Test
    void getModelDetailById_success() {
        when(modelRepository.findById(1L)).thenReturn(Optional.of(mockModel()));

        ModelResponseDTO res = service.getModelDetailById(1L);

        assertThat(res.getModelId()).isEqualTo(1L);
        assertThat(res.getModelName()).isEqualTo("VF8");
    }

    @Test
    void getModelDetailById_notFound() {
        when(modelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getModelDetailById(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── deleteModel ───────────────────────────────────────────────────────

    @Test
    void deleteModel_success() {
        Model model = mockModel();
        when(modelRepository.findById(1L)).thenReturn(Optional.of(model));
        when(carRepository.existsByModel_ModelId(1L)).thenReturn(false);

        assertThatNoException().isThrownBy(() -> service.deleteModel(1L));

        verify(modelRepository).delete(model);
    }

    @Test
    void deleteModel_fail_hasLinkedCars() {
        when(modelRepository.findById(1L)).thenReturn(Optional.of(mockModel()));
        when(carRepository.existsByModel_ModelId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.deleteModel(1L))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("đang có xe");

        verify(modelRepository, never()).delete(any());
    }

    @Test
    void deleteModel_fail_notFound() {
        when(modelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteModel(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }
}