package com.vin.VinSystem.Car.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vin.VinSystem.Car.Entity.CarColor;
import java.util.Optional;

@Repository
public interface CarColorRepository extends JpaRepository<CarColor, Long> {
    /**
     * Tìm màu theo tên
     */
    Optional<CarColor> findByColorNameIgnoreCase(String colorName);

    /**
     * Tìm màu theo color code
     */
    Optional<CarColor> findByColorCode(String colorCode);
}
