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
import com.vin.VinSystem.Car.DTO.CarDimensionsCreateDTO;
import com.vin.VinSystem.Car.DTO.CarDimensionsResponseDTO;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Car.Entity.CarDimensions;
import com.vin.VinSystem.Car.Repository.CarDimensionsRepository;
import com.vin.VinSystem.Car.Repository.CarRepository;

class CarDimensionsServiceTest {

    CarDimensionsRepository carDimensionsRepository = mock(CarDimensionsRepository.class);
    CarRepository           carRepository           = mock(CarRepository.class);
    CarDimensionsService    service;

    @BeforeEach
    void setUp() {
        service = new CarDimensionsService(carDimensionsRepository, carRepository);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Car mockCarNoDims() {
        Car c = new Car();
        c.setCarId(1L);
        c.setCarName("VinFast VF8");
        c.setDimensions(null);
        return c;
    }

    private Car mockCarWithDims() {
        Car c = mockCarNoDims();
        c.setDimensions(mockDims(c));
        return c;
    }

    private CarDimensions mockDims(Car car) {
        CarDimensions d = new CarDimensions();
        d.setCar(car);
        d.setLengthMm(4570);
        d.setWidthMm(1667);
        d.setHeightMm(1934);
        d.setWheelbaseMm(2950);
        d.setCurbWeightKg(new BigDecimal("999.00"));
        d.setTrunkVolumeL(100);
        return d;
    }

    private CarDimensionsCreateDTO validRequest() {
        CarDimensionsCreateDTO dto = new CarDimensionsCreateDTO();
        dto.setLengthMm(4570);
        dto.setWidthMm(1667);
        dto.setHeightMm(1934);
        dto.setWheelbaseMm(2950);
        dto.setCurbWeightKg(new BigDecimal("999.00"));
        dto.setTrunkVolumeL(100);
        return dto;
    }

    // ── addOrUpdateCarDimensions ──────────────────────────────────────────

    @Test
    void addOrUpdate_create_success() {
        Car car = mockCarNoDims();
        CarDimensions saved = mockDims(car);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carDimensionsRepository.save(any())).thenReturn(saved);
        when(carRepository.save(any())).thenReturn(car);

        CarDimensionsResponseDTO res = service.addOrUpdateCarDimensions(1L, validRequest());

        assertThat(res.getLengthMm()).isEqualTo(4570);
        assertThat(res.getWheelbaseMm()).isEqualTo(2950);
        verify(carDimensionsRepository).save(any());
    }

    @Test
    void addOrUpdate_update_existingDims() {
        Car car = mockCarWithDims();
        CarDimensions updated = mockDims(car);
        updated.setLengthMm(4700);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carDimensionsRepository.save(any())).thenReturn(updated);
        when(carRepository.save(any())).thenReturn(car);

        CarDimensionsCreateDTO dto = validRequest();
        dto.setLengthMm(4700);

        CarDimensionsResponseDTO res = service.addOrUpdateCarDimensions(1L, dto);

        assertThat(res.getLengthMm()).isEqualTo(4700);
    }

    @Test
    void addOrUpdate_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addOrUpdateCarDimensions(99L, validRequest()))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── getCarDimensions ──────────────────────────────────────────────────

    @Test
    void getDimensions_success() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarWithDims()));

        CarDimensionsResponseDTO res = service.getCarDimensions(1L);

        assertThat(res.getHeightMm()).isEqualTo(1934);
        assertThat(res.getTrunkVolumeL()).isEqualTo(100);
    }

    @Test
    void getDimensions_fail_noDims() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarNoDims()));

        assertThatThrownBy(() -> service.getCarDimensions(1L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("dimensions");
    }

    @Test
    void getDimensions_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCarDimensions(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── deleteCarDimensions ───────────────────────────────────────────────

    @Test
void delete_success() {
    Car car = mockCarWithDims();
    CarDimensions dims = car.getDimensions(); // ← lưu reference trước

    when(carRepository.findById(1L)).thenReturn(Optional.of(car));

    assertThatNoException().isThrownBy(() -> service.deleteCarDimensions(1L));

    verify(carDimensionsRepository).delete(dims); // ← dùng reference đã lưu
    verify(carRepository).save(car);
}

    @Test
    void delete_noDims_noOp() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarNoDims()));

        assertThatNoException().isThrownBy(() -> service.deleteCarDimensions(1L));

        verify(carDimensionsRepository, never()).delete(any());
    }

    @Test
    void delete_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteCarDimensions(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }
}