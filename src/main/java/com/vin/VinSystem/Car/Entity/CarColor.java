package com.vin.VinSystem.Car.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "car_color")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class CarColor {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "color_id")
  private Long colorId;

  @Column(name = "color_name", nullable = false, length = 100)
  private String colorName;

  @Column(name = "color_code", length = 50)
  private String colorCode;

 
  public Long getColorId() {
    return colorId;
  }

  public void setColorId(Long colorId) {
    this.colorId = colorId;
  }

  public String getColorName() {
    return colorName;
  }

  public void setColorName(String colorName) {
    this.colorName = colorName;
  }

  public String getColorCode() {
    return colorCode;
  }

  public void setColorCode(String colorCode) {
    this.colorCode = colorCode;
  }
  
}