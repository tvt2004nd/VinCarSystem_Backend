package com.vin.VinSystem.Car.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vin.VinSystem.Car.DTO.CarPowertrainCreateDTO;
import com.vin.VinSystem.Car.DTO.CarPowertrainResponseDTO;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Car.Entity.CarPowertrain;
import com.vin.VinSystem.Car.Repository.CarPowertrainRepository;
import com.vin.VinSystem.Car.Repository.CarRepository;
import com.vin.VinSystem.Car.Controller.exception.ResourceNotFoundException;

@Service
@Transactional
public class CarPowertrainService {

    private final CarPowertrainRepository carPowertrainRepository;
    private final CarRepository carRepository;

    public CarPowertrainService(CarPowertrainRepository carPowertrainRepository, CarRepository carRepository) {
        this.carPowertrainRepository = carPowertrainRepository;
        this.carRepository = carRepository;
    }

    /**
     * Thêm hoặc cập nhật CarPowertrain
     */
    public CarPowertrainResponseDTO addOrUpdateCarPowertrain(Long carId, CarPowertrainCreateDTO powertrainDTO) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        CarPowertrain powertrain = car.getPowertrain();
        
        if (powertrain == null) {
            // Tạo mới
            powertrain = new CarPowertrain();
            powertrain.setCar(car);
        }
        
        // Update fields
        powertrain.setDriveType(powertrainDTO.getDriveType());
        powertrain.setMaxPowerHp(powertrainDTO.getMaxPowerHp());
        powertrain.setMaxTorqueNm(powertrainDTO.getMaxTorqueNm());
        powertrain.setAcceleration0100Sec(powertrainDTO.getAcceleration0100Sec());
        powertrain.setTopSpeedKmh(powertrainDTO.getTopSpeedKmh());
        
        CarPowertrain savedPowertrain = carPowertrainRepository.save(powertrain);
        car.setPowertrain(savedPowertrain);
        carRepository.save(car);
        
        return convertToResponseDTO(savedPowertrain);
    }

    /**
     * Lấy Powertrain theo CarId
     */
    public CarPowertrainResponseDTO getCarPowertrain(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        if (car.getPowertrain() == null) {
            throw new ResourceNotFoundException("Không tìm thấy thông tin Powertrain cho xe này");
        }

        return convertToResponseDTO(car.getPowertrain());
    }

    /**
     * Xóa Powertrain
     */
    public void deleteCarPowertrain(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        if (car.getPowertrain() != null) {
            carPowertrainRepository.delete(car.getPowertrain());
            car.setPowertrain(null);
            carRepository.save(car);
        }
    }

    private CarPowertrainResponseDTO convertToResponseDTO(CarPowertrain powertrain) {
        return new CarPowertrainResponseDTO(
                powertrain.getCarId(),
                powertrain.getDriveType(),
                powertrain.getMaxPowerHp(),
                powertrain.getMaxTorqueNm(),
                powertrain.getAcceleration0100Sec(),
                powertrain.getTopSpeedKmh()
        );
    }
}
