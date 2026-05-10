package com.vin.VinSystem.Car.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vin.VinSystem.Car.DTO.CarComfortCreateDTO;
import com.vin.VinSystem.Car.DTO.CarComfortResponseDTO;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Car.Entity.CarComfort;
import com.vin.VinSystem.Car.Repository.CarComfortRepository;
import com.vin.VinSystem.Car.Repository.CarRepository;
import com.vin.VinSystem.Car.Controller.exception.ResourceNotFoundException;

@Service
@Transactional
public class CarComfortService {

    private final CarComfortRepository carComfortRepository;
    private final CarRepository carRepository;

    public CarComfortService(CarComfortRepository carComfortRepository, CarRepository carRepository) {
        this.carComfortRepository = carComfortRepository;
        this.carRepository = carRepository;
    }

    /**
     * Thêm hoặc cập nhật CarComfort
     */
    public CarComfortResponseDTO addOrUpdateCarComfort(Long carId, CarComfortCreateDTO comfortDTO) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        CarComfort comfort = car.getComfort();
        
        if (comfort == null) {
            // Tạo mới
            comfort = new CarComfort();
            comfort.setCar(car);
        }
        
        // Update fields
        comfort.setInfotainmentScreenInch(comfortDTO.getInfotainmentScreenInch());
        comfort.setSpeakerCount(comfortDTO.getSpeakerCount());
        comfort.setClimateControl(comfortDTO.getClimateControl());
        comfort.setSeatMaterial(comfortDTO.getSeatMaterial());
        comfort.setSunroof(comfortDTO.getSunroof());
        comfort.setWirelessCarplay(comfortDTO.getWirelessCarplay());
        
        CarComfort savedComfort = carComfortRepository.save(comfort);
        car.setComfort(savedComfort);
        carRepository.save(car);
        
        return convertToResponseDTO(savedComfort);
    }

    /**
     * Lấy Comfort theo CarId
     */
    public CarComfortResponseDTO getCarComfort(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        if (car.getComfort() == null) {
            throw new ResourceNotFoundException("Không tìm thấy thông tin Comfort cho xe này");
        }

        return convertToResponseDTO(car.getComfort());
    }

    /**
     * Xóa Comfort
     */
    public void deleteCarComfort(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        if (car.getComfort() != null) {
            carComfortRepository.delete(car.getComfort());
            car.setComfort(null);
            carRepository.save(car);
        }
    }

    private CarComfortResponseDTO convertToResponseDTO(CarComfort comfort) {
        return new CarComfortResponseDTO(
                comfort.getCarId(),
                comfort.getInfotainmentScreenInch(),
                comfort.getSpeakerCount(),
                comfort.getClimateControl(),
                comfort.getSeatMaterial(),
                comfort.getSunroof(),
                comfort.getWirelessCarplay()
        );
    }
}
