package com.vin.VinSystem.Car.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vin.VinSystem.Car.Entity.Car;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    //  Tất cả @EntityGraph đổi "color" → "colors"

    @EntityGraph(attributePaths = {
        "model", "series", "colors", "carImages",
        "comfort", "safety", "powertrain", "evSpecs",
        "warranty", "dimensions", "specsCommon"
    })
    @Query("SELECT DISTINCT c FROM Car c")
    List<Car> findAllWithEagerLoad();

    @EntityGraph(attributePaths = {
        "model", "series", "colors", "carImages",
        "comfort", "safety", "powertrain", "evSpecs",
        "warranty", "dimensions", "specsCommon"
    })
    @Query("SELECT c FROM Car c WHERE c.carId = :id")
    Optional<Car> findByIdWithEagerLoad(@Param("id") Long id);

    @EntityGraph(attributePaths = {
        "model", "series", "colors", "carImages",
        "comfort", "safety", "powertrain", "evSpecs",
        "warranty", "dimensions", "specsCommon"
    })
    List<Car> findByModel_ModelId(Long modelId);

    @EntityGraph(attributePaths = {
        "model", "series", "colors", "carImages",
        "comfort", "safety", "powertrain", "evSpecs",
        "warranty", "dimensions", "specsCommon"
    })
    List<Car> findBySeries_SeriesId(Long seriesId);

    @EntityGraph(attributePaths = {
        "model", "series", "colors", "carImages",
        "comfort", "safety", "powertrain", "evSpecs",
        "warranty", "dimensions", "specsCommon"
    })
    List<Car> findByColors_ColorId(Long colorId);

    @EntityGraph(attributePaths = {
        "model", "series", "colors", "carImages",
        "comfort", "safety", "powertrain", "evSpecs",
        "warranty", "dimensions", "specsCommon"
    })
    List<Car> findByStatus(String status);

    @EntityGraph(attributePaths = {
        "model", "series", "colors", "carImages",
        "comfort", "safety", "powertrain", "evSpecs",
        "warranty", "dimensions", "specsCommon"
    })
    List<Car> findByCarNameContainingIgnoreCase(String carName);

    @EntityGraph(attributePaths = {
        "model", "series", "colors", "carImages",
        "comfort", "safety", "powertrain", "evSpecs",
        "warranty", "dimensions", "specsCommon"
    })
    List<Car> findByModel_ModelIdAndStatus(Long modelId, String status);

    @EntityGraph(attributePaths = {
        "model", "series", "colors", "carImages",
        "comfort", "safety", "powertrain", "evSpecs",
        "warranty", "dimensions", "specsCommon"
    })
    List<Car> findBySeries_SeriesIdAndStatus(Long seriesId, String status);

    //  Sửa query — dùng JOIN colors thay vì c.color.colorId
    @Query("""
           SELECT c FROM Car c
           JOIN c.colors col
           WHERE c.model.modelId = :modelId
           AND c.series.seriesId = :seriesId
           AND col.colorId = :colorId
           AND LOWER(c.carName) = LOWER(:carName)
           """)
    Optional<Car> findByModelAndSeriesAndColorAndName(
            @Param("modelId") Long modelId,
            @Param("seriesId") Long seriesId,
            @Param("colorId") Long colorId,
            @Param("carName") String carName);

    boolean existsById(Long carId);

    @EntityGraph(attributePaths = {
        "model", "series", "colors", "carImages",
        "comfort", "safety", "powertrain", "evSpecs",
        "warranty", "dimensions", "specsCommon"
    })
   @Query("SELECT c FROM Car c WHERE c.status != 'INACTIVE'")
List<Car> findAllActive();
boolean existsByModel_ModelId(Long modelId);
boolean existsBySeries_SeriesId(Long seriesId);
}