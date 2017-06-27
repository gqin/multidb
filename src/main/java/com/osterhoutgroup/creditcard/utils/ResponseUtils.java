package com.osterhoutgroup.creditcard.utils;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtils {

    public static Map<String, Object> errorResponse(String error) {
        Map<String, Object> response = new HashMap<>();
        response.put("result", "0");
        response.put("errorMessage", error);
        return response;
    }
}
