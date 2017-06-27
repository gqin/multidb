--
-- Table structure for table `credit_card`
--

DROP TABLE IF EXISTS `credit_card`;
CREATE TABLE `credit_card` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `card_holder` varchar(255) NOT NULL,
  `card_token` varchar(255) NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `is_primary` tinyint(1) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  `last_4_digits` varchar(255) NOT NULL,
  `user_email` varchar(255) NOT NULL,
  `fingerprint` varchar(255) NOT NULL,
  `created_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_on` datetime NOT NULL,
  `expires_on` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ;
