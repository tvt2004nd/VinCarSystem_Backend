package com.vin.VinSystem.Car.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vin.VinSystem.Car.DTO.CarSafetyCreateDTO;
import com.vin.VinSystem.Car.DTO.CarSafetyResponseDTO;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Car.Entity.CarSafety;
import com.vin.VinSystem.Car.Repository.CarRepository;
import com.vin.VinSystem.Car.Repository.CarSafetyRepository;
import com.vin.VinSystem.Car.Controller.exception.ResourceNotFoundException;

@Service
@Transactional
public class CarSafetyService {

    private final CarSafetyRepository carSafetyRepository;
    private final CarRepository carRepository;

    public CarSafetyService(CarSafetyRepository carSafetyRepository, CarRepository carRepository) {
        this.carSafetyRepository = carSafetyRepository;
        this.carRepository = carRepository;
    }

    /**
     * Thêm hoặc cập nhật CarSafety
     */
    public CarSafetyResponseDTO addOrUpdateCarSafety(Long carId, CarSafetyCreateDTO safetyDTO) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        CarSafety safety = car.getSafety();
        
        if (safety == null) {
            // Tạo mới
            safety = new CarSafety();
            safety.setCar(car);
        }
        
        // Update fields
        safety.setAirbags(safetyDTO.getAirbags());
        safety.setAbs(safetyDTO.getAbs());
        safety.setEsc(safetyDTO.getEsc());
        safety.setTractionControl(safetyDTO.getTractionControl());
        safety.setLaneKeepAssist(safetyDTO.getLaneKeepAssist());
        safety.setAdaptiveCruiseControl(safetyDTO.getAdaptiveCruiseControl());
        safety.setRearCamera(safetyDTO.getRearCamera());
        safety.setParkingSensors(safetyDTO.getParkingSensors());
        
        CarSafety savedSafety = carSafetyRepository.save(safety);
        car.setSafety(savedSafety);
        carRepository.save(car);
        
        return convertToResponseDTO(savedSafety);
    }

    /**
     * Lấy Safety theo CarId
     */
    public CarSafetyResponseDTO getCarSafety(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        if (car.getSafety() == null) {
            throw new ResourceNotFoundException("Không tìm thấy thông tin Safety cho xe này");
        }

        return convertToResponseDTO(car.getSafety());
    }

    /**
     * Xóa Safety
     */
    public void deleteCarSafety(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        if (car.getSafety() != null) {
            carSafetyRepository.delete(car.getSafety());
            car.setSafety(null);
            carRepository.save(car);
        }
    }

    private CarSafetyResponseDTO convertToResponseDTO(CarSafety safety) {
        return new CarSafetyResponseDTO(
                safety.getCarId(),
                safety.getAirbags(),
                safety.getAbs(),
                safety.getEsc(),
                safety.getTractionControl(),
                safety.getLaneKeepAssist(),
                safety.getAdaptiveCruiseControl(),
                safety.getRearCamera(),
                safety.getParkingSensors()
        );
    }
}
