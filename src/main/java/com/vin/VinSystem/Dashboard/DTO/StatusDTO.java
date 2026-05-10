package com.vin.VinSystem.Dashboard.DTO;

public class StatusDTO {

    private String name;
    private Long value;

    public StatusDTO(String name, Long value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }
    public Long getValue() { return value; }
}