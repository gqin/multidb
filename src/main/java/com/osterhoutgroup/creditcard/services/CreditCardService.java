package com.osterhoutgroup.creditcard.services;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.osterhoutgroup.creditcard.constants.Messages;
import com.osterhoutgroup.creditcard.creditcard.CreditCardRepo;
import com.osterhoutgroup.creditcard.user.UserRepo;
import com.osterhoutgroup.sharedlibraryplugin.http.GatewayResponse;
import com.osterhoutgroup.sharedlibraryplugin.models.creditcard.CreditCard;
import com.osterhoutgroup.sharedlibraryplugin.models.user.User;

@Service
public class CreditCardService {

    private CreditCardRepo creditCardRepo;
    private UserRepo userRepo;
    private OdgStripeService odgStripeService;

    @Autowired
    public CreditCardService(CreditCardRepo creditCardRepo, UserRepo userRepo,
        OdgStripeService odgStripeService) {
        this.creditCardRepo = creditCardRepo;
        this.userRepo = userRepo;
        this.odgStripeService = odgStripeService;
    }

    /**
     * Create credit card and add this credit card to customer. Save credit
     * cards for customer on Stripe.
     * 
     * @param cardNumber credit card number
     * @param cardExpirationMonth credit card expiration month
     * @param cardExpirationYear credit card expiration year
     * @param cardHolder card holder full name
     * @param cardSecurityCode card security code
     * @param email card holder email
     * @param isPrimary is the card primary
     * 
     * @return CreditCard
     * 
     * @throws Excepton
     */
    public CreditCard createCreditCard(String cardNumber,
        String cardExpirationMonth, String cardExpirationYear,
        String cardHolder, String cardSecurityCode,
        String email, boolean isPrimary) throws Exception {

        // Check if customer is already registered on Stripe. If not, register
        // customer and retrieve customer_id. If already proceed with adding
        // card.
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new Exception(Messages.UNABLE_TO_RESOLVE_USER_FOR_EMAIL);
        }

        if (user.getCustomerId() == null) {
            GatewayResponse customerGatewayResponse = odgStripeService.createCustomer(email);
            if (StringUtils.isEmpty(customerGatewayResponse.id)) {
                throw new Exception(Messages.ERROR_WHILE_CREATING_CUSTOMER);
            } else {
                user.setCustomerId(customerGatewayResponse.id);
                userRepo.save(user);
            }
        }

        // create token
        GatewayResponse tokenGatewayResponse = odgStripeService.createToken(cardNumber,
            cardExpirationMonth, cardExpirationYear, cardSecurityCode, cardHolder);
        if (StringUtils.isEmpty(tokenGatewayResponse.id)
            || StringUtils.isEmpty(tokenGatewayResponse.cardFingerprint)) {
            throw new Exception(Messages.UNABLE_TO_CREATE_CREDIT_CARD);
        }

        // Check if card is already added for this user based on credit card
        // fingerprint

        if (isCardExistForFingerprint(tokenGatewayResponse.cardFingerprint, email)) {
            throw new Exception(Messages.CREDIT_CARD_ALREADY_REGISTERED);
        }

        GatewayResponse creditCardGatewayResponse = odgStripeService
            .storeCreditCard(user.getCustomerId(), isPrimary, tokenGatewayResponse.id);

        if (StringUtils.isEmpty(creditCardGatewayResponse.id)) {
            throw new Exception(Messages.CREDIT_CARD_SERVICE_DATA_ERROR);
        }

        CreditCard creditCard = new CreditCard();
        creditCard.setUserId(user.getId());
        creditCard.setCardHolder(cardHolder);
        creditCard.setUserEmail(email);
        creditCard.setCardToken(creditCardGatewayResponse.id);
        creditCard.setType(creditCardGatewayResponse.cardType);
        creditCard.setLast4Digits(creditCardGatewayResponse.cardLast4);
        creditCard.setFingerprint(creditCardGatewayResponse.cardFingerprint);
        creditCard.setPrimary(isPrimary);

        Calendar calendar = Calendar.getInstance();
        calendar.set(
            Integer.parseInt(cardExpirationYear), Integer.parseInt(cardExpirationMonth), 1);
        creditCard.setExpiresOn(calendar.getTime());

        if (isPrimary) {
            creditCard.setActive(true);
            // if it is primary card, make others secondary
            changePrimaryCardsToSecondary(user.getId());
        }

        CreditCard savedCreditCard = creditCardRepo.save(creditCard);
        if (savedCreditCard == null) {
            throw new Exception(Messages.CHANGES_CANNOT_BE_PERSISTED);
        }

        return savedCreditCard;
    }

    /**
     * Get all valid credit cards
     * 
     * @param email card holder email
     * 
     * @return List<CreditCard> list of valid credit cards
     * 
     * @throws Excepton
     */
    public List<CreditCard> getAllValidCards(String email) throws Exception {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new Exception("User is not existed for " + email);
        }

        return creditCardRepo.findValidCreditCardsByUserId(user.getId());
    }

    /**
     * Get valid primary card
     * 
     * @param email card holder email
     * 
     * @return CreditCard a valid primary credit card
     * 
     * @throws Excepton
     */
    public CreditCard getValidPrimaryCard(String email) throws Exception {
        User user = userRepo.findByEmail(email);
        if (user == null) {
            throw new Exception("User is not existed for " + email);
        }

        // Find users primary card, which is not expired.
        return creditCardRepo.findValidPrimaryCreditCardsByUserId(user.getId());
    }

    /**
     * Remove a credit card by card token
     * 
     * @param cardToken card token
     * 
     * @return CreditCard a removed credit card
     * 
     * @throws Excepton
     */
    public CreditCard removeCard(String cardToken) throws Exception {
        CreditCard card = creditCardRepo.findByCardToken(cardToken);
        if (card == null) {
            throw new Exception("Credit card is not existed for " + cardToken);
        }
        creditCardRepo.delete(card);
        return card;
    }

    /**
     * Set a credit card as primary
     * 
     * @param cardToken card token
     * 
     * @return CreditCard the updated credit card
     * 
     * @throws Excepton
     */
    public CreditCard setCardAsPrimary(String cardToken) throws Exception {
        CreditCard card = creditCardRepo.findByCardToken(cardToken);
        if (card == null) {
            throw new Exception("Credit card is not existed for " + cardToken);
        }

        changePrimaryCardsToSecondary(card.getUserId());

        // set this card to primary
        card.setPrimary(true);
        card.setActive(true);
        creditCardRepo.save(card);
        return card;
    }

    /**
     * Enable or disable a credit card
     *
     * @param cardToken card token
     * @param active card activity
     * 
     * @return CreditCard the updated credit card
     * 
     * @throws Exception
     */
    public CreditCard setCardActivity(String cardToken, boolean active)
        throws Exception {
        CreditCard card = creditCardRepo.findByCardToken(cardToken);
        if (card == null) {
            throw new Exception("Credit card is not existed for " + cardToken);
        }
        card.setActive(active);
        creditCardRepo.save(card);
        return card;
    }

    /**
     * check if a card exists or not in records
     *
     * @param fingerprint finger print
     * @param email user email
     * 
     * @return boolean true or false
     * 
     */
    private boolean isCardExistForFingerprint(String fingerprint,
        String email) {
        return creditCardRepo.findByFingerprintAndUserEmail(fingerprint,
            email) != null;
    }

    /**
     * Change primary cards to secondary
     *
     * @param userId user id
     * 
     */
    private void changePrimaryCardsToSecondary(Long userId) {
        // find current primary card
        List<CreditCard> primaryCards =
            creditCardRepo.findByUserIdAndIsPrimary(userId, true);

        // change other credit cards primary status
        for (CreditCard primaryCard : primaryCards) {
            primaryCard.setPrimary(false);
            creditCardRepo.save(primaryCard);
        }
    }
}
