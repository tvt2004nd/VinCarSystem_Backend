package com.vin.VinSystem.Car.Entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "model_color_options")

public class ModelColorOption {

  @EmbeddedId
  private ModelColorOptionId id;

  @MapsId("modelId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "model_id", nullable = false)
  private Model model;

  @MapsId("colorId")
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "color_id", nullable = false)
  private CarColor color;

  @Column(name = "extra_price", precision = 15, scale = 2)
  private BigDecimal extraPrice = BigDecimal.ZERO;

    public ModelColorOptionId getId() {
        return id;
    }

    public void setId(ModelColorOptionId id) {
        this.id = id;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public CarColor getColor() {
        return color;
    }

    public void setColor(CarColor color) {
        this.color = color;
    }

    public BigDecimal getExtraPrice() {
        return extraPrice;
    }

    public void setExtraPrice(BigDecimal extraPrice) {
        this.extraPrice = extraPrice;
    }

}