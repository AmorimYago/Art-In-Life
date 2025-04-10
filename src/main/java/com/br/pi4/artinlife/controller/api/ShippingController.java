package com.br.pi4.artinlife.controller.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/shipping")
public class ShippingController {

    @Value("${melhorenvio.token}")
    private String melhorEnvioToken;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/calculate")
    public ResponseEntity<String> calcularFrete(@RequestBody Map<String, String> body) {
        String cepDestino = body.get("cep");

        String url = "https://sandbox.melhorenvio.com.br/api/v2/me/shipment/calculate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + melhorEnvioToken);
        headers.set("User-Agent", "VictorEcommerceApp (victor@email.com)");

        Map<String, Object> payload = new HashMap<>();
        payload.put("from", Map.of("postal_code", "01310200"));
        payload.put("to", Map.of("postal_code", cepDestino));
        payload.put("package", Map.of(
                "height", 42,
                "width", 60,
                "length", 10,
                "weight", 5
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao calcular frete: " + e.getMessage());
        }
    }
}
