package com.vin.VinSystem.Car.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vin.VinSystem.Car.Entity.CarComfort;

@Repository
public interface CarComfortRepository extends JpaRepository<CarComfort, Long> {
       @Query("SELECT c FROM CarComfort c JOIN FETCH c.car")
    List<CarComfort> findAllWithCar();
}
