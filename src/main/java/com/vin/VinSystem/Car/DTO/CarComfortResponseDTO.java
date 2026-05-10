package com.vin.VinSystem.Car.DTO;

import java.math.BigDecimal;

public class CarComfortResponseDTO {

    private Long carId;

    private BigDecimal infotainmentScreenInch;

    private Integer speakerCount;

    private Boolean climateControl;

    private String seatMaterial;

    private Boolean sunroof;

    private Boolean wirelessCarplay;

    public CarComfortResponseDTO() {
    }

    public CarComfortResponseDTO(Long carId, BigDecimal infotainmentScreenInch,
                                Integer speakerCount, Boolean climateControl,
                                String seatMaterial, Boolean sunroof,
                                Boolean wirelessCarplay) {
        this.carId = carId;
        this.infotainmentScreenInch = infotainmentScreenInch;
        this.speakerCount = speakerCount;
        this.climateControl = climateControl;
        this.seatMaterial = seatMaterial;
        this.sunroof = sunroof;
        this.wirelessCarplay = wirelessCarplay;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
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
