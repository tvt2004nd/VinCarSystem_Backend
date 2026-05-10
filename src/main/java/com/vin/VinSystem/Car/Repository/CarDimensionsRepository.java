package com.vin.VinSystem.Car.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vin.VinSystem.Car.Entity.CarDimensions;

@Repository
public interface CarDimensionsRepository extends JpaRepository<CarDimensions, Long> {
        @Query("SELECT d FROM CarDimensions d JOIN FETCH d.car")
    List<CarDimensions> findAllWithCar();
}
