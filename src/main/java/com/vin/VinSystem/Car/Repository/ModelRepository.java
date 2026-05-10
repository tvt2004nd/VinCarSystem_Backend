package com.vin.VinSystem.Car.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vin.VinSystem.Car.Entity.Model;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {
    /**
     * Tìm model theo tên
     */
    Optional<Model> findByModelNameIgnoreCase(String modelName);

    /**
     * Tìm tất cả model theo segment
     */
    List<Model> findBySegment(String segment);
    
}
