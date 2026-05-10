package com.vin.VinSystem.Car.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vin.VinSystem.Car.Entity.CarSafety;

@Repository
public interface CarSafetyRepository extends JpaRepository<CarSafety, Long> {
        @Query("SELECT s FROM CarSafety s JOIN FETCH s.car")
    List<CarSafety> findAllWithCar();
}
