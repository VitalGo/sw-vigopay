package com.othr.swvigopay.entity;

import javax.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Address {

    // Attributes
    // -----------------------------------------------------
    private String street;
    private int houseNr;
    private int zipCode;
    private String city;
    private String country;

    // Constructors
    // -----------------------------------------------------
    public Address() {}

    public Address(String street, int houseNr, int zipCode, String city, String country) {
        this.street = street;
        this.houseNr = houseNr;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
    }

    // Methods
    // -----------------------------------------------------
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getHouseNr() {
        return houseNr;
    }

    public void setHouseNr(int houseNr) {
        this.houseNr = houseNr;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
