package com.vin.VinSystem.Car.Entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "car")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Long carId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id")
    private Model model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private CarSeries series;

    // ✅ Đổi @ManyToOne + @JoinColumn sai thành @ManyToMany đúng
@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(
    name = "car_color_mapping",
    joinColumns = @JoinColumn(name = "car_id"),
    inverseJoinColumns = @JoinColumn(name = "color_id")
)
private Set<CarColor> colors = new HashSet<>();

    @Column(name = "car_name", nullable = false, length = 255)
    private String carName;

    @Column(name = "price", precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "year_of_manufacture")
    private Integer yearOfManufacture;

    @Column(name = "status", length = 50)
    private String status;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CarImage> carImages;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CarComfort comfort;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CarSafety safety;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CarPowertrain powertrain;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CarEvSpecs evSpecs;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CarWarranty warranty;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private CarDimensions dimensions;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private SpecsCommon specsCommon;

    // --- Getters & Setters ---

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }

    public Model getModel() { return model; }
    public void setModel(Model model) { this.model = model; }

    public CarSeries getSeries() { return series; }
    public void setSeries(CarSeries series) { this.series = series; }

    // ✅ Đổi getColor/setColor → getColors/setColors
public Set<CarColor> getColors() { return colors; }
public void setColors(Set<CarColor> colors) { this.colors = colors; }

    public String getCarName() { return carName; }
    public void setCarName(String carName) { this.carName = carName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getYearOfManufacture() { return yearOfManufacture; }
    public void setYearOfManufacture(Integer yearOfManufacture) { this.yearOfManufacture = yearOfManufacture; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<CarImage> getCarImages() { return carImages; }
    public void setCarImages(List<CarImage> carImages) { this.carImages = carImages; }

    public CarComfort getComfort() { return comfort; }
    public void setComfort(CarComfort comfort) { this.comfort = comfort; }

    public CarSafety getSafety() { return safety; }
    public void setSafety(CarSafety safety) { this.safety = safety; }

    public CarPowertrain getPowertrain() { return powertrain; }
    public void setPowertrain(CarPowertrain powertrain) { this.powertrain = powertrain; }

    public CarEvSpecs getEvSpecs() { return evSpecs; }
    public void setEvSpecs(CarEvSpecs evSpecs) { this.evSpecs = evSpecs; }

    public CarWarranty getWarranty() { return warranty; }
    public void setWarranty(CarWarranty warranty) { this.warranty = warranty; }

    public CarDimensions getDimensions() { return dimensions; }
    public void setDimensions(CarDimensions dimensions) { this.dimensions = dimensions; }

    public SpecsCommon getSpecsCommon() { return specsCommon; }
    public void setSpecsCommon(SpecsCommon specsCommon) { this.specsCommon = specsCommon; }
}