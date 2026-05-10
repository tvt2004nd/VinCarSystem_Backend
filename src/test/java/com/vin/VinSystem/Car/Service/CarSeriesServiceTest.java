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
import com.vin.VinSystem.Car.DTO.CarSeriesCreateDTO;
import com.vin.VinSystem.Car.DTO.CarSeriesResponseDTO;
import com.vin.VinSystem.Car.Entity.CarSeries;
import com.vin.VinSystem.Car.Entity.Model;
import com.vin.VinSystem.Car.Repository.CarRepository;
import com.vin.VinSystem.Car.Repository.CarSeriesRepository;

class CarSeriesServiceTest {

    CarSeriesRepository carSeriesRepository = mock(CarSeriesRepository.class);
    ModelService        modelService        = mock(ModelService.class);
    CarSeriesService    service;
     CarRepository       carRepository       = mock(CarRepository.class); 

    @BeforeEach
    void setUp() {
        service = new CarSeriesService(carSeriesRepository, modelService,carRepository);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Model mockModel() {
        Model m = new Model();
        m.setModelId(1L);
        m.setModelName("VF8");
        return m;
    }
    @Test
void deleteSeries_fail_hasLinkedCars() {
    when(carSeriesRepository.findById(1L)).thenReturn(Optional.of(mockSeries()));
    when(carRepository.existsBySeries_SeriesId(1L)).thenReturn(true);

    assertThatThrownBy(() -> service.deleteSeries(1L))
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("đang có xe");

    verify(carSeriesRepository, never()).delete(any());
}

    private CarSeries mockSeries() {
        CarSeries s = new CarSeries();
        s.setSeriesId(1L);
        s.setSeriesCode("VF8-Plus");
        s.setSeriesName("VF8 Plus");
        s.setDescription("Tiết kiệm điện");
        s.setSortOrder(1);
        s.setStatus("ACTIVE");
        s.setModel(mockModel());
        return s;
    }

    private CarSeriesCreateDTO validRequest() {
        CarSeriesCreateDTO dto = new CarSeriesCreateDTO();
        dto.setSeriesCode("VF8-Plus");
        dto.setSeriesName("VF8 Plus");
        dto.setDescription("Tiết kiệm điện");
        dto.setSortOrder(1);
        dto.setStatus("ACTIVE");
        dto.setModelId(1L);
        return dto;
    }

    // ── createSeries ──────────────────────────────────────────────────────

    @Test
    void createSeries_success() {
        when(carSeriesRepository.existsBySeriesCode("VF8-Plus")).thenReturn(false);
        when(carSeriesRepository.findBySeriesNameIgnoreCase("VF8 Plus")).thenReturn(Optional.empty());
        when(modelService.getModelById(1L)).thenReturn(mockModel());
        when(carSeriesRepository.save(any())).thenReturn(mockSeries());

        CarSeriesResponseDTO res = service.createSeries(validRequest());

        assertThat(res.getSeriesName()).isEqualTo("VF8 Plus");
        assertThat(res.getSeriesCode()).isEqualTo("VF8-Plus");
        verify(carSeriesRepository).save(any());
    }

    @Test
    void createSeries_fail_duplicateCode() {
        when(carSeriesRepository.existsBySeriesCode("VF8-Plus")).thenReturn(true);

        assertThatThrownBy(() -> service.createSeries(validRequest()))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Series code đã tồn tại");
    }

    @Test
    void createSeries_fail_duplicateName() {
        when(carSeriesRepository.existsBySeriesCode("VF8-Plus")).thenReturn(false);
        when(carSeriesRepository.findBySeriesNameIgnoreCase("VF8 Plus"))
            .thenReturn(Optional.of(mockSeries()));

        assertThatThrownBy(() -> service.createSeries(validRequest()))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("tên này đã tồn tại");
    }

    @Test
    void createSeries_success_nullCode() {
        // seriesCode null → bỏ qua check trùng code
        CarSeriesCreateDTO dto = validRequest();
        dto.setSeriesCode(null);

        CarSeries saved = mockSeries();
        saved.setSeriesCode(null);

        when(carSeriesRepository.findBySeriesNameIgnoreCase("VF8 Plus")).thenReturn(Optional.empty());
        when(modelService.getModelById(1L)).thenReturn(mockModel());
        when(carSeriesRepository.save(any())).thenReturn(saved);

        CarSeriesResponseDTO res = service.createSeries(dto);

        assertThat(res.getSeriesName()).isEqualTo("VF8 Plus");
        verify(carSeriesRepository, never()).existsBySeriesCode(any());
    }

    // ── getAllSeries ───────────────────────────────────────────────────────

    @Test
    void getAllSeries_success() {
        when(carSeriesRepository.findAll()).thenReturn(List.of(mockSeries()));

        List<CarSeriesResponseDTO> result = service.getAllSeries();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSeriesName()).isEqualTo("VF8 Plus");
    }

    @Test
    void getAllSeries_empty() {
        when(carSeriesRepository.findAll()).thenReturn(List.of());

        assertThat(service.getAllSeries()).isEmpty();
    }

    // ── getSeriesById ─────────────────────────────────────────────────────

    @Test
    void getSeriesById_found() {
        when(carSeriesRepository.findById(1L)).thenReturn(Optional.of(mockSeries()));

        CarSeries result = service.getSeriesById(1L);

        assertThat(result.getSeriesId()).isEqualTo(1L);
    }

    @Test
    void getSeriesById_notFound() {
        when(carSeriesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getSeriesById(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── getSeriesDetailById ───────────────────────────────────────────────

    @Test
    void getSeriesDetailById_success() {
        when(carSeriesRepository.findById(1L)).thenReturn(Optional.of(mockSeries()));

        CarSeriesResponseDTO res = service.getSeriesDetailById(1L);

        assertThat(res.getSeriesId()).isEqualTo(1L);
        assertThat(res.getStatus()).isEqualTo("ACTIVE");
    }

    // ── deleteSeries ──────────────────────────────────────────────────────

   @Test
    void deleteSeries_success() {
        CarSeries series = mockSeries();
        when(carSeriesRepository.findById(1L)).thenReturn(Optional.of(series));
        when(carRepository.existsBySeries_SeriesId(1L)).thenReturn(false); // thêm

        assertThatNoException().isThrownBy(() -> service.deleteSeries(1L));
        verify(carSeriesRepository).delete(series);
    }

    @Test
    void deleteSeries_fail_notFound() {
        when(carSeriesRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteSeries(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }
}