package com.skyvisa.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.skyvisa.dto.FlightRecommendationDTO;
import com.skyvisa.dto.RawFlightDto;
import com.skyvisa.entity.VisaRule;
import com.skyvisa.repository.VisaRuleRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TripAdvisorService {

    private final FlightApiService flightApiService;
    private final VisaRuleRepository visaRuleRepository;

    private String resolveImageUrl(String destination) {
    // Sıkıştırılmış JAR dosyasının içini okuyabilmek için ClassPathResource kullanıyoruz
    // Terminalde hepsini .jpg yaptığını gördüm, o yüzden sadece .jpg kontrolü yeterli
    ClassPathResource resource = new ClassPathResource("static/images/" + destination + ".jpg");
    
    if (resource.exists()) {
        return "/images/" + destination + ".jpg";
    }
    
    // O da yoksa, varsayılan bir resim döndür
    return "/images/default.jpg"; 
    }

    public List<FlightRecommendationDTO> getVacationSuggestions(String origin, String date, Double maxBudget, boolean onlyVisaFree, String imgUrl) {
        List<RawFlightDto> rawFlights = flightApiService.getFlights(origin, null, date, imgUrl);
        // --- LOG 1: Ham veri geldi mi? ---
        System.out.println("Gelen ham uçuş sayısı: " + rawFlights.size());

        Set<String> countryIsoCodes = rawFlights.stream()
                .map(flight -> flight.getDestinationCountryIso())
                .filter(code -> code != null)
                .collect(Collectors.toSet());

        // --- LOG 2: DB'den kural geldi mi? ---
        List<VisaRule> visaRules = visaRuleRepository.findByTargetCountry_IsoCodeIn(countryIsoCodes);
        System.out.println("DB'den dönen vize kuralı sayısı: " + visaRules.size());

        // --- LOG 3: Map içine ne doldu? ---
        Map<String, VisaRule> visaRuleMap = visaRules.stream()
                .collect(Collectors.toMap(
                        rule -> rule.getTargetCountry().getIsoCode(),
                        rule -> rule,
                        (kural1, kural2) -> kural1 // Çakışma olursa ilk kuralı tut, çökme!
                ));
        System.out.println("Map içeriği (Ülke kodları): " + visaRuleMap.keySet());

        return rawFlights.stream()

        // FİLTRE: Bütçe ve Vize Mantığı
        .filter(flight -> {
            boolean budgetOk = flight.getPrice() <= maxBudget;
            VisaRule rule = visaRuleMap.get(flight.getDestinationCountryIso());

            // Kullanıcı arayüzde "Sadece Vizesiz" butonuna BASTIYSA:
            if (onlyVisaFree) {
                // Kural DB'de mutlaka olmalı VE statüsü VISA_FREE olmalı
                return budgetOk && rule != null && rule.getStatus().equals("VISA_FREE");
            }

            // Kullanıcı "Sadece Vizesiz" DEMEDİYSE: Veritabanında (DB) kural olsun veya olmasın, bütçesi yeten tüm uçuşları göster.
            return budgetOk;
        })

        // DÖNÜŞTÜRME (DTO'ya Çevirme)
        .map(flight -> {
            VisaRule rule = visaRuleMap.get(flight.getDestinationCountryIso());
            
            // Varsayılan koruma duvarı: DB'de kural tanımlanmadıysa vize istiyor kabul et
            String status = "VİZE GEREKLİ"; 
            
            if (rule != null) {
                switch (rule.getStatus()) {
                    case "VISA_FREE":
                        status = "VİZESİZ";
                        break;
                    case "E_VISA":
                        status = "E-VİZE";
                        break;
                    case "SCHENGEN":
                    case "VISA_REQUIRED":
                        status = "VİZE GEREKLİ";
                        break;
                }
            }
            
        // DB'de ülke adı varsa tam adını (Örn: Fransa), yoksa ISO kodunu bas
        String countryName = (rule != null) ? rule.getTargetCountry().getName() : flight.getDestinationCountryIso();

        return FlightRecommendationDTO.builder()
                .destinationCity(flight.getDestination())
                .destinationCountry(countryName)
                .airline(flight.getAirline())
                .price(flight.getPrice())
                .visaStatus(status)
                .imageUrl(resolveImageUrl(flight.getDestination()))                            
                .build();
        })
        .sorted(Comparator.comparing(FlightRecommendationDTO::getPrice))
        .collect(Collectors.toList());
    }

}