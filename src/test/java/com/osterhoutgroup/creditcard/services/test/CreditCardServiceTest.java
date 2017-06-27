package com.osterhoutgroup.creditcard.services.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.osterhoutgroup.creditcard.constants.Messages;
import com.osterhoutgroup.creditcard.creditcard.CreditCardRepo;
import com.osterhoutgroup.creditcard.services.CreditCardService;
import com.osterhoutgroup.creditcard.services.OdgStripeService;
import com.osterhoutgroup.creditcard.user.UserRepo;
import com.osterhoutgroup.creditcard.utils.TestUtils;
import com.osterhoutgroup.sharedlibraryplugin.http.GatewayResponse;
import com.osterhoutgroup.sharedlibraryplugin.models.creditcard.CreditCard;
import com.osterhoutgroup.sharedlibraryplugin.models.user.User;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CreditCardServiceTest {

	@Autowired
	private CreditCardRepo creditCardRepo;

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private CreditCardRepo cardRepo;
    
	private CreditCardService service;
	
	private OdgStripeService stripeMock;
	
	private List<CreditCard> cardList;
	
	private User user;
	
	private GatewayResponse test;
	
	private GatewayResponse storeTest;
	
	private boolean isPrimary;
	
	private int count = 1111;
	
	private String cardNumber, cardExpirationMonth, cardExpirationYear, cardHolder,
		cardSecurityCode, email, oauthToken, cardToken, userErrMsg, cardErrMsg, customerId;
	
	@Before
	public void setUp() {		
		stripeMock = mock(OdgStripeService.class);
		service = new CreditCardService(creditCardRepo,userRepo,stripeMock);
		test = new GatewayResponse();
		storeTest = new GatewayResponse();
		cardList = new ArrayList<CreditCard>();
		
		// Mock Jane's credit card
		cardNumber = "491646208874" + count;
		cardExpirationMonth = "08";
		cardExpirationYear = "2017";
		cardHolder = "VISA";
		cardSecurityCode = "956";
		email = "fake.jane@osterhoutgroup.com";
		oauthToken = "token=xxxxx";
		cardToken = "123";
		customerId = "8888";
		isPrimary = false;
		
		// Mock error messages
		userErrMsg = "User is not existed for ";
		cardErrMsg = "Credit card is not existed for ";
		
		// If this unit test is ran way too many times for some reason, reset count
		if (count == 9999) {
			count = 1111;
		}
	}
	
	@Test
	public void Given_UserWantsToCreateCreditCard_When_CreateCreditCard_Then_ReturnCreditCard() {		
		// Mock Gateway responses
		test = TestUtils.getGatewayResponse();
		storeTest = TestUtils.getGatewayResponse();
		user = userRepo.findByEmail(email);
		try {
			// Create credit card for Jane using mocked token and credit card
            when(stripeMock.createToken(cardNumber, cardExpirationMonth, cardExpirationYear,
                cardSecurityCode, cardHolder)).thenReturn(test);
            when(stripeMock.storeCreditCard(anyString(), anyBoolean(), anyString()))
                .thenReturn(storeTest);
            service.createCreditCard(cardNumber, cardExpirationMonth, cardExpirationYear,
                cardHolder, cardSecurityCode, email, isPrimary);
			cardList.addAll(cardRepo.findByUserIdAndIsPrimary(user.getId(), isPrimary));
			assertNotNull(cardList);
		} catch (Exception e) {
			fail();
		}
		
		// Increment count so that the last 4 digits of Jane's test cards are different.
		count++;
	}
	
	@Test
	public void Given_UserEmailCannotBeFound_When_CreateCreditCard_Then_ThrowException() {
		email = "fake.email@osterhoutgroup.com";
		try {
            service.createCreditCard(cardNumber, cardExpirationMonth, cardExpirationYear,
                cardHolder, cardSecurityCode, email, isPrimary);
			fail();
		} catch (Exception e) {
			assertEquals(Messages.UNABLE_TO_RESOLVE_USER_FOR_EMAIL, e.getMessage());
		}
	}
	
	@Test
	public void Given_GatewayResponseIdIsMissing_When_CreateCreditCard_Then_ThrowException() {
		test = TestUtils.getGatewayResponse();
		test.id = "";
		
		// Joe is preset to not have a customer Id
		email = "fake.joe@osterhoutgroup.com";
		
		try {
            when(stripeMock.createCustomer(email)).thenReturn(test);
            service.createCreditCard(cardNumber, cardExpirationMonth, cardExpirationYear,
                cardHolder, cardSecurityCode, email, isPrimary);
			fail();
		} catch (Exception e) {
			assertEquals(Messages.ERROR_WHILE_CREATING_CUSTOMER, e.getMessage());
		}
	}
	
	@Test
	public void Given_CreateTokenWithEmptyResponseId_When_CreateCreditCard_Then_ThrowException() {
		test = TestUtils.getGatewayResponse();
		test.id = "";
		try {
            when(stripeMock.createToken(cardNumber, cardExpirationMonth, cardExpirationYear,
                cardSecurityCode, cardHolder)).thenReturn(test);
            service.createCreditCard(cardNumber, cardExpirationMonth, cardExpirationYear,
                cardHolder, cardSecurityCode, email, isPrimary);
			fail();
		} catch (Exception e) {
			assertEquals(Messages.UNABLE_TO_CREATE_CREDIT_CARD, e.getMessage());
		}
	}
	
	@Test
	public void Given_CreateTokenWithEmptyFingerprint_When_CreateCreditCard_Then_ThrowException() {
		test = TestUtils.getGatewayResponse();
        test.cardFingerprint = "";
		try {
            when(stripeMock.createToken(cardNumber, cardExpirationMonth, cardExpirationYear,
                cardSecurityCode, cardHolder)).thenReturn(test);
            service.createCreditCard(cardNumber, cardExpirationMonth, cardExpirationYear,
                cardHolder, cardSecurityCode, email, isPrimary);
			fail();
		} catch (Exception e) {
			assertEquals(Messages.UNABLE_TO_CREATE_CREDIT_CARD, e.getMessage());
		}
	}
	
	@Test
	public void Given_CardAlreadyExistsForFingerprint_When_CreateCreditCard_Then_ThrowException() {
		test = TestUtils.getGatewayResponse();
        test.cardFingerprint = "fingerprint";
		try {
            when(stripeMock.createToken(cardNumber, cardExpirationMonth, cardExpirationYear,
                cardSecurityCode, cardHolder)).thenReturn(test);
            service.createCreditCard(cardNumber, cardExpirationMonth, cardExpirationYear,
                cardHolder, cardSecurityCode, email, isPrimary);
			fail();
		} catch (Exception e) {
			assertEquals(Messages.CREDIT_CARD_ALREADY_REGISTERED, e.getMessage());
		}
	}
	
	@Test
	public void Given_CreditCardIdIsEmpty_When_CreateCreditCard_Then_ThrowException() {
		test = TestUtils.getGatewayResponse();
		storeTest = TestUtils.getGatewayResponse();
		storeTest.id = "";
		try {
            when(stripeMock.createToken(cardNumber, cardExpirationMonth, cardExpirationYear,
                cardSecurityCode, cardHolder)).thenReturn(test);
            when(stripeMock.storeCreditCard(anyString(), anyBoolean(), anyString()))
                .thenReturn(storeTest);
            service.createCreditCard(cardNumber, cardExpirationMonth, cardExpirationYear,
                cardHolder, cardSecurityCode, email, isPrimary);
			fail();
		} catch (Exception e) {
			assertEquals(Messages.CREDIT_CARD_SERVICE_DATA_ERROR, e.getMessage());
		}
		
		count++;
	}
	
	@Test
	public void Given_EmailDoesNotExist_When_GetAllValidCards_Then_ThrowException() {
		email = "fake.email@osterhoutgroup.com";
		try {
			service.getAllValidCards(email);
			fail();
		} catch (Exception e) {
			assertEquals(userErrMsg + email, e.getMessage());
		}
	}
	
	@Test
	public void Given_UserWantsAllCards_When_GetAllValidCards_Then_ReturnAllCardsWithSameId() {
		try {
			cardList.addAll(service.getAllValidCards(email));
			assertNotNull(cardList);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void Given_EmailDoesNotExist_When_GetValidPrimaryCard_Then_ThrowException() {
		email = "fake.email@osterhoutgroup.com";
		try {
			service.getValidPrimaryCard(email);
			fail();
		} catch (Exception e) {
			assertEquals(userErrMsg + email, e.getMessage());
		}
	}
	
	@Test
	public void Given_UserWantPrimaryCard_When_GetValidPrimaryCard_Then_ReturnPrimaryCard() {
		try {
			cardList.add(service.getValidPrimaryCard(email));
			assertNotNull(cardList);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void Given_TokenDoesNotExist_When_RemoveCard_Then_ThrowException() {
		cardToken = "FAKE";
		try {
			service.removeCard(cardToken);
			fail();
		} catch (Exception e) {
			assertEquals(cardErrMsg + cardToken, e.getMessage());
		}
	}
	
//	@Test
//	public void Given_UserWantsToRemoveCard_When_RemoveCard_Then_RemoveCard() {
//		cardToken = "1495753019900";
//		try {
//			service.removeCard(cardToken);
//		} catch (Exception e) {
//			fail();
//		}
//	}
	
	@Test
	public void Given_TokenDoesNotExist_When_SetCardAsPrimary_Then_ThrowException() {
		cardToken = "FAKE";
		try {
			service.setCardAsPrimary(cardToken);
			fail();
		} catch (Exception e) {
			assertEquals(cardErrMsg + cardToken, e.getMessage());
		}
	}
	
	@Test
	public void Given_UserWantsToMakeCardPrimary_When_SetCardAsPrimary_Then_CardBecomesPrimary() {
		try {
			cardList.add(service.setCardAsPrimary(cardToken));
			assertTrue(cardList.get(0).isPrimary());
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void Given_TokenDoesNotExist_When_SetCardActivity_Then_ThrowException() {
		cardToken = "FAKE";
		try {
			service.setCardActivity(cardToken, false);
			fail();
		} catch (Exception e) {
			assertEquals(cardErrMsg + cardToken, e.getMessage());
		}
	}
	
	@Test
	public void Given_UserWantsToEnableCreditCard_When_SetCardActivity_Then_CreditCardIsActive() {
		boolean active = true;
		try {
			cardList.add(service.setCardActivity(cardToken, active));
			assertTrue(cardList.get(0).getActive());
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void Given_UserWantsToDisableCreditCard_When_SetCardActivity_Then_CreditCardIsNotActive() {
		boolean active = false;
		try {
			cardList.add(service.setCardActivity(cardToken, active));
			assertFalse(cardList.get(0).getActive());
		} catch (Exception e) {
			fail();
		}
	}
}
