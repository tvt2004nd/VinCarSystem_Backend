package com.vin.VinSystem.Car.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vin.VinSystem.Car.Entity.CarWarranty;

@Repository
public interface CarWarrantyRepository extends JpaRepository<CarWarranty, Long> {
        @Query("SELECT w FROM CarWarranty w JOIN FETCH w.car")
    List<CarWarranty> findAllWithCar();
}
