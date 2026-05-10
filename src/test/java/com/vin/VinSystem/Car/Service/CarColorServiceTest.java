package com.vin.VinSystem.Car.Service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
import com.vin.VinSystem.Car.DTO.CarColorCreateDTO;
import com.vin.VinSystem.Car.DTO.CarColorResponseDTO;
import com.vin.VinSystem.Car.Entity.CarColor;
import com.vin.VinSystem.Car.Repository.CarColorRepository;

class CarColorServiceTest {

    CarColorRepository carColorRepository = mock(CarColorRepository.class);
    CarColorService    service;

    @BeforeEach
    void setUp() {
        service = new CarColorService(carColorRepository);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private CarColor mockColor() {
        CarColor c = new CarColor();
        c.setColorId(1L);
        c.setColorName("Đỏ Mận");
        c.setColorCode("#a45151");
        return c;
    }

    private CarColorCreateDTO validRequest() {
        CarColorCreateDTO dto = new CarColorCreateDTO();
        dto.setColorName("Đỏ Mận");
        dto.setColorCode("#a45151");
        return dto;
    }

    // ── createColor ───────────────────────────────────────────────────────

    @Test
    void createColor_success() {
        when(carColorRepository.findByColorNameIgnoreCase("Đỏ Mận")).thenReturn(Optional.empty());
        when(carColorRepository.findByColorCode("#a45151")).thenReturn(Optional.empty());
        when(carColorRepository.save(any())).thenReturn(mockColor());

        CarColorResponseDTO res = service.createColor(validRequest());

        assertThat(res.getColorName()).isEqualTo("Đỏ Mận");
        assertThat(res.getColorCode()).isEqualTo("#a45151");
        verify(carColorRepository).save(any());
    }

    @Test
    void createColor_fail_duplicateName() {
        when(carColorRepository.findByColorNameIgnoreCase("Đỏ Mận"))
            .thenReturn(Optional.of(mockColor()));

        assertThatThrownBy(() -> service.createColor(validRequest()))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("đã tồn tại");
    }

    @Test
    void createColor_fail_duplicateCode() {
        when(carColorRepository.findByColorNameIgnoreCase("Đỏ Mận")).thenReturn(Optional.empty());
        when(carColorRepository.findByColorCode("#a45151")).thenReturn(Optional.of(mockColor()));

        assertThatThrownBy(() -> service.createColor(validRequest()))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("Mã màu đã tồn tại");
    }

    @Test
    void createColor_success_nullCode() {
        // colorCode null → bỏ qua check trùng code
        CarColorCreateDTO dto = new CarColorCreateDTO();
        dto.setColorName("Trắng Ngọc");
        dto.setColorCode(null);

        CarColor saved = new CarColor();
        saved.setColorId(2L);
        saved.setColorName("Trắng Ngọc");
        saved.setColorCode(null);

        when(carColorRepository.findByColorNameIgnoreCase("Trắng Ngọc")).thenReturn(Optional.empty());
        when(carColorRepository.save(any())).thenReturn(saved);

        CarColorResponseDTO res = service.createColor(dto);

        assertThat(res.getColorName()).isEqualTo("Trắng Ngọc");
        verify(carColorRepository, never()).findByColorCode(any());
    }

    // ── getAllColors ───────────────────────────────────────────────────────

    @Test
    void getAllColors_success() {
        when(carColorRepository.findAll()).thenReturn(List.of(mockColor()));

        List<CarColorResponseDTO> result = service.getAllColors();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getColorName()).isEqualTo("Đỏ Mận");
    }

    @Test
    void getAllColors_empty() {
        when(carColorRepository.findAll()).thenReturn(List.of());

        assertThat(service.getAllColors()).isEmpty();
    }

    // ── getColorById ──────────────────────────────────────────────────────

    @Test
    void getColorById_found() {
        when(carColorRepository.findById(1L)).thenReturn(Optional.of(mockColor()));

        CarColor result = service.getColorById(1L);

        assertThat(result.getColorId()).isEqualTo(1L);
    }

    @Test
    void getColorById_notFound() {
        when(carColorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getColorById(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── getColorDetailById ────────────────────────────────────────────────

    @Test
    void getColorDetailById_success() {
        when(carColorRepository.findById(1L)).thenReturn(Optional.of(mockColor()));

        CarColorResponseDTO res = service.getColorDetailById(1L);

        assertThat(res.getColorId()).isEqualTo(1L);
        assertThat(res.getColorCode()).isEqualTo("#a45151");
    }
}