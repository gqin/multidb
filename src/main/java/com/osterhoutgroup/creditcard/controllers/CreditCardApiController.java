package com.osterhoutgroup.creditcard.controllers;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.osterhoutgroup.creditcard.services.CreditCardService;
import com.osterhoutgroup.sharedlibraryplugin.http.CreditCardRequest;
import com.osterhoutgroup.sharedlibraryplugin.http.CreditCardResponse;
import com.osterhoutgroup.sharedlibraryplugin.models.creditcard.CreditCard;

@RestController
@RequestMapping("/api/v1")
public class CreditCardApiController {
    private static final Logger LOG = Logger.getLogger(CreditCardApiController.class);

    @Autowired
    CreditCardService creditCardService;

    /**
     * API /addCard Add Credit Card. Call payment gateway API and save credit
     * card for this user.
     *
     * @param data @see com.osterhoutgroup.sharedlibraryplugin.http.CreditCardRequest
     * 
     * @return ResponseEntity<?> Json
     * 
     */
    @RequestMapping(value = "/card", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addCard(@RequestBody CreditCardRequest data) {
        CreditCardResponse response = new CreditCardResponse();
        try {
            CreditCard creditCard = creditCardService.createCreditCard(data.cardNumber,
                data.cardExpMonth, data.cardExpYear, data.name, data.securityCode,
                data.emailAddress, data.primary);
            return defaultResponse(creditCard, response);
        } catch (HttpClientErrorException e) {
            LOG.error("addCard: " + e.getResponseHeaders().get(HttpHeaders.WWW_AUTHENTICATE));
            return errorResponse(e.getStatusCode(),
                e.getResponseHeaders().get(HttpHeaders.WWW_AUTHENTICATE).toString(), response);
        } catch (Exception e) {
            LOG.error("addCard: " + e.getMessage());
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), response);
        }
    }

    /**
     * API api/v3/getSavedCards Get List of credit cards added for user.
     * 
     * @param data @see com.osterhoutgroup.sharedlibraryplugin.http.CreditCardRequest
     * 
     * @return ResponseEntity<?> Json
     * 
     */
    @RequestMapping(value = "/card/valid/{email:.+}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getAllValidCards(@PathVariable String email) {
        CreditCardResponse response = new CreditCardResponse();
        try {
            List<CreditCard> cards = creditCardService.getAllValidCards(email);
            return defaultResponse(cards, response);
        } catch (Exception e) {
            LOG.error("getAllValidCards: " + e.getMessage());
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), response);
        }
    }

    /**
     * API api/v3/getPrimaryCard Retrieve users primary credit card, based on
     * users email address.
     *
     * @param data @see com.osterhoutgroup.sharedlibraryplugin.http.CreditCardRequest
     * 
     * @return ResponseEntity<?> Json
     *
     */
    @RequestMapping(value = "/card/valid_primary/{email:.+}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getPrimaryCard(@PathVariable String email) {
        CreditCardResponse response = new CreditCardResponse();
        try {
            CreditCard primaryCard = creditCardService.getValidPrimaryCard(email);
            return defaultResponse(primaryCard, response);
        } catch (Exception e) {
            LOG.error("getValidPrimaryCard: " + e.getMessage());
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), response);
        }
    }



    /**
     * API api/v3/removeCard
     * 
     * @param token Card token.
     * 
     * @return ResponseEntity<?> Json
     */
    @RequestMapping(value = "/card/{token}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> removeCard(@PathVariable String token) {
        CreditCardResponse response = new CreditCardResponse();
        try {
            CreditCard card = creditCardService.removeCard(token);
            return defaultResponse(card, response);
        } catch (Exception e) {
            LOG.error("removeCard: " + e.getMessage());
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), response);
        }
    }

    /**
     * API api/v3/setCardAsPrimary
     * 
     * @param token Card token.
     * 
     * @return ResponseEntity<?> Json
     * 
     */
    @RequestMapping(value = "/card/primary/{token}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> setCardAsPrimary(@PathVariable String token) {
        CreditCardResponse response = new CreditCardResponse();
        try {
            CreditCard primaryCard = creditCardService.setCardAsPrimary(token);
            return defaultResponse(primaryCard, response);
        } catch (Exception e) {
            LOG.error("setCardAsPrimary: " + e.getMessage());
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), response);
        }
    }

    /**
     * API /setCardStatus Called on user.osterhoutgroup.com
     *
     * @param token Card token.
     * @param data @see com.osterhoutgroup.sharedlibraryplugin.http.CreditCardRequest
     * 
     */
    @RequestMapping(value = "/card/{token}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> setCardActivity(@PathVariable String token,
        @RequestBody CreditCardRequest data) {
        CreditCardResponse response = new CreditCardResponse();
        try {
            CreditCard card = creditCardService.setCardActivity(token, data.active);
            return defaultResponse(card, response);
        } catch (Exception e) {
            LOG.error("setCardAsPrimary: " + e.getMessage());
            return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), response);
        }
    }

    private ResponseEntity<?> errorResponse(HttpStatus httpStatus, String errorMessage,
        CreditCardResponse response) {
        response.code = httpStatus.value();
        response.errorMessage = errorMessage;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private ResponseEntity<?> defaultResponse(Object creditCards, CreditCardResponse response) {
        response.code = HttpStatus.OK.value();
        response.creditCards = creditCards;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
