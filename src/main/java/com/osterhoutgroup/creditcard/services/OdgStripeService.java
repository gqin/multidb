package com.osterhoutgroup.creditcard.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.osterhoutgroup.sharedlibraryplugin.http.GatewayRequest;
import com.osterhoutgroup.sharedlibraryplugin.http.GatewayResponse;

@Service
public class OdgStripeService {

    @Value("${com.odg.stripeservice.url}")
    private String odgStripeServiceUrl;

    public GatewayResponse createToken(String cardNumber, String cardExpirationMonth,
        String cardExpirationYear, String cardSecurityCode, String cardHolder) {

        String odgStripeTokenServiceUrl = odgStripeServiceUrl + "/token";
        RestTemplate rt = new RestTemplate();

        GatewayRequest request = new GatewayRequest();
        request.cardNumber = cardNumber;
        request.cardExpMonth = cardExpirationMonth;
        request.cardExpYear = cardExpirationYear;
        request.securityCode = cardSecurityCode;
        request.name = cardHolder;

        HttpEntity<GatewayRequest> entity = new HttpEntity<>(request, headerConfig());
        return rt.postForObject(odgStripeTokenServiceUrl, entity, GatewayResponse.class);

    }

    public GatewayResponse storeCreditCard(String customerId, boolean isPrimary, String tokenId) {

        String odgStripeStoreCreditCardServiceUrl = odgStripeServiceUrl + "/storeCreditCard";
        RestTemplate rt = new RestTemplate();

        GatewayRequest request = new GatewayRequest();
        request.customerId = customerId;
        request.primary = isPrimary;
        request.id = tokenId;

        HttpEntity<GatewayRequest> entity = new HttpEntity<>(request, headerConfig());
        return rt.postForObject(odgStripeStoreCreditCardServiceUrl, entity, GatewayResponse.class);
    }

    public GatewayResponse createCustomer(String email) {

        String odgStripeCustomerServiceUrl = odgStripeServiceUrl + "/customer";
        RestTemplate rt = new RestTemplate();

        GatewayRequest request = new GatewayRequest();
        request.emailAddress = email;

        HttpEntity<GatewayRequest> entity = new HttpEntity<>(request, headerConfig());

        return rt.postForObject(odgStripeCustomerServiceUrl, entity, GatewayResponse.class);
    }

    private HttpHeaders headerConfig() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
