package com.vin.VinSystem.Car.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vin.VinSystem.Car.Entity.CarPowertrain;

@Repository
public interface CarPowertrainRepository extends JpaRepository<CarPowertrain, Long> {
        @Query("SELECT p FROM CarPowertrain p JOIN FETCH p.car")
    List<CarPowertrain> findAllWithCar();
}
