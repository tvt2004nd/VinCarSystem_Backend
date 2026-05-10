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
import com.vin.VinSystem.Car.DTO.CarPowertrainCreateDTO;
import com.vin.VinSystem.Car.DTO.CarPowertrainResponseDTO;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Car.Entity.CarPowertrain;
import com.vin.VinSystem.Car.Repository.CarPowertrainRepository;
import com.vin.VinSystem.Car.Repository.CarRepository;

class CarPowertrainServiceTest {

    CarPowertrainRepository carPowertrainRepository = mock(CarPowertrainRepository.class);
    CarRepository           carRepository           = mock(CarRepository.class);
    CarPowertrainService    service;

    @BeforeEach
    void setUp() {
        service = new CarPowertrainService(carPowertrainRepository, carRepository);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Car mockCarNoPowertrain() {
        Car c = new Car();
        c.setCarId(1L);
        c.setCarName("VinFast VF8");
        c.setPowertrain(null);
        return c;
    }

    private Car mockCarWithPowertrain() {
        Car c = mockCarNoPowertrain();
        c.setPowertrain(mockPowertrain(c));
        return c;
    }

    private CarPowertrain mockPowertrain(Car car) {
        CarPowertrain p = new CarPowertrain();
        p.setCar(car);
        p.setDriveType("FWD");
        p.setMaxPowerHp(260);
        p.setMaxTorqueNm(500);
        p.setAcceleration0100Sec(new BigDecimal("5.50"));
        p.setTopSpeedKmh(139);
        return p;
    }

    private CarPowertrainCreateDTO validRequest() {
        CarPowertrainCreateDTO dto = new CarPowertrainCreateDTO();
        dto.setDriveType("FWD");
        dto.setMaxPowerHp(260);
        dto.setMaxTorqueNm(500);
        dto.setAcceleration0100Sec(new BigDecimal("5.50"));
        dto.setTopSpeedKmh(139);
        return dto;
    }

    // ── addOrUpdateCarPowertrain ──────────────────────────────────────────

    @Test
    void addOrUpdate_create_success() {
        Car car = mockCarNoPowertrain();
        CarPowertrain saved = mockPowertrain(car);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carPowertrainRepository.save(any())).thenReturn(saved);
        when(carRepository.save(any())).thenReturn(car);

        CarPowertrainResponseDTO res = service.addOrUpdateCarPowertrain(1L, validRequest());

        assertThat(res.getDriveType()).isEqualTo("FWD");
        assertThat(res.getMaxPowerHp()).isEqualTo(260);
        verify(carPowertrainRepository).save(any());
    }

    @Test
    void addOrUpdate_update_existingPowertrain() {
        Car car = mockCarWithPowertrain();
        CarPowertrain updated = mockPowertrain(car);
        updated.setMaxPowerHp(300);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carPowertrainRepository.save(any())).thenReturn(updated);
        when(carRepository.save(any())).thenReturn(car);

        CarPowertrainCreateDTO dto = validRequest();
        dto.setMaxPowerHp(300);

        CarPowertrainResponseDTO res = service.addOrUpdateCarPowertrain(1L, dto);

        assertThat(res.getMaxPowerHp()).isEqualTo(300);
    }

    @Test
    void addOrUpdate_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addOrUpdateCarPowertrain(99L, validRequest()))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── getCarPowertrain ──────────────────────────────────────────────────

    @Test
    void getPowertrain_success() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarWithPowertrain()));

        CarPowertrainResponseDTO res = service.getCarPowertrain(1L);

        assertThat(res.getTopSpeedKmh()).isEqualTo(139);
        assertThat(res.getAcceleration0100Sec()).isEqualByComparingTo("5.50");
    }

    @Test
    void getPowertrain_fail_noPowertrain() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarNoPowertrain()));

        assertThatThrownBy(() -> service.getCarPowertrain(1L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Powertrain");
    }

    @Test
    void getPowertrain_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCarPowertrain(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── deleteCarPowertrain ───────────────────────────────────────────────

    @Test
    void delete_success() {
        Car car = mockCarWithPowertrain();
        CarPowertrain powertrain = car.getPowertrain(); // ← lưu reference trước

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        assertThatNoException().isThrownBy(() -> service.deleteCarPowertrain(1L));

        verify(carPowertrainRepository).delete(powertrain);
        verify(carRepository).save(car);
    }

    @Test
    void delete_noPowertrain_noOp() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarNoPowertrain()));

        assertThatNoException().isThrownBy(() -> service.deleteCarPowertrain(1L));

        verify(carPowertrainRepository, never()).delete(any());
    }

    @Test
    void delete_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteCarPowertrain(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }
}