package com.vin.VinSystem.Branch.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;


@Entity
@Table(name = "branch")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branch_id")
    private Long branchId;

    @Column(name = "branch_name", nullable = false, length = 255)
    private String branchName;

    @Lob
    @Column(name = "location")
    private String location;

    @Lob
    @Column(name = "contact_info")
    private String contactInfo;

    // ================= NEW FIELDS =================

@Column(name = "latitude")
private Double latitude;

@Column(name = "longitude")
private Double longitude;

    // ================= CONSTRUCTOR =================

    public Branch() {}

    public Branch(Long branchId,
                  String branchName,
                  String location,
                  String contactInfo,
                  Double latitude,
                  Double longitude) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.location = location;
        this.contactInfo = contactInfo;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // ================= GETTER SETTER =================

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}