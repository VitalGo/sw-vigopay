package com.othr.swvigopay.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class User extends BaseEntity implements UserDetails {

    // Attributes
    // -----------------------------------------------------
    @OneToOne(mappedBy = "user")
    private Account account;

    private String company;
    private String forename;
    private String surname;

    @Column(unique = true)
    private String email;
    private String password;
    private Address address;
    private String iban;

    @Column(columnDefinition = "boolean default true")
    private boolean isActive;

    // Constructors
    // -----------------------------------------------------
    public User() {
        this.isActive = true;
    }

    public User(String company, String forename, String surname, String street, int houseNr, int zipCode, String city, String country, String email, String password, String iban) {
        this();
        this.company = company;
        this.forename = forename;
        this.surname = surname;
        this.address = new Address(street, houseNr, zipCode, city, country);
        this.email = email;
        this.password = password;
        this.iban = iban;
    }

    // Methods
    // -----------------------------------------------------
    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isActive;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isActive;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isActive;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive;
    }

    public String generateUsername() {
        String username = "";

        if(this.company != null && !this.company.equals(""))
            username = this.company;

        if(this.forename != null && this.surname != null && !this.forename.equals("") && !this.surname.equals("") && !username.equals(""))
            username += " - " + this.forename + " " + this.surname;
        else if(this.forename != null && this.surname != null && !this.forename.equals("") && !this.surname.equals("") && username.equals(""))
            username = this.forename + " " + this.surname;

        return username;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String bank) {
        this.iban = bank;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getCompany() {
        if(this.company == "")
            return null;
        return company;
    }

    public void setCompany(String firma) {
        this.company = firma;
    }
}
