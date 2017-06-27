package com.osterhoutgroup.creditcard.creditcard;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.osterhoutgroup.sharedlibraryplugin.models.creditcard.CreditCard;

public interface CreditCardRepo extends JpaRepository<CreditCard, Integer> {
    public CreditCard findByFingerprintAndUserEmail(String fingerprint,
        String userEmail);

    public CreditCard findByCardToken(String cardToken);

    public List<CreditCard> findByUserIdAndIsPrimary(Long userId,
        boolean primary);

    @Query(value = "SELECT * FROM credit_card WHERE (expires_on is NULL || expires_on >= CURRENT_DATE) AND user_id = :userId", nativeQuery = true)
    public List<CreditCard> findValidCreditCardsByUserId(
        @Param("userId") Long userId);

    @Query(value = "SELECT * FROM credit_card WHERE (expires_on is NULL || expires_on >= CURRENT_DATE) AND is_primary = 1 AND user_id = :userId", nativeQuery = true)
    public CreditCard findValidPrimaryCreditCardsByUserId(
        @Param("userId") Long userId);
}