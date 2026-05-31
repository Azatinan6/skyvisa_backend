package com.skyvisa.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.skyvisa.dto.FlightRecommendationDTO;
import com.skyvisa.service.TripAdvisorService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("api/v1/flights")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class FlightController {

    private final TripAdvisorService tripAdvisorService;

    @GetMapping("/search")
    public ResponseEntity<List<FlightRecommendationDTO>> searchFlights(
            @RequestParam String origin,
            @RequestParam String date,
            @RequestParam(required = false, defaultValue = "100000") Double maxBudget,
            @RequestParam(required = false, defaultValue = "false") boolean onlyVisaFree,
            @RequestParam(required = false) String imageUrl) {
        // Servisimizi çağırıp veriyi alıyoruz
        List<FlightRecommendationDTO> recommendations = tripAdvisorService.getVacationSuggestions(
                origin, date, maxBudget, onlyVisaFree, imageUrl);

        // Eğer liste boş dönerse 204 No Content veya 200 OK ile boş liste dönebiliriz.
        // ResponseEntity.ok() burada kullanıcıya veriyi başarıyla sunduğumuzu belirtir.
        return ResponseEntity.ok(recommendations);
    }
}