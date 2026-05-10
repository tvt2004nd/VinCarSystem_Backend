package com.vin.VinSystem.Car.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vin.VinSystem.Car.Entity.CarEvSpecs;

@Repository
public interface CarEvSpecsRepository extends JpaRepository<CarEvSpecs, Long> {
        @Query("SELECT e FROM CarEvSpecs e JOIN FETCH e.car")
    List<CarEvSpecs> findAllWithCar();
}
