package com.vin.VinSystem.Car.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vin.VinSystem.Car.Entity.ModelColorOption;
import com.vin.VinSystem.Car.Entity.ModelColorOptionId;
import java.util.List;

@Repository
public interface ModelColorOptionRepository extends JpaRepository<ModelColorOption, ModelColorOptionId> {
    
    /**
     * Batch load all color options for given model IDs and color IDs
     * Returns all combinations of (modelId, colorId) pairs
     */
    @Query("SELECT mco FROM ModelColorOption mco " +
           "WHERE mco.id.modelId IN :modelIds AND mco.id.colorId IN :colorIds")
    List<ModelColorOption> findAllByModelColorIds(
            @Param("modelIds") List<Long> modelIds,
            @Param("colorIds") List<Long> colorIds);
}