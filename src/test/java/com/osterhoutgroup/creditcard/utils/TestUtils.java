package com.osterhoutgroup.creditcard.utils;

import com.osterhoutgroup.sharedlibraryplugin.http.GatewayResponse;

public class TestUtils {
	
	public static GatewayResponse getGatewayResponse() {
		GatewayResponse response = new GatewayResponse();
        response.apiKey = "api_key";
        response.cardFingerprint = "" + System.currentTimeMillis();
        response.cardLast4 = "0000";
        response.cardType = "VISA";
		response.code = 1;
        response.currentPeriodEnd = 1L;
        response.currentPeriodStart = 2L;
        response.gatewayCode = 0;
		response.id = "" + System.currentTimeMillis();
		response.interval = "month";
        response.isValid = true;
		return response;
	}

}
