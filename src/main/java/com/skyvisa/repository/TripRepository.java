package com.skyvisa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skyvisa.entity.Country;
import com.skyvisa.entity.Trip;
import com.skyvisa.entity.User;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByUser(User user);

    boolean existsByUserAndDestinationCountryAndDestinationCity(User user, Country destinationCountry, Country destinationCity);
}
