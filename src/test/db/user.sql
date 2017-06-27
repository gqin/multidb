
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
);

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
INSERT INTO `role` VALUES (1,'admin'),(2,'manager'),(3,'finance'),(4,'user'),(5,'developer');
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `last_name` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `profile_picture_url` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `last_login` datetime DEFAULT NULL,
  `customer_id` varchar(255) DEFAULT NULL COMMENT 'User gets customer id, after he register for the first time his credit card.',
  `custom_fee` double DEFAULT '0',
  `fk_role_id` int(11) NOT NULL DEFAULT '4',
  `accepted_terms` tinyint(1) NOT NULL,
  `hash` varchar(255) NOT NULL,
  `hash_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `active` tinyint(1) NOT NULL DEFAULT '0' COMMENT '"0" - User not verified email, "1" - User verified email',
  `is_on_mailing_list` tinyint(1) NOT NULL DEFAULT '0',
  `is_disabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '"0" - User disabled by admin, "1" - User enabled by admin',
  `created_on` datetime NOT NULL,
  `updated_on` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_role_id` (`fk_role_id`),
  CONSTRAINT `fk_user_role_id` FOREIGN KEY (`fk_role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
INSERT INTO `user` VALUES (1,'Qin','Gary','gary.qin@osterhoutgroup.com',NULL,'$2a$06$xJ1og5RwMQV3BeO1jibsHOgz0WBEUFxz2.AiLRvKeogMuWGuCHkQa','2017-03-13 20:44:22',NULL,0,4,1,'a8849b052492b5106526b2331e526138','2017-01-30 18:48:52',1,0,1,'2017-01-30 13:48:52','2017-03-13 20:44:32');
UNLOCK TABLES;
