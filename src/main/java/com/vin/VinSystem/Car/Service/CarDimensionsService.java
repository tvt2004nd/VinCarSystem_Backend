package com.vin.VinSystem.Car.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vin.VinSystem.Car.DTO.CarDimensionsCreateDTO;
import com.vin.VinSystem.Car.DTO.CarDimensionsResponseDTO;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Car.Entity.CarDimensions;
import com.vin.VinSystem.Car.Repository.CarDimensionsRepository;
import com.vin.VinSystem.Car.Repository.CarRepository;
import com.vin.VinSystem.Car.Controller.exception.ResourceNotFoundException;

@Service
@Transactional
public class CarDimensionsService {

    private final CarDimensionsRepository carDimensionsRepository;
    private final CarRepository carRepository;

    public CarDimensionsService(CarDimensionsRepository carDimensionsRepository, CarRepository carRepository) {
        this.carDimensionsRepository = carDimensionsRepository;
        this.carRepository = carRepository;
    }

    public CarDimensionsResponseDTO addOrUpdateCarDimensions(Long carId, CarDimensionsCreateDTO dto) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        CarDimensions dims = car.getDimensions();
        if (dims == null) {
            dims = new CarDimensions();
            dims.setCar(car);
        }

        dims.setLengthMm(dto.getLengthMm());
        dims.setWidthMm(dto.getWidthMm());
        dims.setHeightMm(dto.getHeightMm());
        dims.setWheelbaseMm(dto.getWheelbaseMm());
        dims.setCurbWeightKg(dto.getCurbWeightKg());
        dims.setTrunkVolumeL(dto.getTrunkVolumeL());

        CarDimensions saved = carDimensionsRepository.save(dims);
        car.setDimensions(saved);
        carRepository.save(car);

        return convertToResponseDTO(saved);
    }

    public CarDimensionsResponseDTO getCarDimensions(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        if (car.getDimensions() == null) {
            throw new ResourceNotFoundException("Không tìm thấy thông tin dimensions cho xe này");
        }
        return convertToResponseDTO(car.getDimensions());
    }

    public void deleteCarDimensions(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        if (car.getDimensions() != null) {
            carDimensionsRepository.delete(car.getDimensions());
            car.setDimensions(null);
            carRepository.save(car);
        }
    }

    private CarDimensionsResponseDTO convertToResponseDTO(CarDimensions dims) {
        return new CarDimensionsResponseDTO(
                dims.getCarId(),
                dims.getLengthMm(),
                dims.getWidthMm(),
                dims.getHeightMm(),
                dims.getWheelbaseMm(),
                dims.getCurbWeightKg(),
                dims.getTrunkVolumeL()
        );
    }
}