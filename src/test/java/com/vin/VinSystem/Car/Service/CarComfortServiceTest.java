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
import com.vin.VinSystem.Car.DTO.CarComfortCreateDTO;
import com.vin.VinSystem.Car.DTO.CarComfortResponseDTO;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Car.Entity.CarComfort;
import com.vin.VinSystem.Car.Repository.CarComfortRepository;
import com.vin.VinSystem.Car.Repository.CarRepository;

class CarComfortServiceTest {

    CarComfortRepository carComfortRepository = mock(CarComfortRepository.class);
    CarRepository        carRepository        = mock(CarRepository.class);
    CarComfortService    service;

    @BeforeEach
    void setUp() {
        service = new CarComfortService(carComfortRepository, carRepository);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Car mockCarNoComfort() {
        Car c = new Car();
        c.setCarId(1L);
        c.setCarName("VinFast VF8");
        c.setComfort(null);
        return c;
    }

    private Car mockCarWithComfort() {
        Car c = mockCarNoComfort();
        CarComfort comfort = mockComfort(c);
        c.setComfort(comfort);
        return c;
    }

    private CarComfort mockComfort(Car car) {
        CarComfort cf = new CarComfort();
        cf.setCar(car);
        cf.setInfotainmentScreenInch(new BigDecimal("15.00"));
        cf.setSpeakerCount(8);
        cf.setClimateControl(true);
        cf.setSeatMaterial("Nappa");
        cf.setSunroof(true);
        cf.setWirelessCarplay(true);
        return cf;
    }

    private CarComfortCreateDTO validRequest() {
        CarComfortCreateDTO dto = new CarComfortCreateDTO();
        dto.setInfotainmentScreenInch(new BigDecimal("15.00"));
        dto.setSpeakerCount(8);
        dto.setClimateControl(true);
        dto.setSeatMaterial("Nappa");
        dto.setSunroof(true);
        dto.setWirelessCarplay(true);
        return dto;
    }

    // ── addOrUpdateCarComfort ─────────────────────────────────────────────

    @Test
    void addOrUpdate_create_success() {
        Car car = mockCarNoComfort();
        CarComfort saved = mockComfort(car);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carComfortRepository.save(any())).thenReturn(saved);
        when(carRepository.save(any())).thenReturn(car);

        CarComfortResponseDTO res = service.addOrUpdateCarComfort(1L, validRequest());

        assertThat(res.getSpeakerCount()).isEqualTo(8);
        assertThat(res.getSeatMaterial()).isEqualTo("Nappa");
        verify(carComfortRepository).save(any());
    }

    @Test
    void addOrUpdate_update_existingComfort() {
        // Xe đã có comfort → update thay vì tạo mới
        Car car = mockCarWithComfort();
        CarComfort updated = mockComfort(car);
        updated.setSpeakerCount(12);

        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(carComfortRepository.save(any())).thenReturn(updated);
        when(carRepository.save(any())).thenReturn(car);

        CarComfortCreateDTO dto = validRequest();
        dto.setSpeakerCount(12);

        CarComfortResponseDTO res = service.addOrUpdateCarComfort(1L, dto);

        assertThat(res.getSpeakerCount()).isEqualTo(12);
    }

    @Test
    void addOrUpdate_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addOrUpdateCarComfort(99L, validRequest()))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── getCarComfort ─────────────────────────────────────────────────────

    @Test
    void getCarComfort_success() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarWithComfort()));

        CarComfortResponseDTO res = service.getCarComfort(1L);

        assertThat(res.getClimateControl()).isTrue();
        assertThat(res.getSunroof()).isTrue();
    }

    @Test
    void getCarComfort_fail_noComfort() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarNoComfort()));

        assertThatThrownBy(() -> service.getCarComfort(1L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Comfort");
    }

    @Test
    void getCarComfort_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCarComfort(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }

    // ── deleteCarComfort ──────────────────────────────────────────────────

@Test
void deleteCarComfort_success() {
    Car car = mockCarWithComfort();
    CarComfort comfort = car.getComfort(); // ✅ giữ lại object

    when(carRepository.findById(1L)).thenReturn(Optional.of(car));

    assertThatNoException().isThrownBy(() -> service.deleteCarComfort(1L));

    verify(carComfortRepository).delete(comfort); // ✅ dùng object cũ
    verify(carRepository).save(car);
}

    @Test
    void deleteCarComfort_noComfort_noOp() {
        // Xe không có comfort → không làm gì, không throw
        when(carRepository.findById(1L)).thenReturn(Optional.of(mockCarNoComfort()));

        assertThatNoException().isThrownBy(() -> service.deleteCarComfort(1L));

        verify(carComfortRepository, never()).delete(any());
    }

    @Test
    void deleteCarComfort_fail_carNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteCarComfort(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("99");
    }
}