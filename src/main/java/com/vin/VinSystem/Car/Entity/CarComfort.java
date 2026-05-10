package com.vin.VinSystem.Car.Entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "car_comfort")
public class CarComfort {

    @Id
    @Column(name = "car_id")
    private Long carId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "car_id")
    private Car car;

    @Column(name = "infotainment_screen_inch", precision = 6, scale = 2)
    private BigDecimal infotainmentScreenInch;

    @Column(name = "speaker_count")
    private Integer speakerCount;

    @Column(name = "climate_control")
    private Boolean climateControl;

    @Column(name = "seat_material", length = 100)
    private String seatMaterial;

    @Column(name = "sunroof")
    private Boolean sunroof;

    @Column(name = "wireless_carplay")
    private Boolean wirelessCarplay;

    // Constructor rỗng (bắt buộc cho JPA)
    public CarComfort() {
    }

    // Constructor đầy đủ
    public CarComfort(Long carId,
                      Car car,
                      BigDecimal infotainmentScreenInch,
                      Integer speakerCount,
                      Boolean climateControl,
                      String seatMaterial,
                      Boolean sunroof,
                      Boolean wirelessCarplay) {
        this.carId = carId;
        this.car = car;
        this.infotainmentScreenInch = infotainmentScreenInch;
        this.speakerCount = speakerCount;
        this.climateControl = climateControl;
        this.seatMaterial = seatMaterial;
        this.sunroof = sunroof;
        this.wirelessCarplay = wirelessCarplay;
    }

    // Getter & Setter

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public BigDecimal getInfotainmentScreenInch() {
        return infotainmentScreenInch;
    }

    public void setInfotainmentScreenInch(BigDecimal infotainmentScreenInch) {
        this.infotainmentScreenInch = infotainmentScreenInch;
    }

    public Integer getSpeakerCount() {
        return speakerCount;
    }

    public void setSpeakerCount(Integer speakerCount) {
        this.speakerCount = speakerCount;
    }

    public Boolean getClimateControl() {
        return climateControl;
    }

    public void setClimateControl(Boolean climateControl) {
        this.climateControl = climateControl;
    }

    public String getSeatMaterial() {
        return seatMaterial;
    }

    public void setSeatMaterial(String seatMaterial) {
        this.seatMaterial = seatMaterial;
    }

    public Boolean getSunroof() {
        return sunroof;
    }

    public void setSunroof(Boolean sunroof) {
        this.sunroof = sunroof;
    }

    public Boolean getWirelessCarplay() {
        return wirelessCarplay;
    }

    public void setWirelessCarplay(Boolean wirelessCarplay) {
        this.wirelessCarplay = wirelessCarplay;
    }
}