package com.skyvisa.controller;

import com.skyvisa.entity.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import com.skyvisa.dto.FlightRecommendationDTO;
import com.skyvisa.entity.Country;
import com.skyvisa.entity.Trip;
import com.skyvisa.repository.CountryRepository;
import com.skyvisa.repository.TripRepository;
import com.skyvisa.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/trips")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class TripController {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;

    // 1. Kullanıcının Tatilini Kaydet
    // 1. Düz String yerine ResponseEntity<?> yapıyoruz ki JSON dönebilelim
    @PostMapping("/save")
    public ResponseEntity<?> saveTrip(@RequestBody FlightRecommendationDTO dto, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).get();
        Country country = countryRepository.findByName(dto.getDestinationCountry());
        String city = dto.getDestinationCity();
        
        if (tripRepository.existsByUserAndDestinationCountryAndDestinationCity(user, country, city)) {
            // Hata mesajını da JSON formatında yollamak her zaman daha sağlıklıdır
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Bu rota zaten favorilerinizde ekli!"));
        }

        Trip trip = new Trip();
        trip.setUser(user);
        trip.setDestinationCountry(country);
        trip.setDestinationCity(city);
        trip.setBudget(dto.getPrice());

        // 2. KAYIT İŞLEMİ: save metodu, veritabanında oluşan ID'yi içeren güncel objeyi
        // geri verir
        Trip savedTrip = tripRepository.save(trip);

        // 3. KRİTİK NOKTA: Sadece mesajı değil, kaydedilen objeyi dönüyoruz.
        // Böylece React "response.data.id" ile gerçek ID'yi yakalayabilecek.
        return ResponseEntity.ok(savedTrip);
    }

    @GetMapping("/myTrips")
    public ResponseEntity<List<Trip>> getMyTrips(Principal principal) {

        // 1. İsteği atan kullanıcıyı bul
        User user = userRepository.findByEmail(principal.getName()).get();

        // 2. O kullanıcıya ait tatilleri veritabanından çek
        List<Trip> trips = tripRepository.findByUser(user);

        // 3. React'e gönder
        return ResponseEntity.ok(trips);
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<String> deleteTrip(@PathVariable @NonNull Long tripId, Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).get();

        // Silinmek istenen tatili bul
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Tatil bulunamadı"));

        // Güvenlik: Kullanıcı başkasının tatilini silemesin
        if (!trip.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Yetkisiz işlem!");
        }

        tripRepository.delete(trip);
        return ResponseEntity.ok("Tatil başarıyla silindi!");
    }
}
