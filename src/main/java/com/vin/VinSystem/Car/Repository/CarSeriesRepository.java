package com.vin.VinSystem.Car.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vin.VinSystem.Car.Entity.CarSeries;

@Repository
public interface CarSeriesRepository extends JpaRepository<CarSeries, Long> {
    /**
     * Tìm series theo series code
     */
    Optional<CarSeries> findBySeriesCode(String seriesCode);

    /**
     * Tìm series theo series name
     */
    Optional<CarSeries> findBySeriesNameIgnoreCase(String seriesName);

    /**
     * Kiểm tra series code đã tồn tại
     */
    boolean existsBySeriesCode(String seriesCode);
   

}
