package com.vin.VinSystem.Car.Service;

import java.math.BigDecimal;
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
import com.vin.VinSystem.Car.DTO.CarEvSpecsCreateDTO;
import com.vin.VinSystem.Car.DTO.CarEvSpecsResponseDTO;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Car.Entity.CarEvSpecs;
import com.vin.VinSystem.Car.Repository.CarEvSpecsRepository;
import com.vin.VinSystem.Car.Repository.CarRepository;

class CarEvSpecsServiceTest {

    CarEvSpecsRepository carEvSpecsRepository = mock(CarEvSpecsRepository.class);
    CarRepository        carRepository        = mock(CarRepository.class);
    CarEvSpecsService    service;

    @BeforeEach
    void setUp() {
        service = new CarEvSpecsService(carEvSpecsRepository, carRepository);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Car mockCarNoSpecs() {
        Car c = new Car();
        c.setCarId(1L);
        c.setCarName("VinFast VF8");
        c.setEvSpecs(null);
        return c;
    }

    private Car mockCarWithSpecs() {
        Car c = mockCarNoSpecs();
        c.setEvSpecs(mockEvSpecs(c));
        return c;
    }

    private CarEvSpecs mockEvSpecs(Car car) {
        CarEvSpecs s = new CarEvSpecs();
        s.setCar(car);
        s.setBatteryCapacityKwh(new BigDecimal("82.00"));
        s.setRangeKm(500);
        s.setAcChargingKw(new BigDecimal("10.00"));
        s.setDcFastChargingKw(new BigDecimal("12.00"));
        s.setFastChargeTimeMin(70);
        return s;
    }

    private CarEvSpecsCreateDTO validRequest() {
        CarEvSpecsCreateDTO dto = new CarEvSpecsCreateDTO();
        dto.setBatteryCapacityKwh(new BigDecimal("82.00"));
        dto.setRangeKm(500);
        dto.setAcChargingKw(new BigDecimal("10.00"));
        dto.setDcFastChargingKw(new BigDecimal("12.00"));
        dto.setFastChargeTimeMin(70);
        return dto;
    }

    // ── addOrUpdateCarEvSpecs ─────────────────────────────────────────────

    @Test
    void addOrUpdate_create_success() {
        Car car = mockCarNoSpecs();
        CarEvSpecs saved = mockEvSpecs(car);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carEvSpecsRepository.save(any())).thenReturn(saved);
        when(carRepository.save(any())).thenReturn(car);

        CarEvSpecsResponseDTO res = service.addOrUpdateCarEvSpecs(1L, validRequest());

        assertThat(res.getRangeKm()).isEqualTo(500);
        assertThat(res.getFastChargeTimeMin()).isEqualTo(70);
        verify(carEvSpecsRepository).save(any());
    }

    @Test
    void addOrUpdate_update_existingSpecs() {
        Car car = mockCarWithSpecs();
        CarEvSpecs updated = mockEvSpecs(car);
        updated.setRangeKm(600);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carEvSpecsRepository.save(any())).thenReturn(updated);
        when(carRepository.save(any())).thenReturn(car);

        CarEvSpecsCreateDTO dto = validRequest();
        dto.setRangeKm(600);

        CarEvSpecsResponseDTO res = service.addOrUpdateCarEvSpecs(1L, dto);

        assertThat(res.getRangeKm()).isEqualTo(600);
    }

    @Test
    void addOrUpdate_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addOrUpdateCarEvSpecs(99L, validRequest()))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── getCarEvSpecs ─────────────────────────────────────────────────────

    @Test
    void getEvSpecs_success() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarWithSpecs()));

        CarEvSpecsResponseDTO res = service.getCarEvSpecs(1L);

        assertThat(res.getBatteryCapacityKwh()).isEqualByComparingTo("82.00");
        assertThat(res.getAcChargingKw()).isEqualByComparingTo("10.00");
    }

    @Test
    void getEvSpecs_fail_noSpecs() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarNoSpecs()));

        assertThatThrownBy(() -> service.getCarEvSpecs(1L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("EV Specs");
    }

    @Test
    void getEvSpecs_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCarEvSpecs(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── deleteCarEvSpecs ──────────────────────────────────────────────────

    @Test
    void delete_success() {
        Car car = mockCarWithSpecs();
        CarEvSpecs specs = car.getEvSpecs(); // ← lưu reference trước khi service set null

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        assertThatNoException().isThrownBy(() -> service.deleteCarEvSpecs(1L));

        verify(carEvSpecsRepository).delete(specs);
        verify(carRepository).save(car);
    }

    @Test
    void delete_noSpecs_noOp() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarNoSpecs()));

        assertThatNoException().isThrownBy(() -> service.deleteCarEvSpecs(1L));

        verify(carEvSpecsRepository, never()).delete(any());
    }

    @Test
    void delete_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteCarEvSpecs(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }
}