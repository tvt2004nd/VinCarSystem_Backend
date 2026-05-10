package com.vin.VinSystem.Car.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vin.VinSystem.Car.DTO.CarWarrantyCreateDTO;
import com.vin.VinSystem.Car.DTO.CarWarrantyResponseDTO;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Car.Entity.CarWarranty;
import com.vin.VinSystem.Car.Repository.CarRepository;
import com.vin.VinSystem.Car.Repository.CarWarrantyRepository;
import com.vin.VinSystem.Car.Controller.exception.ResourceNotFoundException;

@Service
@Transactional
public class CarWarrantyService {

    private final CarWarrantyRepository carWarrantyRepository;
    private final CarRepository carRepository;

    public CarWarrantyService(CarWarrantyRepository carWarrantyRepository, CarRepository carRepository) {
        this.carWarrantyRepository = carWarrantyRepository;
        this.carRepository = carRepository;
    }

    /**
     * Thêm hoặc cập nhật CarWarranty
     */
    public CarWarrantyResponseDTO addOrUpdateCarWarranty(Long carId, CarWarrantyCreateDTO warrantyDTO) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        CarWarranty warranty = car.getWarranty();
        
        if (warranty == null) {
            // Tạo mới
            warranty = new CarWarranty();
            // id will be inherited from associated car via @MapsId
            warranty.setCar(car);
        }
    
        // Update fields
        warranty.setWarrantyYears(warrantyDTO.getWarrantyYears());
        warranty.setWarrantyKm(warrantyDTO.getWarrantyKm());
        warranty.setBatteryWarrantyYears(warrantyDTO.getBatteryWarrantyYears());
        warranty.setBatteryWarrantyKm(warrantyDTO.getBatteryWarrantyKm());
        
        CarWarranty savedWarranty = carWarrantyRepository.save(warranty);
        car.setWarranty(savedWarranty);
        carRepository.save(car);
        
        return convertToResponseDTO(savedWarranty);
    }

    /**
     * Lấy Warranty theo CarId
     */
    public CarWarrantyResponseDTO getCarWarranty(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        if (car.getWarranty() == null) {
            throw new ResourceNotFoundException("Không tìm thấy thông tin Warranty cho xe này");
        }

        return convertToResponseDTO(car.getWarranty());
    }

    /**
     * Xóa Warranty
     */
    public void deleteCarWarranty(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        if (car.getWarranty() != null) {
            carWarrantyRepository.delete(car.getWarranty());
            car.setWarranty(null);
            carRepository.save(car);
        }
    }

    private CarWarrantyResponseDTO convertToResponseDTO(CarWarranty warranty) {
        return new CarWarrantyResponseDTO(
                warranty.getCarId(),
                warranty.getWarrantyYears(),
                warranty.getWarrantyKm(),
                warranty.getBatteryWarrantyYears(),
                warranty.getBatteryWarrantyKm()
        );
    }
}
