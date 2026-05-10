package com.vin.VinSystem.Car.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vin.VinSystem.Car.Entity.SpecsCommon;

@Repository
public interface SpecsCommonRepository extends JpaRepository<SpecsCommon, Long> {
        @Query("SELECT s FROM SpecsCommon s JOIN FETCH s.car")
    List<SpecsCommon> findAllWithCar();
}
