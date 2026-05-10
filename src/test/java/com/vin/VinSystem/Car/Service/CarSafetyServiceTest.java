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
import com.vin.VinSystem.Car.DTO.CarSafetyCreateDTO;
import com.vin.VinSystem.Car.DTO.CarSafetyResponseDTO;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Car.Entity.CarSafety;
import com.vin.VinSystem.Car.Repository.CarRepository;
import com.vin.VinSystem.Car.Repository.CarSafetyRepository;

class CarSafetyServiceTest {

    CarSafetyRepository carSafetyRepository = mock(CarSafetyRepository.class);
    CarRepository       carRepository       = mock(CarRepository.class);
    CarSafetyService    service;

    @BeforeEach
    void setUp() {
        service = new CarSafetyService(carSafetyRepository, carRepository);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Car mockCarNoSafety() {
        Car c = new Car();
        c.setCarId(1L);
        c.setCarName("VinFast VF8");
        c.setSafety(null);
        return c;
    }

    private Car mockCarWithSafety() {
        Car c = mockCarNoSafety();
        c.setSafety(mockSafety(c));
        return c;
    }

    private CarSafety mockSafety(Car car) {
        CarSafety s = new CarSafety();
        s.setCar(car);
        s.setAirbags(true);
        s.setAbs(true);
        s.setEsc(true);
        s.setTractionControl(true);
        s.setLaneKeepAssist(true);
        s.setAdaptiveCruiseControl(true);
        s.setRearCamera(true);
        s.setParkingSensors(true);
        return s;
    }

    private CarSafetyCreateDTO validRequest() {
        CarSafetyCreateDTO dto = new CarSafetyCreateDTO();
        dto.setAirbags(true);
        dto.setAbs(true);
        dto.setEsc(true);
        dto.setTractionControl(true);
        dto.setLaneKeepAssist(true);
        dto.setAdaptiveCruiseControl(true);
        dto.setRearCamera(true);
        dto.setParkingSensors(true);
        return dto;
    }

    // ── addOrUpdateCarSafety ──────────────────────────────────────────────

    @Test
    void addOrUpdate_create_success() {
        Car car = mockCarNoSafety();
        CarSafety saved = mockSafety(car);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carSafetyRepository.save(any())).thenReturn(saved);
        when(carRepository.save(any())).thenReturn(car);

        CarSafetyResponseDTO res = service.addOrUpdateCarSafety(1L, validRequest());

        assertThat(res.getAirbags()).isTrue();
        assertThat(res.getAbs()).isTrue();
        assertThat(res.getLaneKeepAssist()).isTrue();
        verify(carSafetyRepository).save(any());
    }

    @Test
    void addOrUpdate_update_existingSafety() {
        Car car = mockCarWithSafety();
        CarSafety updated = mockSafety(car);
        updated.setLaneKeepAssist(false);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carSafetyRepository.save(any())).thenReturn(updated);
        when(carRepository.save(any())).thenReturn(car);

        CarSafetyCreateDTO dto = validRequest();
        dto.setLaneKeepAssist(false);

        CarSafetyResponseDTO res = service.addOrUpdateCarSafety(1L, dto);

        assertThat(res.getLaneKeepAssist()).isFalse();
    }

    @Test
    void addOrUpdate_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addOrUpdateCarSafety(99L, validRequest()))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── getCarSafety ──────────────────────────────────────────────────────

    @Test
    void getSafety_success() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarWithSafety()));

        CarSafetyResponseDTO res = service.getCarSafety(1L);

        assertThat(res.getAdaptiveCruiseControl()).isTrue();
        assertThat(res.getRearCamera()).isTrue();
        assertThat(res.getParkingSensors()).isTrue();
    }

    @Test
    void getSafety_fail_noSafety() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarNoSafety()));

        assertThatThrownBy(() -> service.getCarSafety(1L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Safety");
    }

    @Test
    void getSafety_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCarSafety(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── deleteCarSafety ───────────────────────────────────────────────────

    @Test
    void delete_success() {
        Car car = mockCarWithSafety();
        CarSafety safety = car.getSafety(); // ← lưu reference trước

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        assertThatNoException().isThrownBy(() -> service.deleteCarSafety(1L));

        verify(carSafetyRepository).delete(safety);
        verify(carRepository).save(car);
    }

    @Test
    void delete_noSafety_noOp() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarNoSafety()));

        assertThatNoException().isThrownBy(() -> service.deleteCarSafety(1L));

        verify(carSafetyRepository, never()).delete(any());
    }

    @Test
    void delete_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteCarSafety(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }
}