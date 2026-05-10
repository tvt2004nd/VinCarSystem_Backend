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
import com.vin.VinSystem.Car.DTO.CarWarrantyCreateDTO;
import com.vin.VinSystem.Car.DTO.CarWarrantyResponseDTO;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Car.Entity.CarWarranty;
import com.vin.VinSystem.Car.Repository.CarRepository;
import com.vin.VinSystem.Car.Repository.CarWarrantyRepository;

class CarWarrantyServiceTest {

    CarWarrantyRepository carWarrantyRepository = mock(CarWarrantyRepository.class);
    CarRepository         carRepository         = mock(CarRepository.class);
    CarWarrantyService    service;

    @BeforeEach
    void setUp() {
        service = new CarWarrantyService(carWarrantyRepository, carRepository);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Car mockCarNoWarranty() {
        Car c = new Car();
        c.setCarId(1L);
        c.setCarName("VinFast VF8");
        c.setWarranty(null);
        return c;
    }

    private Car mockCarWithWarranty() {
        Car c = mockCarNoWarranty();
        c.setWarranty(mockWarranty(c));
        return c;
    }

    private CarWarranty mockWarranty(Car car) {
        CarWarranty w = new CarWarranty();
        w.setCar(car);
        w.setWarrantyYears(3);
        w.setWarrantyKm(10000);
        w.setBatteryWarrantyYears(3);
        w.setBatteryWarrantyKm(15000);
        return w;
    }

    private CarWarrantyCreateDTO validRequest() {
        CarWarrantyCreateDTO dto = new CarWarrantyCreateDTO();
        dto.setWarrantyYears(3);
        dto.setWarrantyKm(10000);
        dto.setBatteryWarrantyYears(3);
        dto.setBatteryWarrantyKm(15000);
        return dto;
    }

    // ── addOrUpdateCarWarranty ────────────────────────────────────────────

    @Test
    void addOrUpdate_create_success() {
        Car car = mockCarNoWarranty();
        CarWarranty saved = mockWarranty(car);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carWarrantyRepository.save(any())).thenReturn(saved);
        when(carRepository.save(any())).thenReturn(car);

        CarWarrantyResponseDTO res = service.addOrUpdateCarWarranty(1L, validRequest());

        assertThat(res.getWarrantyYears()).isEqualTo(3);
        assertThat(res.getBatteryWarrantyKm()).isEqualTo(15000);
        verify(carWarrantyRepository).save(any());
    }

    @Test
    void addOrUpdate_update_existingWarranty() {
        Car car = mockCarWithWarranty();
        CarWarranty updated = mockWarranty(car);
        updated.setWarrantyYears(5);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carWarrantyRepository.save(any())).thenReturn(updated);
        when(carRepository.save(any())).thenReturn(car);

        CarWarrantyCreateDTO dto = validRequest();
        dto.setWarrantyYears(5);

        CarWarrantyResponseDTO res = service.addOrUpdateCarWarranty(1L, dto);

        assertThat(res.getWarrantyYears()).isEqualTo(5);
    }

    @Test
    void addOrUpdate_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addOrUpdateCarWarranty(99L, validRequest()))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── getCarWarranty ────────────────────────────────────────────────────

    @Test
    void getWarranty_success() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarWithWarranty()));

        CarWarrantyResponseDTO res = service.getCarWarranty(1L);

        assertThat(res.getWarrantyKm()).isEqualTo(10000);
        assertThat(res.getBatteryWarrantyYears()).isEqualTo(3);
    }

    @Test
    void getWarranty_fail_noWarranty() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarNoWarranty()));

        assertThatThrownBy(() -> service.getCarWarranty(1L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Warranty");
    }

    @Test
    void getWarranty_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCarWarranty(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── deleteCarWarranty ─────────────────────────────────────────────────

    @Test
    void delete_success() {
        Car car = mockCarWithWarranty();
        CarWarranty warranty = car.getWarranty(); // ← lưu reference trước

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        assertThatNoException().isThrownBy(() -> service.deleteCarWarranty(1L));

        verify(carWarrantyRepository).delete(warranty);
        verify(carRepository).save(car);
    }

    @Test
    void delete_noWarranty_noOp() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarNoWarranty()));

        assertThatNoException().isThrownBy(() -> service.deleteCarWarranty(1L));

        verify(carWarrantyRepository, never()).delete(any());
    }

    @Test
    void delete_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteCarWarranty(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }
}