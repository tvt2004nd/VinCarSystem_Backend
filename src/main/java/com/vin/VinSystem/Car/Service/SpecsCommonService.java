package com.vin.VinSystem.Car.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vin.VinSystem.Car.DTO.SpecsCommonCreateDTO;
import com.vin.VinSystem.Car.DTO.SpecsCommonResponseDTO;
import com.vin.VinSystem.Car.Entity.Car;
import com.vin.VinSystem.Car.Entity.SpecsCommon;
import com.vin.VinSystem.Car.Repository.CarRepository;
import com.vin.VinSystem.Car.Repository.SpecsCommonRepository;
import com.vin.VinSystem.Car.Controller.exception.ResourceNotFoundException;

@Service
@Transactional
public class SpecsCommonService {
    private final SpecsCommonRepository specsCommonRepository;
    private final CarRepository carRepository;

    public SpecsCommonService(SpecsCommonRepository specsCommonRepository, CarRepository carRepository) {
        this.specsCommonRepository = specsCommonRepository;
        this.carRepository = carRepository;
    }

    public SpecsCommonResponseDTO addOrUpdateSpecsCommon(Long carId, SpecsCommonCreateDTO dto) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        SpecsCommon specs = car.getSpecsCommon();
        if (specs == null) {
            specs = new SpecsCommon();
            specs.setCar(car);
        }

        specs.setBodyType(dto.getBodyType());
        specs.setSeatingCapacity(dto.getSeatingCapacity());
        specs.setDoors(dto.getDoors());
        specs.setFuelType(dto.getFuelType());

        SpecsCommon saved = specsCommonRepository.save(specs);
        car.setSpecsCommon(saved);
        carRepository.save(car);

        return convertToResponseDTO(saved);
    }

    public SpecsCommonResponseDTO getSpecsCommon(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));
        if (car.getSpecsCommon() == null) {
            throw new ResourceNotFoundException("Không tìm thấy thông tin specs common cho xe này");
        }
        return convertToResponseDTO(car.getSpecsCommon());
    }

    public void deleteSpecsCommon(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe với ID: " + carId));

        if (car.getSpecsCommon() != null) {
            specsCommonRepository.delete(car.getSpecsCommon());
            car.setSpecsCommon(null);
            carRepository.save(car);
        }
    }

    private SpecsCommonResponseDTO convertToResponseDTO(SpecsCommon specs) {
        return new SpecsCommonResponseDTO(
                specs.getCarId(),
                specs.getBodyType(),
                specs.getSeatingCapacity(),
                specs.getDoors(),
                specs.getFuelType()
        );
    }
}