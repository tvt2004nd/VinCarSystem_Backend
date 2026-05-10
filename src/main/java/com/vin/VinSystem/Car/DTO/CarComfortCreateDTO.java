package com.vin.VinSystem.Car.DTO;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class CarComfortCreateDTO {

    private BigDecimal infotainmentScreenInch;

    @Min(value = 0, message = "Số loa phải lớn hơn hoặc bằng 0")
    private Integer speakerCount;

    private Boolean climateControl;

    @Size(max = 100, message = "Vật liệu ghế tối đa 100 ký tự")
    private String seatMaterial;

    private Boolean sunroof;

    private Boolean wirelessCarplay;

    public CarComfortCreateDTO() {
    }

    public CarComfortCreateDTO(BigDecimal infotainmentScreenInch, Integer speakerCount,
                              Boolean climateControl, String seatMaterial,
                              Boolean sunroof, Boolean wirelessCarplay) {
        this.infotainmentScreenInch = infotainmentScreenInch;
        this.speakerCount = speakerCount;
        this.climateControl = climateControl;
        this.seatMaterial = seatMaterial;
        this.sunroof = sunroof;
        this.wirelessCarplay = wirelessCarplay;
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
