package com.vin.VinSystem.Car.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vin.VinSystem.Car.Entity.CarImage;
import java.util.List;

@Repository
public interface CarImageRepository extends JpaRepository<CarImage, Long> {
    /**
     * Tìm tất cả ảnh của một chiếc xe
     */
    List<CarImage> findByCar_CarId(Long carId);

    /**
     * Tìm ảnh chính của xe
     */
    CarImage findByCar_CarIdAndIsPrimaryTrue(Long carId);

    /**
     * Lấy danh sách URL ảnh của một chiếc xe (không trigger lazy load)
     */
    @Query("SELECT ci.imageUrl FROM CarImage ci WHERE ci.car.carId = :carId")
    List<String> findImageUrlsByCarId(@Param("carId") Long carId);

    /**
     * Xóa tất cả ảnh của một chiếc xe
     */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM CarImage ci WHERE ci.car.carId = :carId")
    void deleteByCar_CarId(@Param("carId") Long carId);
}
