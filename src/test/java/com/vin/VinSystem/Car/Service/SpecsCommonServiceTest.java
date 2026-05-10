package com.vin.VinSystem.Car.Service;

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
import com.vin.VinSystem.Car.DTO.SpecsCommonCreateDTO;
import com.vin.VinSystem.Car.DTO.SpecsCommonResponseDTO;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Car.Entity.SpecsCommon;
import com.vin.VinSystem.Car.Repository.CarRepository;
import com.vin.VinSystem.Car.Repository.SpecsCommonRepository;

class SpecsCommonServiceTest {

    SpecsCommonRepository specsCommonRepository = mock(SpecsCommonRepository.class);
    CarRepository         carRepository         = mock(CarRepository.class);
    SpecsCommonService    service;

    @BeforeEach
    void setUp() {
        service = new SpecsCommonService(specsCommonRepository, carRepository);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Car mockCarNoSpecs() {
        Car c = new Car();
        c.setCarId(1L);
        c.setCarName("VinFast VF8");
        c.setSpecsCommon(null);
        return c;
    }

    private Car mockCarWithSpecs() {
        Car c = mockCarNoSpecs();
        c.setSpecsCommon(mockSpecs(c));
        return c;
    }

    private SpecsCommon mockSpecs(Car car) {
        SpecsCommon s = new SpecsCommon();
        s.setCar(car);
        s.setBodyType("SUV");
        s.setSeatingCapacity(7);
        s.setDoors(4);
        s.setFuelType("Electric");
        return s;
    }

    private SpecsCommonCreateDTO validRequest() {
        SpecsCommonCreateDTO dto = new SpecsCommonCreateDTO();
        dto.setBodyType("SUV");
        dto.setSeatingCapacity(7);
        dto.setDoors(4);
        dto.setFuelType("Electric");
        return dto;
    }

    // ── addOrUpdateSpecsCommon ────────────────────────────────────────────

    @Test
    void addOrUpdate_create_success() {
        Car car = mockCarNoSpecs();
        SpecsCommon saved = mockSpecs(car);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(specsCommonRepository.save(any())).thenReturn(saved);
        when(carRepository.save(any())).thenReturn(car);

        SpecsCommonResponseDTO res = service.addOrUpdateSpecsCommon(1L, validRequest());

        assertThat(res.getBodyType()).isEqualTo("SUV");
        assertThat(res.getSeatingCapacity()).isEqualTo(7);
        assertThat(res.getFuelType()).isEqualTo("Electric");
        verify(specsCommonRepository).save(any());
    }

    @Test
    void addOrUpdate_update_existingSpecs() {
        Car car = mockCarWithSpecs();
        SpecsCommon updated = mockSpecs(car);
        updated.setSeatingCapacity(5);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(specsCommonRepository.save(any())).thenReturn(updated);
        when(carRepository.save(any())).thenReturn(car);

        SpecsCommonCreateDTO dto = validRequest();
        dto.setSeatingCapacity(5);

        SpecsCommonResponseDTO res = service.addOrUpdateSpecsCommon(1L, dto);

        assertThat(res.getSeatingCapacity()).isEqualTo(5);
    }

    @Test
    void addOrUpdate_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addOrUpdateSpecsCommon(99L, validRequest()))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── getSpecsCommon ────────────────────────────────────────────────────

    @Test
    void getSpecs_success() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarWithSpecs()));

        SpecsCommonResponseDTO res = service.getSpecsCommon(1L);

        assertThat(res.getBodyType()).isEqualTo("SUV");
        assertThat(res.getDoors()).isEqualTo(4);
    }

    @Test
    void getSpecs_fail_noSpecs() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarNoSpecs()));

        assertThatThrownBy(() -> service.getSpecsCommon(1L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("specs common");
    }

    @Test
    void getSpecs_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getSpecsCommon(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── deleteSpecsCommon ─────────────────────────────────────────────────

    @Test
    void delete_success() {
        Car car = mockCarWithSpecs();
        SpecsCommon specs = car.getSpecsCommon(); // ← lưu reference trước

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        assertThatNoException().isThrownBy(() -> service.deleteSpecsCommon(1L));

        verify(specsCommonRepository).delete(specs);
        verify(carRepository).save(car);
    }

    @Test
    void delete_noSpecs_noOp() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarNoSpecs()));

        assertThatNoException().isThrownBy(() -> service.deleteSpecsCommon(1L));

        verify(specsCommonRepository, never()).delete(any());
    }

    @Test
    void delete_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteSpecsCommon(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }
}