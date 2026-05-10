package com.vin.VinSystem.Car.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vin.VinSystem.Car.DTO.CarEvSpecsCreateDTO;
import com.vin.VinSystem.Car.DTO.CarEvSpecsResponseDTO;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Car.Entity.CarEvSpecs;
import com.vin.VinSystem.Car.Repository.CarEvSpecsRepository;
import com.vin.VinSystem.Car.Repository.CarRepository;
import com.vin.VinSystem.Car.Controller.exception.ResourceNotFoundException;

@Service
@Transactional
public class CarEvSpecsService {

    private final CarEvSpecsRepository carEvSpecsRepository;
    private final CarRepository carRepository;

    public CarEvSpecsService(CarEvSpecsRepository carEvSpecsRepository, CarRepository carRepository) {
        this.carEvSpecsRepository = carEvSpecsRepository;
        this.carRepository = carRepository;
    }

    /**
     * Thêm hoặc cập nhật CarEvSpecs
     */
    public CarEvSpecsResponseDTO addOrUpdateCarEvSpecs(Long carId, CarEvSpecsCreateDTO evSpecsDTO) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        CarEvSpecs evSpecs = car.getEvSpecs();
        
        if (evSpecs == null) {
            // Tạo mới
            evSpecs = new CarEvSpecs();
            evSpecs.setCar(car);
        }
        
        // Update fields
        evSpecs.setBatteryCapacityKwh(evSpecsDTO.getBatteryCapacityKwh());
        evSpecs.setRangeKm(evSpecsDTO.getRangeKm());
        evSpecs.setAcChargingKw(evSpecsDTO.getAcChargingKw());
        evSpecs.setDcFastChargingKw(evSpecsDTO.getDcFastChargingKw());
        evSpecs.setFastChargeTimeMin(evSpecsDTO.getFastChargeTimeMin());
        
        CarEvSpecs savedEvSpecs = carEvSpecsRepository.save(evSpecs);
        car.setEvSpecs(savedEvSpecs);
        carRepository.save(car);
        
        return convertToResponseDTO(savedEvSpecs);
    }

    /**
     * Lấy EvSpecs theo CarId
     */
    public CarEvSpecsResponseDTO getCarEvSpecs(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        if (car.getEvSpecs() == null) {
            throw new ResourceNotFoundException("Không tìm thấy thông tin EV Specs cho xe này");
        }

        return convertToResponseDTO(car.getEvSpecs());
    }

    /**
     * Xóa EvSpecs
     */
    public void deleteCarEvSpecs(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        if (car.getEvSpecs() != null) {
            carEvSpecsRepository.delete(car.getEvSpecs());
            car.setEvSpecs(null);
            carRepository.save(car);
        }
    }

    private CarEvSpecsResponseDTO convertToResponseDTO(CarEvSpecs evSpecs) {
        return new CarEvSpecsResponseDTO(
                evSpecs.getCarId(),
                evSpecs.getBatteryCapacityKwh(),
                evSpecs.getRangeKm(),
                evSpecs.getAcChargingKw(),
                evSpecs.getDcFastChargingKw(),
                evSpecs.getFastChargeTimeMin()
        );
    }
}
