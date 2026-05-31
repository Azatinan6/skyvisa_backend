package com.skyvisa.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skyvisa.dto.RawFlightDto; // Sizin projenizdeki DTO'nun adresi (Dto mu DTO mu yazıldığına dikkat et)
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j // Konsola profesyonel log basmak için
@Service
public class FlightApiService {

    // Veritabanı (veya JSON) verisini RAM'de (hafızada) tutacağımız ana liste
    private List<RawFlightDto> cachedFlights = new ArrayList<>();

    // @PostConstruct: Uygulama ilk ayağa kalktığında bu metot sadece BİR KERE
    // otomatik çalışır.
    @PostConstruct
    public void loadMockData() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            InputStream inputStream = new ClassPathResource("mock-flights.json").getInputStream();
            cachedFlights = mapper.readValue(inputStream, new TypeReference<List<RawFlightDto>>() {
            });
            log.info("Mock uçuş verileri başarıyla hafızaya yüklendi. Toplam kayıt: {}", cachedFlights.size());
        } catch (Exception e) {
            log.error("Mock veri okunurken kritik bir hata oluştu: ", e);
        }
    }

    // Arama metodu artık dosyaya gitmez, doğrudan hafızadaki 'cachedFlights'
    // listesini filtreler
    public List<RawFlightDto> getFlights(String origin, String destination, String date, String imgUrl) {

        return cachedFlights.stream()
                // 1. Kalkış noktası filtresi (Zorunlu)
                .filter(f -> origin == null || f.getOrigin().equalsIgnoreCase(origin))
                // 2. Varış noktası filtresi (Eğer null değilse kontrol et)
                .filter(f -> destination == null || f.getDestination().equalsIgnoreCase(destination))
                // 3. Tarih filtresi
                // .filter(f -> date == null || f.getDepartureDate().equals(date))
                // 4. Resim URL filtresi
                .filter(f -> imgUrl == null || f.getImageUrl().equalsIgnoreCase(imgUrl))
                .collect(Collectors.toList());
    }
}