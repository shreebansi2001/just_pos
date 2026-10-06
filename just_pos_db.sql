-- ======================================================
-- JUST POS & JC Portal - Complete Database Schema & Seed Data
-- Database Name: just_pos_db / jc_portal
-- Compatible with: MySQL 5.7+ / MySQL 8.0+
-- ======================================================

SET FOREIGN_KEY_CHECKS=0;
SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';
SET NAMES utf8mb4;

-- Host: 103.235.105.129:3306
-- Exported on: 2026-09-29 12:16:37
-- ------------------------------------------------------

SET FOREIGN_KEY_CHECKS=0;
SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';
SET NAMES utf8mb4;

--
-- Table structure for table `cities`
--

DROP TABLE IF EXISTS `cities`;
CREATE TABLE `cities` (
  `city_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `is_delete` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `state_id` bigint(20) NOT NULL,
  PRIMARY KEY (`city_id`),
  KEY `FKsu54e1tlhaof4oklvv7uphsli` (`state_id`),
  CONSTRAINT `FKsu54e1tlhaof4oklvv7uphsli` FOREIGN KEY (`state_id`) REFERENCES `states` (`state_id`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `cities`
--

LOCK TABLES `cities` WRITE;
INSERT INTO `cities` (`city_id`, `created_at`, `is_delete`, `name`, `updated_at`, `state_id`) VALUES
(1, '2025-08-21 17:23:51', 0x00, 'Patan', '2025-08-21 17:26:15', 1),
(2, '2025-09-09 10:55:48', 0x00, 'Ahmedabad', '2025-09-09 10:55:48', 1),
(3, '2025-09-09 10:55:48', 0x00, 'Surat', '2025-09-09 10:55:48', 1),
(4, '2025-09-09 10:55:48', 0x00, 'Vadodara', '2025-09-09 10:55:48', 1),
(5, '2025-09-09 10:55:48', 0x00, 'Rajkot', '2025-09-09 10:55:48', 1),
(6, '2025-09-09 10:55:48', 0x00, 'Bhavnagar', '2025-09-09 10:55:48', 1),
(7, '2025-09-09 10:55:48', 0x00, 'Visakhapatnam', '2025-09-09 10:55:48', 2),
(8, '2025-09-09 10:55:48', 0x00, 'Vijayawada', '2025-09-09 10:55:48', 2),
(9, '2025-09-09 10:55:48', 0x00, 'Guntur', '2025-09-09 10:55:48', 2),
(10, '2025-09-09 10:55:48', 0x00, 'Nellore', '2025-09-09 10:55:48', 2),
(11, '2025-09-09 10:55:48', 0x00, 'Tirupati', '2025-09-09 10:55:48', 2),
(12, '2025-09-09 10:55:48', 0x00, 'Itanagar', '2025-09-09 10:55:48', 3),
(13, '2025-09-09 10:55:48', 0x00, 'Naharlagun', '2025-09-09 10:55:48', 3),
(14, '2025-09-09 10:55:48', 0x00, 'Pasighat', '2025-09-09 10:55:48', 3),
(15, '2025-09-09 10:55:48', 0x00, 'Guwahati', '2025-09-09 10:55:48', 4),
(16, '2025-09-09 10:55:48', 0x00, 'Silchar', '2025-09-09 10:55:48', 4),
(17, '2025-09-09 10:55:48', 0x00, 'Dibrugarh', '2025-09-09 10:55:48', 4),
(18, '2025-09-09 10:55:48', 0x00, 'Jorhat', '2025-09-09 10:55:48', 4),
(19, '2025-09-09 10:55:48', 0x00, 'Patna', '2025-09-09 10:55:48', 5),
(20, '2025-09-09 10:55:48', 0x00, 'Gaya', '2025-09-09 10:55:48', 5),
(21, '2025-09-09 10:55:48', 0x00, 'Muzaffarpur', '2025-09-09 10:55:48', 5),
(22, '2025-09-09 10:55:48', 0x00, 'Bhagalpur', '2025-09-09 10:55:48', 5),
(23, '2025-09-09 10:55:48', 0x00, 'Raipur', '2025-09-09 10:55:48', 6),
(24, '2025-09-09 10:55:48', 0x00, 'Bhilai', '2025-09-09 10:55:48', 6),
(25, '2025-09-09 10:55:48', 0x00, 'Bilaspur', '2025-09-09 10:55:48', 6),
(26, '2025-09-09 10:55:48', 0x00, 'Panaji', '2025-09-09 10:55:48', 7),
(27, '2025-09-09 10:55:48', 0x00, 'Margao', '2025-09-09 10:55:48', 7),
(28, '2025-09-09 10:55:48', 0x00, 'Vasco da Gama', '2025-09-09 10:55:48', 7),
(29, '2025-09-09 10:55:48', 0x00, 'Shimla', '2025-09-09 10:55:48', 9),
(30, '2025-09-09 10:55:48', 0x00, 'Manali', '2025-09-09 10:55:48', 9),
(31, '2025-09-09 10:55:48', 0x00, 'Dharamshala', '2025-09-09 10:55:48', 9),
(32, '2025-09-09 10:55:48', 0x00, 'Ranchi', '2025-09-09 10:55:48', 10),
(33, '2025-09-09 10:55:48', 0x00, 'Jamshedpur', '2025-09-09 10:55:48', 10),
(34, '2025-09-09 10:55:48', 0x00, 'Dhanbad', '2025-09-09 10:55:48', 10),
(35, '2025-09-09 10:55:48', 0x00, 'Bengaluru', '2025-09-09 10:55:48', 12),
(36, '2025-09-09 10:55:48', 0x00, 'Mysuru', '2025-09-09 10:55:48', 12),
(37, '2025-09-09 10:55:48', 0x00, 'Mangaluru', '2025-09-09 10:55:48', 12),
(38, '2025-09-09 10:55:48', 0x00, 'Hubballi', '2025-09-09 10:55:48', 12),
(39, '2025-09-09 10:55:48', 0x00, 'Thiruvananthapuram', '2025-09-09 10:55:48', 13),
(40, '2025-09-09 10:55:48', 0x00, 'Kochi', '2025-09-09 10:55:48', 13),
(41, '2025-09-09 10:55:48', 0x00, 'Kozhikode', '2025-09-09 10:55:48', 13),
(42, '2025-09-09 10:55:48', 0x00, 'Bhopal', '2025-09-09 10:55:48', 14),
(43, '2025-09-09 10:55:48', 0x00, 'Indore', '2025-09-09 10:55:48', 14),
(44, '2025-09-09 10:55:48', 0x00, 'Gwalior', '2025-09-09 10:55:48', 14),
(45, '2025-09-09 10:55:48', 0x00, 'Jabalpur', '2025-09-09 10:55:48', 14),
(46, '2025-09-09 10:55:48', 0x00, 'Mumbai', '2025-09-09 10:55:48', 15),
(47, '2025-09-09 10:55:48', 0x00, 'Pune', '2025-09-09 10:55:48', 15),
(48, '2025-09-09 10:55:48', 0x00, 'Nagpur', '2025-09-09 10:55:48', 15),
(49, '2025-09-09 10:55:48', 0x00, 'Nashik', '2025-09-09 10:55:48', 15),
(50, '2025-09-09 10:55:48', 0x00, 'Aurangabad', '2025-09-09 10:55:48', 15);
INSERT INTO `cities` (`city_id`, `created_at`, `is_delete`, `name`, `updated_at`, `state_id`) VALUES
(51, '2025-09-09 10:55:48', 0x00, 'Bhubaneswar', '2025-09-09 10:55:48', 20),
(52, '2025-09-09 10:55:48', 0x00, 'Cuttack', '2025-09-09 10:55:48', 20),
(53, '2025-09-09 10:55:48', 0x00, 'Rourkela', '2025-09-09 10:55:48', 20),
(54, '2025-09-09 10:55:48', 0x00, 'Amritsar', '2025-09-09 10:55:48', 21),
(55, '2025-09-09 10:55:48', 0x00, 'Ludhiana', '2025-09-09 10:55:48', 21),
(56, '2025-09-09 10:55:48', 0x00, 'Patiala', '2025-09-09 10:55:48', 21),
(57, '2025-09-09 10:55:48', 0x00, 'Jalandhar', '2025-09-09 10:55:48', 21),
(58, '2025-09-09 10:55:48', 0x00, 'Jaipur', '2025-09-09 10:55:48', 22),
(59, '2025-09-09 10:55:48', 0x00, 'Jodhpur', '2025-09-09 10:55:48', 22),
(60, '2025-09-09 10:55:48', 0x00, 'Udaipur', '2025-09-09 10:55:48', 22),
(61, '2025-09-09 10:55:48', 0x00, 'Kota', '2025-09-09 10:55:48', 22),
(62, '2025-09-09 10:55:48', 0x00, 'Chennai', '2025-09-09 10:55:48', 24),
(63, '2025-09-09 10:55:48', 0x00, 'Coimbatore', '2025-09-09 10:55:48', 24),
(64, '2025-09-09 10:55:48', 0x00, 'Madurai', '2025-09-09 10:55:48', 24),
(65, '2025-09-09 10:55:48', 0x00, 'Tiruchirappalli', '2025-09-09 10:55:48', 24),
(66, '2025-09-09 10:55:48', 0x00, 'Hyderabad', '2025-09-09 10:55:48', 25),
(67, '2025-09-09 10:55:48', 0x00, 'Warangal', '2025-09-09 10:55:48', 25),
(68, '2025-09-09 10:55:48', 0x00, 'Nizamabad', '2025-09-09 10:55:48', 25),
(69, '2025-09-09 10:55:48', 0x00, 'Lucknow', '2025-09-09 10:55:48', 27),
(70, '2025-09-09 10:55:48', 0x00, 'Kanpur', '2025-09-09 10:55:48', 27),
(71, '2025-09-09 10:55:48', 0x00, 'Varanasi', '2025-09-09 10:55:48', 27),
(72, '2025-09-09 10:55:48', 0x00, 'Agra', '2025-09-09 10:55:48', 27),
(73, '2025-09-09 10:55:48', 0x00, 'Prayagraj', '2025-09-09 10:55:48', 27),
(74, '2025-09-09 10:55:48', 0x00, 'Dehradun', '2025-09-09 10:55:48', 28),
(75, '2025-09-09 10:55:48', 0x00, 'Haridwar', '2025-09-09 10:55:48', 28),
(76, '2025-09-09 10:55:48', 0x00, 'Rishikesh', '2025-09-09 10:55:48', 28),
(77, '2025-09-09 10:55:48', 0x00, 'Kolkata', '2025-09-09 10:55:48', 29),
(78, '2025-09-09 10:55:48', 0x00, 'Howrah', '2025-09-09 10:55:48', 29),
(79, '2025-09-09 10:55:48', 0x00, 'Durgapur', '2025-09-09 10:55:48', 29),
(80, '2025-09-09 10:55:48', 0x00, 'Siliguri', '2025-09-09 10:55:48', 29),
(81, '2025-09-09 10:55:48', 0x00, 'Asansol', '2025-09-09 10:55:48', 29);
UNLOCK TABLES;

--
-- Table structure for table `contact_category`
--

DROP TABLE IF EXISTS `contact_category`;
CREATE TABLE `contact_category` (
  `contact_category_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `is_delete` bit(1) NOT NULL,
  `name_english` varchar(255) NOT NULL,
  `name_gujarati` varchar(255) DEFAULT NULL,
  `name_hindi` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`contact_category_id`),
  KEY `FKm2iiuetisjnfyehlu5yute9of` (`user_id`),
  CONSTRAINT `FKm2iiuetisjnfyehlu5yute9of` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `contact_category`
--

LOCK TABLES `contact_category` WRITE;
INSERT INTO `contact_category` (`contact_category_id`, `created_at`, `is_delete`, `name_english`, `name_gujarati`, `name_hindi`, `updated_at`, `user_id`) VALUES
(1, '2025-08-22 18:37:22', 0x01, 'family', 'ફેમીલી', 'फेमिली', NULL, 1),
(2, '2025-08-22 18:39:29', 0x00, 'Business', 'string', 'string', '2025-08-23 11:57:17', 1),
(3, '2025-08-25 17:40:10', 0x01, 'VIVEk', '', '', '2025-08-25 17:41:17', 1),
(4, '2025-08-26 14:03:08', 0x00, 'Family ', '', '', '2025-08-26 14:14:30', 1),
(5, '2025-08-26 14:12:51', 0x01, 'Social', '', '', NULL, 1),
(6, '2025-08-26 15:56:57', 0x00, 'Friend', '', '', NULL, 1),
(7, '2025-08-27 15:37:27', 0x00, 'test', '', '', NULL, 1),
(8, '2025-09-02 17:32:11', 0x00, 'asdtyu', '', '', NULL, 1),
(9, '2025-09-04 13:20:10', 0x00, 'Family', '', '', NULL, 2);
UNLOCK TABLES;

--
-- Table structure for table `countries`
--

DROP TABLE IF EXISTS `countries`;
CREATE TABLE `countries` (
  `country_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(10) NOT NULL,
  `created_at` datetime NOT NULL,
  `is_delete` bit(1) NOT NULL,
  `name` varchar(100) NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`country_id`),
  UNIQUE KEY `UK_5dhgnik9p8t72kaktdb8kd8dt` (`code`),
  UNIQUE KEY `UK_1pyiwrqimi3hnl3vtgsypj5r` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `countries`
--

LOCK TABLES `countries` WRITE;
INSERT INTO `countries` (`country_id`, `code`, `created_at`, `is_delete`, `name`, `updated_at`) VALUES
(1, '91', '2025-08-21 17:09:29', 0x00, 'INDIA', '2025-08-22 18:16:06'),
(2, '1', '2025-08-21 17:13:14', 0x01, 'USA', NULL);
UNLOCK TABLES;

--
-- Table structure for table `event_function`
--

DROP TABLE IF EXISTS `event_function`;
CREATE TABLE `event_function` (
  `event_function_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `function_end_date_time` datetime DEFAULT NULL,
  `function_start_date_time` datetime DEFAULT NULL,
  `function_venue` varchar(255) DEFAULT NULL,
  `is_delete` bit(1) NOT NULL,
  `notes_english` varchar(255) DEFAULT NULL,
  `notes_gujarati` varchar(255) DEFAULT NULL,
  `notes_hindi` varchar(255) DEFAULT NULL,
  `pax` int(11) DEFAULT NULL,
  `rate` double DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `event_id` bigint(20) DEFAULT NULL,
  `function_master_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`event_function_id`),
  KEY `FKo4p54h1p26e11tv4yytv8gh4u` (`event_id`),
  KEY `FKiwnnilmha69ym5127bmsnwdf4` (`function_master_id`),
  CONSTRAINT `FKiwnnilmha69ym5127bmsnwdf4` FOREIGN KEY (`function_master_id`) REFERENCES `functions` (`function_id`),
  CONSTRAINT `FKo4p54h1p26e11tv4yytv8gh4u` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `event_function`
--

LOCK TABLES `event_function` WRITE;
INSERT INTO `event_function` (`event_function_id`, `created_at`, `function_end_date_time`, `function_start_date_time`, `function_venue`, `is_delete`, `notes_english`, `notes_gujarati`, `notes_hindi`, `pax`, `rate`, `updated_at`, `event_id`, `function_master_id`) VALUES
(1, '2025-09-05 18:57:10', '2025-09-16 11:00:00', '2025-09-14 10:09:00', 'Airport', 0x00, '', '', '', 120, NULL, NULL, 1, 4),
(2, '2025-09-05 22:27:00', '2025-09-15 11:00:00', '2025-09-12 10:09:00', 'e2e', 0x00, '', '', '', 22, 22.0, NULL, 2, 4),
(3, '2025-09-05 22:27:00', '2025-09-15 11:00:00', '2025-09-12 03:00:00', 'edewe', 0x00, '', '', '', 2, 22.0, NULL, 2, 12),
(4, '2025-09-06 11:01:06', '2025-09-13 11:00:00', '2025-09-12 10:09:00', 'dfg', 0x00, '', '', '', 22, 2.0, NULL, 3, 4),
(5, '2025-09-06 11:09:30', '2025-09-30 11:00:00', '2025-09-29 10:09:00', 'Airport', 0x00, '', '', '', 1520, 200.0, NULL, 4, 4),
(6, '2025-09-06 11:09:30', '2025-09-30 11:00:00', '2025-09-29 03:00:00', 'Airport', 0x00, '', '', '', 520, 250.0, NULL, 4, 12),
(7, '2025-09-06 13:15:59', '2025-09-25 11:00:00', '2025-09-19 10:09:00', 'hghghg', 0x00, '', '', '', 1210, 150.0, '2025-09-06 21:51:18', 7, 4),
(8, '2025-09-06 16:30:10', '2025-09-27 04:15:00', '2025-09-26 04:00:00', 'gghgh', 0x00, '', '', '', 15, 150.0, NULL, 6, 16),
(9, '2025-09-06 17:00:53', '2025-09-25 11:00:00', '2025-09-19 10:09:00', 'hghghg', 0x00, '', '', '', 1210, 150.0, NULL, 7, 4),
(10, '2025-09-07 14:00:43', '2025-09-06 11:00:00', '2025-09-06 10:09:00', 'asdfghj', 0x00, '', '', '', 500, 200.0, NULL, 5, 4),
(11, '2025-09-07 17:03:58', '2025-09-13 11:00:00', '2025-09-10 10:09:00', 'resort', 0x00, '', '', '', 250, 25.0, NULL, 8, 4),
(12, '2025-09-08 11:07:27', '2025-09-21 11:00:00', '2025-09-20 03:00:00', 'dds', 0x00, '', '', '', 200, 20.0, NULL, 9, 12),
(13, '2025-09-08 11:16:34', '2025-09-13 11:00:00', '2025-09-11 03:00:00', 'sddsa', 0x00, '', '', '', 85, 55.0, NULL, 10, 12);
UNLOCK TABLES;

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
CREATE TABLE `events` (
  `event_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `bride_birth_date` date DEFAULT NULL,
  `bride_insta_link` varchar(255) DEFAULT NULL,
  `bride_mobileno` varchar(255) DEFAULT NULL,
  `bride_name` varchar(255) DEFAULT NULL,
  `bride_community` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `event_end_date_time` datetime DEFAULT NULL,
  `event_no` varchar(255) DEFAULT NULL,
  `event_start_date_time` datetime DEFAULT NULL,
  `groom_birth_date` date DEFAULT NULL,
  `groom_insta_link` varchar(255) DEFAULT NULL,
  `groom_mobileno` varchar(255) DEFAULT NULL,
  `groom_name` varchar(255) DEFAULT NULL,
  `groom_community` varchar(255) DEFAULT NULL,
  `inquiry_date` date DEFAULT NULL,
  `is_delete` bit(1) NOT NULL,
  `is_high_priority` varchar(255) DEFAULT NULL,
  `meal_notes` varchar(255) DEFAULT NULL,
  `mobileno` varchar(255) DEFAULT NULL,
  `prefix` varchar(255) DEFAULT NULL,
  `reference` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `service` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `theme` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `venue` varchar(255) DEFAULT NULL,
  `event_type_id` bigint(20) DEFAULT NULL,
  `manager_id` bigint(20) DEFAULT NULL,
  `meal_type_id` bigint(20) DEFAULT NULL,
  `party_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`event_id`),
  KEY `FKc2oqk2pnerfbs2rdq4c7pinay` (`event_type_id`),
  KEY `FK4lsvuu8y3xvo76gd0q1u30nnj` (`manager_id`),
  KEY `FK4w78t1nedx0vd25fvuwiovo83` (`meal_type_id`),
  KEY `FKs5gpumebi5i024vkg7fesy7o6` (`party_id`),
  KEY `FKat8p3s7yjcp57lny4udqvqncq` (`user_id`),
  CONSTRAINT `FK4lsvuu8y3xvo76gd0q1u30nnj` FOREIGN KEY (`manager_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FK4w78t1nedx0vd25fvuwiovo83` FOREIGN KEY (`meal_type_id`) REFERENCES `mealtype` (`meal_type_id`),
  CONSTRAINT `FKat8p3s7yjcp57lny4udqvqncq` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKc2oqk2pnerfbs2rdq4c7pinay` FOREIGN KEY (`event_type_id`) REFERENCES `eventtype` (`event_type_id`),
  CONSTRAINT `FKs5gpumebi5i024vkg7fesy7o6` FOREIGN KEY (`party_id`) REFERENCES `partymaster` (`party_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
INSERT INTO `events` (`event_id`, `address`, `bride_birth_date`, `bride_insta_link`, `bride_mobileno`, `bride_name`, `bride_community`, `created_at`, `event_end_date_time`, `event_no`, `event_start_date_time`, `groom_birth_date`, `groom_insta_link`, `groom_mobileno`, `groom_name`, `groom_community`, `inquiry_date`, `is_delete`, `is_high_priority`, `meal_notes`, `mobileno`, `prefix`, `reference`, `remark`, `service`, `status`, `theme`, `updated_at`, `venue`, `event_type_id`, `manager_id`, `meal_type_id`, `party_id`, `user_id`) VALUES
(1, 'Ahmedabad', NULL, NULL, NULL, NULL, NULL, '2025-09-05 18:57:10', '2025-09-16 00:00:00', 'I250001', '2025-09-14 00:00:00', NULL, NULL, NULL, NULL, NULL, '2025-09-05', 0x00, 'Yes', '', '9900990099', NULL, NULL, '', '', 0, '', NULL, 'Ahmedabad', 6, 1, 4, 7, 1),
(2, 'patan', NULL, NULL, NULL, NULL, NULL, '2025-09-05 22:27:00', '2025-09-15 00:00:00', 'I250002', '2025-09-12 00:00:00', NULL, NULL, NULL, NULL, NULL, '2025-09-05', 0x00, 'Yes', '', '9900990099', NULL, 'aa', '', '', 1, '', NULL, 'hello', 6, 4, 4, 12, 1),
(3, 'Ahmedabad', NULL, NULL, NULL, NULL, NULL, '2025-09-06 11:01:06', '2025-09-13 00:00:00', 'I250003', '2025-09-12 00:00:00', NULL, NULL, NULL, NULL, NULL, '2025-09-06', 0x00, 'Yes', '', '9900990099', NULL, 'qwe', '', '', 1, '', NULL, 'VEDIK RESORT', 6, 2, 5, 7, 1),
(4, 'Porbandar', NULL, NULL, NULL, NULL, NULL, '2025-09-06 11:09:30', '2025-09-30 00:00:00', 'I250004', '2025-09-29 00:00:00', NULL, NULL, NULL, NULL, NULL, '2025-09-06', 0x00, 'Yes', '', '7895448994', NULL, NULL, '', '', 2, '', NULL, 'asasa', 6, 4, 5, 13, 1),
(5, 'Ahmedabad', NULL, NULL, NULL, NULL, NULL, '2025-09-06 13:15:59', '2025-09-06 00:01:00', 'I250005', '2025-09-06 00:00:00', NULL, NULL, NULL, NULL, NULL, '2025-09-06', 0x00, 'Yes', '', '9316856975', NULL, NULL, '', '', 1, '', '2025-09-07 14:00:43', 'asasa', 6, 6, 5, 4, 1),
(6, 'Ahmedabad', NULL, NULL, NULL, NULL, NULL, '2025-09-06 16:30:10', '2025-09-27 00:00:00', 'I250006', '2025-09-26 00:00:00', NULL, NULL, NULL, NULL, NULL, '2025-09-06', 0x00, 'Yes', '', '9900990099', NULL, NULL, '', '', 2, '', NULL, 'Ahmedabad', 10, 4, 6, 7, 1),
(7, 'Ahmedabad', NULL, NULL, NULL, NULL, NULL, '2025-09-06 17:00:53', '2025-09-25 00:00:00', 'I250007', '2025-09-19 00:00:00', NULL, NULL, NULL, NULL, NULL, '2025-09-06', 0x00, NULL, '', '9316856975', NULL, NULL, '', '', 0, '', '2025-09-06 21:51:18', 'asa', 5, 3, 4, 4, 1),
(8, 'Ahmedabad', NULL, NULL, NULL, NULL, NULL, '2025-09-07 17:03:58', '2025-09-13 00:00:00', 'I250008', '2025-09-10 00:00:00', NULL, NULL, NULL, NULL, NULL, '2025-09-07', 0x00, 'Yes', '', '9316856975', NULL, NULL, '', '', 1, '', NULL, 'VEDIK RESORT', 6, 16, 4, 4, 1),
(9, 'Porbandar', NULL, NULL, NULL, NULL, NULL, '2025-09-08 11:07:27', '2025-09-21 00:00:00', 'I250009', '2025-09-20 00:00:00', NULL, NULL, NULL, NULL, NULL, '2025-09-08', 0x00, 'Yes', '', '7895448994', NULL, NULL, '', '', 1, '', NULL, 'VEDIK RESORT', 10, 18, 5, 13, 1),
(10, 'Ahmedabad', NULL, NULL, NULL, NULL, NULL, '2025-09-08 11:16:34', '2025-09-13 00:00:00', 'I250010', '2025-09-11 00:00:00', NULL, NULL, NULL, NULL, NULL, '2025-09-08', 0x00, 'Yes', '', '9316856974', NULL, NULL, '', '', 0, '', NULL, 'VEDIK RESORT', 5, 16, 4, 10, 1);
UNLOCK TABLES;

--
-- Table structure for table `eventtype`
--

DROP TABLE IF EXISTS `eventtype`;
CREATE TABLE `eventtype` (
  `event_type_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `is_delete` bit(1) NOT NULL,
  `name_english` varchar(255) NOT NULL,
  `name_gujarati` varchar(255) DEFAULT NULL,
  `name_hindi` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`event_type_id`),
  KEY `FKefjjas5fc7ye3hqiql6ytp5ax` (`user_id`),
  CONSTRAINT `FKefjjas5fc7ye3hqiql6ytp5ax` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `eventtype`
--

LOCK TABLES `eventtype` WRITE;
INSERT INTO `eventtype` (`event_type_id`, `created_at`, `is_delete`, `name_english`, `name_gujarati`, `name_hindi`, `updated_at`, `user_id`) VALUES
(1, '2025-08-23 23:34:49', 0x01, 'WEDDING', '', '', '2025-08-26 13:22:14', 1),
(2, '2025-08-24 11:48:42', 0x00, 'Haldi', 'હલ્દી', 'हल्दी', NULL, 2),
(3, '2025-08-26 13:05:40', 0x01, 'Baby Shower', '', '', '2025-08-27 15:55:35', 1),
(4, '2025-08-26 13:17:53', 0x00, 'Haldis', '', '', '2025-09-02 17:19:23', 1),
(5, '2025-08-26 13:19:08', 0x00, 'Sangeet', '', '', NULL, 1),
(6, '2025-08-26 13:44:13', 0x00, 'WEDDING ', '', '', NULL, 1),
(7, '2025-08-27 15:40:59', 0x01, 'test', '', '', NULL, 1),
(8, '2025-08-27 15:56:51', 0x01, 'sss', '', '', NULL, 1),
(9, '2025-08-27 16:00:01', 0x01, 'test1', '', '', NULL, 1),
(10, '2025-08-27 16:03:15', 0x00, 'Party', '', '', NULL, 1),
(11, '2025-09-02 17:32:20', 0x00, 'tghjk', '', '', NULL, 1),
(12, '2025-09-02 17:50:22', 0x00, 'test', '', '', NULL, 1),
(13, '2025-09-03 17:07:21', 0x00, 'krish', '', '', NULL, 1),
(14, '2025-09-03 17:07:48', 0x00, 'krishhh', '', '', NULL, 1),
(15, '2025-09-03 21:05:14', 0x00, 'as', 'asd', 'asdasd', NULL, 1);
UNLOCK TABLES;

--
-- Table structure for table `functions`
--

DROP TABLE IF EXISTS `functions`;
CREATE TABLE `functions` (
  `function_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `end_time` varchar(255) DEFAULT NULL,
  `name_english` varchar(255) DEFAULT NULL,
  `name_gujarati` varchar(255) DEFAULT NULL,
  `name_hindi` varchar(255) DEFAULT NULL,
  `start_time` varchar(255) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `created_at` datetime NOT NULL,
  `is_delete` bit(1) NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`function_id`),
  KEY `FK5dwdmss73y84j8fqr3h3tlfe1` (`user_id`),
  CONSTRAINT `FK5dwdmss73y84j8fqr3h3tlfe1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `functions`
--

LOCK TABLES `functions` WRITE;
INSERT INTO `functions` (`function_id`, `end_time`, `name_english`, `name_gujarati`, `name_hindi`, `start_time`, `user_id`, `created_at`, `is_delete`, `updated_at`) VALUES
(1, '10:00 AM', 'Breakfast', 'બ્રેકફાસ્ટ', 'ब्रेकफास्ट', '08:00 AM', 1, '2025-08-25 10:28:29', 0x01, '2025-08-25 10:29:00'),
(2, '13:13', 'Rahul', 'રાહુલ', 'राहुल', '09:12', 1, '2025-08-25 12:17:38', 0x01, NULL),
(3, '01:05', 'Aarya', 'આર્યા', 'आर्य', '06:06', 1, '2025-08-25 12:58:45', 0x01, NULL),
(4, '11:00', 'Dinner', '', '', '10:09', 1, '2025-08-25 13:15:43', 0x00, '2025-09-02 13:17:39'),
(5, '05:05', 'Lunch', '', '', '00:05', 1, '2025-08-25 13:17:20', 0x01, NULL),
(6, '06:00', 'BreakFast', '', '', '03:00', 1, '2025-08-25 13:18:26', 0x01, NULL),
(7, '13:24', 'Haldi Dinner', '', '', '00:07', 1, '2025-08-25 13:24:23', 0x01, NULL),
(8, '04:07', 'Lunch', '', '', '00:06', 1, '2025-08-25 16:17:13', 0x01, NULL),
(9, '07:00', 'Haldi Dinner', '', '', '06:00', 1, '2025-08-26 12:39:11', 0x01, NULL),
(10, '00:06', 'Lunch', '', '', '00:06', 1, '2025-08-27 13:14:11', 0x01, NULL),
(11, '07:00', 'lunch', '', '', '01:00', 1, '2025-08-27 15:42:17', 0x01, NULL),
(12, '11:00', 'lunch', '', '', '03:00', 1, '2025-08-27 15:42:53', 0x00, '2025-08-27 15:54:32'),
(13, '06:00', 'test1', '', '', '02:00', 1, '2025-08-27 16:04:39', 0x01, NULL),
(14, '03:00', 'Party Dinners', '', '', '03:00', 1, '2025-08-27 19:40:09', 0x01, '2025-08-27 19:40:30'),
(15, '04:15', 'Hi-tea', '', '', '04:00', 1, '2025-08-28 18:42:01', 0x00, NULL),
(16, '04:15', 'Hi-coffee', '', '', '04:00', 1, '2025-08-28 18:47:09', 0x00, NULL),
(17, '05:00', 'qasdfgh', '', '', '00:05', 1, '2025-08-30 12:32:48', 0x01, NULL),
(18, '05:05', 'kkkk', '', '', '01:03', 1, '2025-09-02 12:20:03', 0x01, NULL),
(19, '02:00', 'test', '', '', '01:00', 1, '2025-09-03 17:10:45', 0x00, NULL),
(20, '05:08', 'ert', 'erter', 'rte', '00:04', 1, '2025-09-04 20:47:54', 0x00, NULL),
(21, '02:03', 'test12', 'test12', 'test12', '00:01', 1, '2025-09-04 21:55:35', 0x00, NULL),
(22, '07:00', 'Sangeet', '', '', '00:11', 1, '2025-09-06 13:35:55', 0x00, NULL);
UNLOCK TABLES;

--
-- Table structure for table `kitchenarea`
--

DROP TABLE IF EXISTS `kitchenarea`;
CREATE TABLE `kitchenarea` (
  `kitchen_area_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `is_delete` bit(1) NOT NULL,
  `name_english` varchar(255) DEFAULT NULL,
  `name_gujarati` varchar(255) DEFAULT NULL,
  `name_hindi` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`kitchen_area_id`),
  KEY `FKbw4la8upk22fi0ek5oygtr0q0` (`user_id`),
  CONSTRAINT `FKbw4la8upk22fi0ek5oygtr0q0` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `kitchenarea`
--

LOCK TABLES `kitchenarea` WRITE;
INSERT INTO `kitchenarea` (`kitchen_area_id`, `created_at`, `is_active`, `is_delete`, `name_english`, `name_gujarati`, `name_hindi`, `updated_at`, `user_id`) VALUES
(1, '2025-09-02 11:25:10', 0x01, 0x01, 'dishs', 'string', 'string', '2025-09-03 11:36:24', 1),
(2, '2025-09-03 11:19:41', 0x01, 0x00, 'spoons', 'string', 'string', '2025-09-03 12:12:05', 1),
(3, '2025-09-03 11:26:24', 0x01, 0x00, 'utensilss', 'string', 'string', '2025-09-03 12:45:48', 1),
(4, '2025-09-03 11:30:54', 0x01, 0x01, 'test', '', '', NULL, 1),
(5, '2025-09-03 12:41:52', 0x01, 0x01, 'test', '', '', NULL, 1),
(6, '2025-09-03 12:43:52', 0x01, 0x01, 'test', '', '', NULL, 1),
(7, '2025-09-03 13:27:39', 0x01, 0x01, 'tests', '', '', '2025-09-03 13:27:45', 1),
(8, '2025-09-04 11:31:40', 0x01, 0x00, 'test', '', '', NULL, 1);
UNLOCK TABLES;

--
-- Table structure for table `mealtype`
--

DROP TABLE IF EXISTS `mealtype`;
CREATE TABLE `mealtype` (
  `meal_type_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `is_delete` bit(1) NOT NULL,
  `name_english` varchar(255) DEFAULT NULL,
  `name_gujarati` varchar(255) DEFAULT NULL,
  `name_hindi` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`meal_type_id`),
  KEY `FKeu9gxpgej4m9awkr54elsbp8r` (`user_id`),
  CONSTRAINT `FKeu9gxpgej4m9awkr54elsbp8r` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `mealtype`
--

LOCK TABLES `mealtype` WRITE;
INSERT INTO `mealtype` (`meal_type_id`, `created_at`, `is_delete`, `name_english`, `name_gujarati`, `name_hindi`, `updated_at`, `user_id`) VALUES
(1, '2025-08-25 13:25:46', 0x01, 'Jain 10%', 'જૈન ૧૦%', 'जैन १०%', '2025-08-25 13:26:55', 1),
(2, '2025-08-25 17:35:52', 0x01, 'Jain Meal', 'string', 'string', NULL, 1),
(3, '2025-08-25 17:36:12', 0x01, 'Veg Meals', '', '', '2025-09-02 17:19:12', 1),
(4, '2025-08-25 17:36:22', 0x00, 'Non-Vegetarian-Meal', '', '', '2025-09-04 16:37:56', 1),
(5, '2025-08-25 17:36:32', 0x00, 'Vegan Meal', 'string', 'string', NULL, 1),
(6, '2025-08-25 17:36:43', 0x00, 'Gluten-Free Meal', 'string', 'string', NULL, 1),
(7, '2025-08-25 17:36:52', 0x00, 'Keto Meal', 'string', 'string', NULL, 1),
(8, '2025-08-25 17:37:00', 0x01, 'Diabetic-Friendly Meal', 'string', 'string', NULL, 1),
(9, '2025-08-25 17:37:10', 0x01, 'Kids Special Meal', 'string', 'string', NULL, 1),
(10, '2025-08-25 19:07:51', 0x01, '30% veg 70% non veg', '', '', NULL, 1),
(11, '2025-08-25 19:13:01', 0x01, '50%jain 50%normal', '', '', NULL, 1),
(12, '2025-08-27 15:36:07', 0x00, 'test', '', '', NULL, 1),
(13, '2025-08-27 15:37:11', 0x00, 'test1', '', '', '2025-08-27 15:37:14', 1),
(14, '2025-09-01 23:37:46', 0x00, 'lunch', '', '', NULL, 1),
(15, '2025-09-03 17:12:37', 0x00, 'test111', '', '', NULL, 1);
UNLOCK TABLES;

--
-- Table structure for table `memuitems`
--

DROP TABLE IF EXISTS `memuitems`;
CREATE TABLE `memuitems` (
  `menu_item_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `is_delete` bit(1) NOT NULL,
  `name_english` varchar(255) DEFAULT NULL,
  `name_gujarati` varchar(255) DEFAULT NULL,
  `name_hindi` varchar(255) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `sequence` int(11) DEFAULT NULL,
  `slogan` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `kitchen_area_id` bigint(20) NOT NULL,
  `menu_category_id` bigint(20) NOT NULL,
  `menu_sub_category_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`menu_item_id`),
  KEY `FKk2o6sa48ujn6b1i41aydxumyp` (`kitchen_area_id`),
  KEY `FKrt2cpdpv4oujltqo2arbmjp1` (`menu_category_id`),
  KEY `FKc5ven8drleq4xs3cpy4ggytd` (`menu_sub_category_id`),
  KEY `FKe2mhul4cqmm0gpta56lpw7t05` (`user_id`),
  CONSTRAINT `FKc5ven8drleq4xs3cpy4ggytd` FOREIGN KEY (`menu_sub_category_id`) REFERENCES `menusubcategory` (`menu_sub_cat_id`),
  CONSTRAINT `FKe2mhul4cqmm0gpta56lpw7t05` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKk2o6sa48ujn6b1i41aydxumyp` FOREIGN KEY (`kitchen_area_id`) REFERENCES `kitchenarea` (`kitchen_area_id`),
  CONSTRAINT `FKrt2cpdpv4oujltqo2arbmjp1` FOREIGN KEY (`menu_category_id`) REFERENCES `menucategory` (`menu_category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `memuitems`
--

LOCK TABLES `memuitems` WRITE;
INSERT INTO `memuitems` (`menu_item_id`, `created_at`, `image_path`, `is_active`, `is_delete`, `name_english`, `name_gujarati`, `name_hindi`, `price`, `sequence`, `slogan`, `updated_at`, `kitchen_area_id`, `menu_category_id`, `menu_sub_category_id`, `user_id`) VALUES
(1, '2025-09-02 13:00:40', '/jcupload/MenuCategory/2/f083f09c-2c90-4d84-8029-37eb379eadd5.jpg', 0x01, 0x01, 'abcd', 'string', 'string', '35.00', 11, 'qqq', '2025-09-02 13:10:17', 1, 2, 5, 1),
(2, '2025-09-02 13:02:27', NULL, 0x01, 0x01, 'pqr', '', '', '30.00', 22, 'abc', NULL, 1, 2, 5, 1),
(3, '2025-09-03 17:38:08', NULL, 0x01, 0x01, 'test1', ' ', ' ', '550.00', 10, ' ', NULL, 3, 3, 2, 1),
(4, '2025-09-03 17:38:21', NULL, 0x01, 0x01, 'test2', ' ', ' ', '550.00', 9, ' ', NULL, 3, 3, 2, 1),
(5, '2025-09-03 17:38:28', NULL, 0x01, 0x01, 'test3', ' ', ' ', '550.00', 8, ' ', NULL, 3, 3, 2, 1),
(6, '2025-09-03 17:38:31', NULL, 0x01, 0x01, 'test4', ' ', ' ', '550.00', 7, ' ', NULL, 3, 3, 2, 1),
(7, '2025-09-03 17:38:36', NULL, 0x01, 0x01, 'test5', ' ', ' ', '550.00', 6, ' ', NULL, 3, 3, 2, 1),
(8, '2025-09-03 17:40:29', NULL, 0x01, 0x01, 'test6', ' ', ' ', '550.00', 16, ' ', NULL, 3, 10, 2, 1),
(9, '2025-09-03 17:40:41', NULL, 0x01, 0x01, 'test74', ' ', ' ', '550.00', 15, ' ', NULL, 3, 10, 2, 1),
(10, '2025-09-03 17:40:48', NULL, 0x01, 0x01, 'test8.0', ' ', ' ', '550.00', 14, ' ', NULL, 3, 10, 2, 1),
(11, '2025-09-03 17:41:06', NULL, 0x01, 0x01, 'test656', ' ', ' ', '550.00', 13, ' ', NULL, 3, 11, 2, 1),
(12, '2025-09-03 18:13:10', NULL, 0x01, 0x01, ' drink', 'string', 'string', '100.00', 12, 'string', NULL, 2, 2, 2, 1),
(13, '2025-09-04 19:00:35', NULL, 0x01, 0x01, 'asd', '', '', '123.00', 3, 'asd', NULL, 2, 2, 2, 1),
(14, '2025-09-04 19:02:57', NULL, 0x01, 0x01, 'qwe', '', '', '12.00', 2, 'asdf', NULL, 3, 2, 6, 1),
(15, '2025-09-04 19:46:13', NULL, 0x01, 0x01, 'pizza', '', '', '600.00', 1, '', NULL, 8, 3, 11, 1),
(16, '2025-09-04 20:05:11', NULL, 0x01, 0x01, 'pizza', '', '', '20.00', 8, 'this', NULL, 3, 9, 12, 1),
(17, '2025-09-04 20:29:39', NULL, 0x00, 0x01, 'kitkatttt', '', '', '50.00', 12, 'abc', '2025-09-04 21:55:58', 3, 17, 11, 1),
(18, '2025-09-04 20:33:41', NULL, 0x00, 0x01, 'rice', '', '', '4040.00', 18, 'abc', NULL, 3, 3, 11, 1),
(19, '2025-09-04 20:36:55', NULL, 0x00, 0x01, 'veg', '', '', '40.00', 17, 'abc', NULL, 3, 3, 11, 1),
(20, '2025-09-04 20:40:26', '/jcupload/MenuItem/20/5a8bf41e-a2c7-443c-95df-855e9f1f714a.jpg', 0x01, 0x01, 'idliiii', '', '', '70.00', 11, 'abc', '2025-09-04 21:56:19', 3, 2, 11, 1),
(21, '2025-09-04 20:43:57', '/jcupload/MenuItem/21/991b97b7-1284-4562-9dcb-0c452e4610b6.png', 0x01, 0x01, 'dosa', '', '', '90.00', 15, 'abc', NULL, 3, 3, 11, 1),
(22, '2025-09-04 20:47:06', NULL, 0x01, 0x01, 'vada', '', '', '44.00', 14, 'abc', NULL, 3, 2, 11, 1),
(23, '2025-09-04 20:48:36', '/jcupload/MenuItem/23/d2a2b445-0374-40bb-adb4-61034927b78c.png', 0x01, 0x01, 'sss', '', '', '4.00', 13, 'dd', NULL, 3, 2, 11, 1),
(24, '2025-09-04 21:28:07', NULL, 0x01, 0x01, 'gg', '', '', '5.00', 12, 'gg', NULL, 3, 2, 11, 1),
(25, '2025-09-04 23:30:33', '/jcupload/MenuItem/25/4d0be2f8-d06b-4aad-bbd4-90d1703961cc.png', 0x01, 0x01, 'PopCorn', '', '', '255.00', 10, 'asf', NULL, 2, 2, 11, 1),
(26, '2025-09-05 12:56:51', '/jcupload/MenuItem/26/463b8286-2505-4544-9aa8-f185c3e29e74.png', 0x01, 0x01, 'cheese', '', '', '50.00', 9, 'bc', '2025-09-05 12:57:28', 2, 6, 11, 1),
(27, '2025-09-05 13:10:04', '/jcupload/MenuItem/27/de405f1b-6867-483c-b5bc-6d7d25833743.png', 0x01, 0x01, 'panipuri', '', '', '50.00', 8, 'bcd', NULL, 3, 6, 11, 1),
(28, '2025-09-05 13:13:26', '/jcupload/MenuItem/28/3aba407f-bcaf-4674-8274-0c30a302b8e4.png', 0x01, 0x01, 'test', '', '', '55.00', 7, 'bb', NULL, 2, 6, 11, 1),
(29, '2025-09-05 13:19:18', '/jcupload/MenuItem/29/7e5401b8-f610-49d4-a07d-60f99c32761a.jpg', 0x00, 0x01, 'chocolate', '', '', '44.00', 1, 'chocolate', '2025-09-05 13:57:46', 8, 17, 11, 1),
(30, '2025-09-05 13:21:56', '/jcupload/MenuItem/30/cd77dec9-916a-416a-993f-2c612baeea47.jpg', 0x00, 0x01, 'roti', '', '', '33.00', 1, 'ff', '2025-09-05 13:54:59', 3, 2, 11, 1),
(31, '2025-09-05 13:37:03', '/jcupload/MenuItem/31/d1ad2c0a-e635-4039-bfcf-fb90cda78173.jpg', 0x01, 0x01, 'manchurians', '', '', '300.00', 2, 'abc', '2025-09-05 13:52:12', 2, 18, 11, 1),
(32, '2025-09-05 13:44:13', NULL, 0x01, 0x01, 'noodles', '', '', '500.00', 2, 'noodle', NULL, 3, 18, 11, 1),
(33, '2025-09-05 13:45:41', NULL, 0x01, 0x01, 'noodle', '', '', '500.00', 1, 'noddles', NULL, 3, 18, 11, 1),
(34, '2025-09-05 13:52:56', '/jcupload/MenuItem/34/ae3ea0a1-9122-4082-88fd-30eb4d58c26a.jpg', 0x01, 0x01, 'noddle', '', '', '500.00', 1, 'noddle', NULL, 3, 18, 11, 1),
(35, '2025-09-05 19:35:43', '/jcupload/MenuItem/35/a04cd56b-bb3c-4afa-80a5-5ac152367649.jpeg', 0x01, 0x00, 'Orange juice', '', '', '150.00', 8, 'orange juice', NULL, 3, 19, 12, 1),
(36, '2025-09-05 19:36:37', '/jcupload/MenuItem/36/bfb84eca-a27c-46ba-91fb-959b0b769f84.jpeg', 0x01, 0x00, 'manchow ', '', '', '120.00', 7, 'manchow ', NULL, 3, 20, 12, 1),
(37, '2025-09-05 19:37:35', '/jcupload/MenuItem/37/f87c9643-512f-4b52-84d4-12f4e7f1dd1c.jpeg', 0x01, 0x00, 'Manchurian', '', '', '250.00', 6, 'Manchurian', NULL, 3, 21, 12, 1),
(38, '2025-09-05 19:38:38', '/jcupload/MenuItem/38/8784ea71-e260-40dd-8e74-1b44f1211755.jpeg', 0x01, 0x00, 'Paneer Butter Masala', '', '', '250.00', 5, 'Paneer Butter Masala', '2025-09-05 19:38:46', 3, 22, 12, 1),
(39, '2025-09-05 19:39:47', '/jcupload/MenuItem/39/40e1e6e7-4a45-42ef-b426-c8c23d9f0553.jpeg', 0x01, 0x00, 'Tandoori Roti ', '', '', '32.00', 4, 'Tandoori Roti ', NULL, 3, 22, 12, 1),
(40, '2025-09-05 22:16:36', '/jcupload/MenuItem/40/3cb4fad6-9094-42e7-b80f-327b465dfc41.jpeg', 0x00, 0x00, 'Papad ', '', '', '150.00', 3, 'papad ', NULL, 3, 22, 12, 1),
(41, '2025-09-06 15:34:57', NULL, 0x01, 0x01, 'Dosa', '', '', '10.00', 2, 'Dosa', NULL, 3, 22, 12, 1),
(42, '2025-09-06 15:38:25', NULL, 0x01, 0x01, 'Dosa masala', '', '', '150.00', 1, 'Dosa masala', NULL, 3, 22, 12, 1);
UNLOCK TABLES;

--
-- Table structure for table `menucategory`
--

DROP TABLE IF EXISTS `menucategory`;
CREATE TABLE `menucategory` (
  `menu_category_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `image_path` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `is_delete` bit(1) NOT NULL,
  `menuslogan` varchar(255) DEFAULT NULL,
  `name_english` varchar(255) DEFAULT NULL,
  `name_gujarati` varchar(255) DEFAULT NULL,
  `name_hindi` varchar(255) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `sequence` int(11) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`menu_category_id`),
  KEY `FKbdywxmus3apgivqxwe8v08dmy` (`user_id`),
  CONSTRAINT `FKbdywxmus3apgivqxwe8v08dmy` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `menucategory`
--

LOCK TABLES `menucategory` WRITE;
INSERT INTO `menucategory` (`menu_category_id`, `created_at`, `image_path`, `is_active`, `is_delete`, `menuslogan`, `name_english`, `name_gujarati`, `name_hindi`, `price`, `sequence`, `updated_at`, `user_id`) VALUES
(1, '2025-08-27 15:06:08', NULL, 0x00, 0x01, 'abc', 'Starter', '', '', '1000.00', 5, NULL, 1),
(2, '2025-08-27 15:10:23', '/jcupload/MenuCategory/2/f083f09c-2c90-4d84-8029-37eb379eadd5.jpg', 0x01, 0x01, 'abc', 'Welcome Drinks upfate', '', '', '100.00', 13, '2025-09-02 23:32:28', 1),
(3, '2025-08-27 15:10:55', '/jcupload/MenuCategory/3/058f8d33-ebdf-4d35-9158-02abea17e0ca.jpg', 0x01, 0x01, 'abc', 'Soup', '', '', '100.00', 16, NULL, 1),
(4, '2025-08-27 15:12:05', '/jcupload/MenuCategory/4/6d34a3f3-cf1b-43df-a3a7-48710646112e.jpg', 0x01, 0x01, 'qqq', 'www', '', '', '1000.00', 14, '2025-08-27 17:44:39', 1),
(5, '2025-08-30 13:13:41', NULL, 0x01, 0x01, 'test', 'test', 'string', 'string', '0.00', 0, NULL, 1),
(6, '2025-09-02 17:17:10', NULL, 0x01, 0x01, NULL, 'Punjabi', '', '', '23.00', 11, NULL, 1),
(7, '2025-09-02 22:59:58', NULL, 0x01, 0x01, NULL, 'abc', 'string', 'string', '1.00', 9, NULL, 1),
(8, '2025-09-02 23:00:16', NULL, 0x01, 0x01, NULL, 'string1', 'string1', 'string1', '1.00', 2, NULL, 1),
(9, '2025-09-02 23:08:30', NULL, 0x01, 0x01, NULL, 'Pizza', 'પિજ્જા.', 'पिज़्ज़ा', '399.00', 3, NULL, 1),
(10, '2025-09-02 23:09:50', NULL, 0x01, 0x01, NULL, 'test2', 'test 2', '53w5', '333.00', 8, NULL, 1),
(11, '2025-09-02 23:15:45', NULL, 0x01, 0x01, NULL, 'test3', 'test3', '53w5', '333.00', 7, NULL, 1),
(12, '2025-09-02 23:16:29', NULL, 0x01, 0x01, NULL, 'test4', 'test 4', '53w5', '333.00', 6, NULL, 1),
(13, '2025-09-02 23:17:23', NULL, 0x01, 0x01, NULL, 'test5', 'test 4', '53w5', '333.00', 5, NULL, 1),
(14, '2025-09-02 23:18:34', NULL, 0x01, 0x01, NULL, 'test421', 'test 4', '53w5', '333.00', 4, NULL, 1),
(15, '2025-09-02 23:18:50', '/jcupload/MenuCategory/15/42776e4c-fb2e-4008-9608-b551dfb59771.jpg', 0x01, 0x01, NULL, 'test421sda', 'test 4', '53w5', '333.00', 3, '2025-09-02 23:23:44', 1),
(16, '2025-09-03 10:34:32', NULL, 0x01, 0x01, NULL, 'test1', '', '', '33.00', 33, NULL, 1),
(17, '2025-09-03 22:04:17', NULL, 0x01, 0x01, NULL, 'asd', '', '', '2.00', 1, NULL, 1),
(18, '2025-09-05 13:35:52', NULL, 0x01, 0x01, NULL, 'starter', '', '', '299.00', 199, NULL, 1),
(19, '2025-09-05 19:23:12', '/jcupload/MenuCategory/19/d585b72c-8033-47c2-9b71-19879bd6dc9e.jpeg', 0x01, 0x00, NULL, 'Welcome Drink', '', '', '250.00', 16, NULL, 1),
(20, '2025-09-05 19:23:54', '/jcupload/MenuCategory/20/1ec6265e-6c1b-4d84-873c-a1839074fe23.jpeg', 0x01, 0x00, NULL, 'Soup', '', '', '500.00', 15, NULL, 1),
(21, '2025-09-05 19:24:16', '/jcupload/MenuCategory/21/8dae35bf-b983-4b8f-9932-9e9d655864ed.jpeg', 0x01, 0x00, NULL, 'Starters', '', '', '300.00', 52, NULL, 1),
(22, '2025-09-05 19:24:57', '/jcupload/MenuCategory/22/81c172fb-24ab-4085-98ea-ecb31b8b2fb9.jpeg', 0x01, 0x00, NULL, 'Main Course', '', '', '1300.00', 102, NULL, 1),
(23, '2025-09-05 19:25:19', '/jcupload/MenuCategory/23/3303fa81-4375-4675-8fa3-2e041f98a616.jpg', 0x01, 0x01, NULL, 'Starter', '', '', '200.00', 1, NULL, 1),
(24, '2025-09-06 15:57:45', NULL, 0x01, 0x01, NULL, 'Veg Meal', '', '', '120.00', 1, NULL, 1);
UNLOCK TABLES;

--
-- Table structure for table `menupreparation`
--

DROP TABLE IF EXISTS `menupreparation`;
CREATE TABLE `menupreparation` (
  `menu_preparation_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `is_delete` bit(1) NOT NULL,
  `pax` int(11) DEFAULT NULL,
  `price` decimal(19,2) DEFAULT NULL,
  `sortorder` int(11) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `event_function_id` bigint(20) NOT NULL,
  `default_price` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`menu_preparation_id`),
  KEY `FKoo3hs39vbx2h3p55t6jcyn110` (`event_function_id`),
  CONSTRAINT `FKoo3hs39vbx2h3p55t6jcyn110` FOREIGN KEY (`event_function_id`) REFERENCES `event_function` (`event_function_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `menupreparation`
--

LOCK TABLES `menupreparation` WRITE;
INSERT INTO `menupreparation` (`menu_preparation_id`, `created_at`, `is_delete`, `pax`, `price`, `sortorder`, `updated_at`, `event_function_id`, `default_price`) VALUES
(1, '2025-09-05 21:42:01', 0x00, 120, '674.00', 1, NULL, 1, '0.00'),
(2, '2025-09-05 22:56:53', 0x00, 22, '145.00', 1, NULL, 2, '22.00'),
(3, '2025-09-06 09:48:49', 0x00, 22, '44.00', 1, NULL, 3, '22.00'),
(4, '2025-09-06 11:01:38', 0x00, 22, '8.00', 1, NULL, 4, '2.00'),
(5, '2025-09-07 14:02:25', 0x00, 500, '702.00', 1, NULL, 10, '200.00'),
(6, '2025-09-08 10:34:19', 0x00, 250, '952.00', 1, NULL, 11, '25.00'),
(7, '2025-09-08 12:46:57', 0x00, 1520, '320.00', 1, NULL, 5, '200.00'),
(8, '2025-09-08 12:50:20', 0x00, 85, '165.00', 1, NULL, 13, '55.00');
UNLOCK TABLES;

--
-- Table structure for table `menupreparationdetails`
--

DROP TABLE IF EXISTS `menupreparationdetails`;
CREATE TABLE `menupreparationdetails` (
  `menu_preparation_details_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `item_notes` varchar(255) DEFAULT NULL,
  `item_price` decimal(10,2) DEFAULT NULL,
  `item_sortorder` int(11) DEFAULT NULL,
  `menu_category_name` varchar(255) DEFAULT NULL,
  `menuitem_name` varchar(255) DEFAULT NULL,
  `menu_notes` varchar(255) DEFAULT NULL,
  `menu_sortorder` int(11) DEFAULT NULL,
  `starttime` varchar(255) DEFAULT NULL,
  `menu_category_id` bigint(20) NOT NULL,
  `menu_item_id` bigint(20) NOT NULL,
  `menu_preparation_id` bigint(20) NOT NULL,
  `item_slogan` varchar(255) DEFAULT NULL,
  `menu_slogan` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`menu_preparation_details_id`),
  KEY `FKqjtx0e5gl4o6kxvf4fl9ipd7e` (`menu_category_id`),
  KEY `FK1hxt651y1gfaq9gbr2plrjlru` (`menu_item_id`),
  KEY `FK5i6iecdw4m6b2wlk26swjywuy` (`menu_preparation_id`),
  CONSTRAINT `FK1hxt651y1gfaq9gbr2plrjlru` FOREIGN KEY (`menu_item_id`) REFERENCES `memuitems` (`menu_item_id`),
  CONSTRAINT `FK5i6iecdw4m6b2wlk26swjywuy` FOREIGN KEY (`menu_preparation_id`) REFERENCES `menupreparation` (`menu_preparation_id`),
  CONSTRAINT `FKqjtx0e5gl4o6kxvf4fl9ipd7e` FOREIGN KEY (`menu_category_id`) REFERENCES `menucategory` (`menu_category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=126 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `menupreparationdetails`
--

LOCK TABLES `menupreparationdetails` WRITE;
INSERT INTO `menupreparationdetails` (`menu_preparation_details_id`, `created_at`, `item_notes`, `item_price`, `item_sortorder`, `menu_category_name`, `menuitem_name`, `menu_notes`, `menu_sortorder`, `starttime`, `menu_category_id`, `menu_item_id`, `menu_preparation_id`, `item_slogan`, `menu_slogan`) VALUES
(48, '2025-09-06 10:56:26', '', '22.00', 1, 'Soup', 'manchow ', '', 1, '12/09/2025 12:00 am', 20, 36, 2, NULL, NULL),
(49, '2025-09-06 10:56:26', '', '22.00', 2, 'Starters', 'Manchurian', '', 2, '12/09/2025 12:00 am', 21, 37, 2, NULL, NULL),
(50, '2025-09-06 10:56:26', '', '22.00', 3, 'Main Course', 'Tandoori Roti ', '', 3, '12/09/2025 12:00 am', 22, 39, 2, NULL, NULL),
(51, '2025-09-06 10:56:26', '', '22.00', 4, 'Main Course', 'Paneer Butter Masala', '', 4, '12/09/2025 12:00 am', 22, 38, 2, NULL, NULL),
(57, '2025-09-06 10:57:30', '', '22.00', 1, 'Welcome Drinks upfate', 'abcd', '', 1, '12/09/2025 12:00 am', 2, 1, 3, NULL, NULL),
(58, '2025-09-06 10:57:30', '', '22.00', 2, 'Soup', 'manchow ', '', 2, '12/09/2025 12:00 am', 20, 36, 3, NULL, NULL),
(71, '2025-09-07 14:02:25', '', '200.00', 1, 'Main Course', 'Paneer Butter Masala', '', 1, '06/09/2025 12:00 am', 22, 38, 5, NULL, NULL),
(74, '2025-09-07 14:02:25', '', '200.00', 4, 'Welcome Drink', 'Orange juice', '', 4, '06/09/2025 12:00 am', 19, 35, 5, NULL, NULL),
(75, '2025-09-07 14:02:25', '', '200.00', 5, 'Soup', 'manchow ', '', 5, '06/09/2025 12:00 am', 20, 36, 5, NULL, NULL),
(76, '2025-09-07 17:05:26', '', '2.00', 1, 'Starters', 'Manchurian', '', 1, '12/09/2025 12:00 am', 21, 37, 4, NULL, NULL),
(77, '2025-09-07 17:05:26', '', '2.00', 2, 'Main Course', 'Paneer Butter Masala', '', 2, '12/09/2025 12:00 am', 22, 38, 4, NULL, NULL),
(78, '2025-09-07 17:05:26', '', '2.00', 3, 'Welcome Drink', 'Orange juice', '', 3, '12/09/2025 12:00 am', 19, 35, 4, NULL, NULL),
(79, '2025-09-07 17:05:26', '', '2.00', 4, 'Soup', 'manchow ', '', 4, '12/09/2025 12:00 am', 20, 36, 4, NULL, NULL),
(80, '2025-09-08 10:34:19', '', '25.00', 1, 'Welcome Drink', 'Orange juice', '', 1, '10/09/2025 12:00 am', 19, 35, 6, NULL, NULL),
(81, '2025-09-08 10:34:19', '', '25.00', 2, 'Starters', 'Manchurian', '', 2, '10/09/2025 12:00 am', 21, 37, 6, NULL, NULL),
(82, '2025-09-08 10:34:19', '', '25.00', 3, 'Soup', 'manchow ', '', 3, '10/09/2025 12:00 am', 20, 36, 6, NULL, NULL),
(83, '2025-09-08 10:34:19', '', '25.00', 4, 'Main Course', 'Paneer Butter Masala', '', 4, '10/09/2025 12:00 am', 22, 38, 6, NULL, NULL),
(84, '2025-09-08 10:34:19', '', '25.00', 5, 'Main Course', 'Tandoori Roti ', '', 5, '10/09/2025 12:00 am', 22, 39, 6, NULL, NULL),
(85, '2025-09-08 10:34:19', '', '25.00', 6, 'Main Course', 'Papad ', '', 6, '10/09/2025 12:00 am', 22, 40, 6, NULL, NULL),
(105, '2025-09-08 12:39:33', 'This is very good ', '95.00', 1, 'Soup', 'manchow ', '', 1, '14/09/2025 12:00 am', 20, 36, 1, 'Test of best ', NULL),
(106, '2025-09-08 12:39:33', '', '297.00', 2, 'Starters', 'Manchurian', '', 2, '14/09/2025 12:00 am', 21, 37, 1, '', NULL),
(107, '2025-09-08 12:39:33', '', '0.00', 3, 'Main Course', 'Tandoori Roti ', '', 3, '14/09/2025 12:00 am', 22, 39, 1, '', NULL),
(108, '2025-09-08 12:39:33', '', '0.00', 4, 'Main Course', 'Paneer Butter Masala', '', 4, '14/09/2025 12:00 am', 22, 38, 1, '', NULL),
(112, '2025-09-08 12:47:56', '', '200.00', 1, 'Welcome Drink', 'Orange juice', '', 1, '29/09/2025 12:00 am', 19, 35, 7, '', NULL),
(113, '2025-09-08 12:47:56', '', '200.00', 2, 'Soup', 'manchow ', '', 2, '29/09/2025 12:00 am', 20, 36, 7, '', NULL),
(123, '2025-09-08 13:27:58', '', '55.00', 1, 'Soup', 'manchow ', '', 1, '11/09/2025 12:00 am', 20, 36, 8, '', ''),
(124, '2025-09-08 13:27:58', '', '55.00', 2, 'Welcome Drink', 'Orange juice', 'Test', 2, '11/09/2025 12:00 am', 19, 35, 8, '', 'test is best '),
(125, '2025-09-08 13:27:58', '', '55.00', 3, 'Starters', 'Manchurian', '', 3, '11/09/2025 12:00 am', 21, 37, 8, '', '');
UNLOCK TABLES;

--
-- Table structure for table `menusubcategory`
--

DROP TABLE IF EXISTS `menusubcategory`;
CREATE TABLE `menusubcategory` (
  `menu_sub_cat_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `is_delete` bit(1) NOT NULL,
  `name_english` varchar(255) DEFAULT NULL,
  `name_gujarati` varchar(255) DEFAULT NULL,
  `name_hindi` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`menu_sub_cat_id`),
  KEY `FKsnwiwc3j8l5eidl3ot195mho5` (`user_id`),
  CONSTRAINT `FKsnwiwc3j8l5eidl3ot195mho5` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `menusubcategory`
--

LOCK TABLES `menusubcategory` WRITE;
INSERT INTO `menusubcategory` (`menu_sub_cat_id`, `created_at`, `is_active`, `is_delete`, `name_english`, `name_gujarati`, `name_hindi`, `updated_at`, `user_id`) VALUES
(1, '2025-08-29 21:46:08', 0x01, 0x01, 'LIVE', '', '', '2025-08-29 21:48:05', 2),
(2, '2025-08-29 21:46:36', 0x00, 0x00, 'Live', 'લાઇવ', 'लाइव', '2025-08-30 15:45:32', 1),
(3, '2025-08-30 15:10:14', 0x01, 0x01, 'OFF LINE', '', '', '2025-08-30 15:14:12', 1),
(4, '2025-08-30 15:19:10', 0x00, 0x01, 'MOBILE', '', '', '2025-08-30 15:37:12', 1),
(5, '2025-08-30 15:47:01', 0x00, 0x00, 'OFF LINE', '', '', NULL, 1),
(6, '2025-08-31 17:17:47', 0x00, 0x00, 'Onlines', '', '', '2025-09-02 11:19:52', 1),
(7, '2025-08-31 17:17:57', 0x01, 0x01, 'Test', '', '', NULL, 1),
(8, '2025-08-31 17:24:40', 0x01, 0x00, 'Test', '', '', NULL, 1),
(9, '2025-09-02 11:18:00', 0x01, 0x00, 'idli', '', '', NULL, 1),
(10, '2025-09-02 17:38:10', 0x00, 0x01, 'asd', '', '', NULL, 1),
(11, '2025-09-02 23:46:17', 0x01, 0x00, 'Child Updated.', 'ચાઇલ્ડ.', 'चाइल्ड।', '2025-09-02 23:46:28', 1),
(12, '2025-09-03 22:07:24', 0x01, 0x00, 'Category', '', '', NULL, 1);
UNLOCK TABLES;

--
-- Table structure for table `partymaster`
--

DROP TABLE IF EXISTS `partymaster`;
CREATE TABLE `partymaster` (
  `party_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address_english` varchar(255) NOT NULL,
  `address_gujarati` varchar(255) DEFAULT NULL,
  `address_hindi` varchar(255) DEFAULT NULL,
  `alt_mobileno` varchar(255) DEFAULT NULL,
  `birth_date` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `doc_path` varchar(255) DEFAULT NULL,
  `document` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `gst` varchar(255) DEFAULT NULL,
  `is_delete` bit(1) NOT NULL,
  `mobileno` varchar(255) NOT NULL,
  `name_english` varchar(255) NOT NULL,
  `name_gujarati` varchar(255) DEFAULT NULL,
  `name_hindi` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `contact_category_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`party_id`),
  KEY `FK1axnaasfi5eq901u40dmr3iwo` (`contact_category_id`),
  KEY `FKdwar1pcfk8pbkbvat51frefdm` (`user_id`),
  CONSTRAINT `FK1axnaasfi5eq901u40dmr3iwo` FOREIGN KEY (`contact_category_id`) REFERENCES `contact_category` (`contact_category_id`),
  CONSTRAINT `FKdwar1pcfk8pbkbvat51frefdm` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `partymaster`
--

LOCK TABLES `partymaster` WRITE;
INSERT INTO `partymaster` (`party_id`, `address_english`, `address_gujarati`, `address_hindi`, `alt_mobileno`, `birth_date`, `created_at`, `doc_path`, `document`, `email`, `gst`, `is_delete`, `mobileno`, `name_english`, `name_gujarati`, `name_hindi`, `updated_at`, `contact_category_id`, `user_id`) VALUES
(1, 'patan', 'પાટણ', 'पाटन', '1234567890', '2006-03-11 00:00:00', '2025-08-23 11:28:50', '/uploads/Party/1/ee220e7c-86e9-4b36-8c56-a684a7f452c4.png', 'driving', 'vivek@gmail.com', '1234567890', 0x01, '9316856975', 'Vivek', 'વિવેક', 'विवेक', '2025-08-29 12:45:52', 2, 1),
(2, 'Ahmedabad', 'string', 'string', 'string', '2025-08-20 00:00:00', '2025-08-23 12:34:55', NULL, 'string', 'shree.aarya@gmail.com', 'string', 0x01, '1234567890', 'Aarya', 'string', 'string', NULL, 2, 1),
(3, 'patan', 'પાટણ', 'पाटन', '', '2003-12-07 00:00:00', '2025-08-23 16:17:31', NULL, 'pan', 'vivek@gmail.com', '1234567890', 0x01, '9900990099', 'Vivek', 'વિવેક', 'विवेक', NULL, 2, 2),
(4, 'Ahmedabad', '', '', '1234567890', '2025-08-23 00:00:00', '2025-08-23 16:24:19', NULL, 'passport', 'shree.tarun2025@gmail.com', '123456', 0x00, '9316856975', 'Tarun Maheshwari', '', '', '2025-09-02 18:42:20', 4, 1),
(5, 'Ahmedabad', '', '', '1234567890', '2025-08-25 00:00:00', '2025-08-23 16:28:00', NULL, 'pan', 'info@justwedding.in', '123456', 0x01, '9316856976', 'Aman', '', '', '2025-08-29 12:46:08', 2, 1),
(6, 'patan', '', '', '1234567890', '2003-08-23 00:00:00', '2025-08-25 15:45:16', NULL, 'pan', 'shree.ritesh2025@gmail.com', '123456', 0x00, '9316856972', 'Ritesh', '', '', '2025-08-29 12:46:17', 2, 1),
(7, 'Ahmedabad', '', '', '1234567890', '2001-06-24 00:00:00', '2025-08-25 15:54:45', NULL, 'pan', 'info@justwedding.in', '1234567890', 0x00, '9900990099', 'Aman Sha', '', '', NULL, 2, 1),
(8, 'patan', '', '', '1234567890', '2003-08-23 00:00:00', '2025-08-25 16:00:20', NULL, 'pan', 'shree.ritesh2025@gmail.com', '123456', 0x01, '9900990099', 'Ritesh', '', '', NULL, 2, 1),
(9, 'Ahmedabad', '', '', '1234567890', '2025-08-25 00:00:00', '2025-08-25 16:02:05', NULL, 'pan', 'info@justwedding.in', '123456', 0x01, '1234567890', 'Aman Jha', '', '', NULL, 2, 1),
(10, 'Ahmedabad', '', '', '1234567890', '2001-01-22 00:00:00', '2025-08-25 17:00:11', NULL, 'driving', 'shree.aarya2025@gmail.com', '1234567890', 0x00, '9316856974', 'Aarya', '', '', '2025-08-29 12:46:24', 2, 1),
(11, 'patan', 'પાટણ', 'पाटन', '1234567890', '2025-08-30 00:00:00', '2025-08-30 20:07:18', NULL, 'pan', 'vivek@gmail.com', '1234567890', 0x01, '9900990099', 'Vivek', 'વિવેક', 'विवेक', NULL, 6, 1),
(12, 'patan', 'પાટણ', 'पाटन', '1234567890', '2025-08-30 00:00:00', '2025-09-01 16:57:41', NULL, 'aadhar', 'vivek@gmail.com', '1234567890', 0x00, '9900990099', 'Vivek', 'વિવેક', 'विवेक', '2025-09-03 22:50:23', 4, 1),
(13, 'Porbandar', '', '', '7458658954', '2025-09-24 00:00:00', '2025-09-02 18:43:49', NULL, 'pan', 'rahul@gmail.com', '74569545635', 0x00, '7895448994', 'Rahul Gohel', '', '', '2025-09-02 18:44:20', 4, 1),
(14, 'Ahmedabad', '', '', '1234567890', '2001-07-03 00:00:00', '2025-09-04 14:46:14', NULL, 'pan', 'vivek@gmail.com', '1234567890', 0x00, '9900990099', 'Vivek', '', '', NULL, 9, 2),
(15, 'Ahmedabad', '', '', '1234567890', '2025-09-03 00:00:00', '2025-09-04 14:48:32', NULL, 'pan', 'shree.ritesh2025@gmail.com', 'DHEPM2754K', 0x00, '9316856975', 'Aarya', '', '', NULL, 9, 2),
(16, 'Ahmedabad', '', '', '1234567890', '2025-09-03 00:00:00', '2025-09-04 14:49:23', NULL, 'passport', 'shree.aarya2025@gmail.com', '1234567890', 0x00, '9316856976', 'Ritesh', '', '', NULL, 9, 2),
(17, 'Ahmedabad', '', '', '1234567890', '2025-09-03 00:00:00', '2025-09-04 14:52:06', NULL, 'pan', 'shree.ritesh2025@gmail.com', '123456', 0x00, '9316856974', 'Vivek', '', '', NULL, 9, 2),
(18, 'Ahmedabad', '', '', '1234567890', '2025-10-01 00:00:00', '2025-09-04 14:55:40', NULL, 'pan', 'shree.aarya2025@gmail.com', '1234567890', 0x00, '9316856975', 'Vivek', '', '', NULL, 6, 1),
(19, 'Rajasthan', '', '', '7456969954', '2005-09-06 00:00:00', '2025-09-06 11:42:56', NULL, 'aadhar', 'zainab@gmail.com', '741258935', 0x00, '7558659584', 'Zainab', '', '', NULL, 6, 1),
(20, 'sdfgb', '', '', '7745886956', '2025-09-04 00:00:00', '2025-09-06 11:52:21', NULL, 'aadhar', 'jay@gmail.com', '741852963', 0x00, '7458696958', 'Raj', '', '', NULL, 6, 1),
(21, 'sdfghjk', '', '', '', '2025-09-12 00:00:00', '2025-09-06 11:57:44', NULL, 'pan', 'rohit45@gmail.com', '', 0x00, '7418529636', 'rohit', '', '', NULL, 4, 1);
UNLOCK TABLES;

--
-- Table structure for table `plan_features`
--

DROP TABLE IF EXISTS `plan_features`;
CREATE TABLE `plan_features` (
  `plan_feature_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `feature_text` varchar(255) NOT NULL,
  `is_delete` bit(1) NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `plan_id` bigint(20) NOT NULL,
  PRIMARY KEY (`plan_feature_id`),
  KEY `FKmii31u2imuu6cet94c3yv7c0p` (`plan_id`),
  CONSTRAINT `FKmii31u2imuu6cet94c3yv7c0p` FOREIGN KEY (`plan_id`) REFERENCES `plans` (`plan_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `plan_features`
--

LOCK TABLES `plan_features` WRITE;
INSERT INTO `plan_features` (`plan_feature_id`, `created_at`, `feature_text`, `is_delete`, `updated_at`, `plan_id`) VALUES
(3, '2025-08-21 12:26:29', 'xyz', 0x01, NULL, 1),
(4, '2025-08-21 13:22:03', 'abc', 0x01, NULL, 2),
(5, '2025-08-21 13:22:03', 'pqr', 0x01, NULL, 2),
(6, '2025-08-21 13:22:03', 'xyz', 0x01, NULL, 2),
(9, '2025-08-21 13:31:28', 'BBB', 0x01, NULL, 2),
(10, '2025-08-21 13:31:28', 'AAA', 0x01, NULL, 2),
(11, '2025-08-22 11:16:00', 'All', 0x01, NULL, 3),
(12, '2025-08-22 12:21:15', 'xyz', 0x01, NULL, 4),
(13, '2025-08-22 15:56:32', 'xyz', 0x01, NULL, 1),
(14, '2025-08-22 15:58:16', 'xyz', 0x01, NULL, 1),
(15, '2025-08-22 15:58:16', 'abc', 0x01, NULL, 1),
(16, '2025-08-22 15:59:43', 'xyz', 0x01, NULL, 1),
(17, '2025-08-22 15:59:43', 'abc', 0x01, NULL, 1),
(18, '2025-08-22 16:24:31', 'xyz', 0x01, NULL, 4),
(19, '2025-08-22 16:30:03', 'Fully responsive Webflow template', 0x00, NULL, 4),
(20, '2025-08-22 16:30:03', 'CMS + Figma file included', 0x00, NULL, 4),
(21, '2025-08-22 16:30:03', 'SEO-ready structure', 0x00, NULL, 4),
(22, '2025-08-22 16:30:03', 'Email support included', 0x00, NULL, 4),
(23, '2025-08-22 16:35:59', 'Fully responsive Webflow template', 0x00, NULL, 1),
(24, '2025-08-22 16:35:59', 'Modular & scalable components', 0x00, NULL, 1),
(25, '2025-08-22 16:35:59', 'Easy-to-edit CMS setup', 0x00, NULL, 1),
(26, '2025-08-22 16:46:17', 'All included in Agency', 0x00, NULL, 3),
(27, '2025-08-22 16:46:17', 'Custom integrations & support', 0x00, NULL, 3),
(28, '2025-08-22 16:46:17', 'Dedicated account manager', 0x00, NULL, 3),
(29, '2025-09-05 17:43:51', 'All Modules', 0x00, NULL, 5);
UNLOCK TABLES;

--
-- Table structure for table `plans`
--

DROP TABLE IF EXISTS `plans`;
CREATE TABLE `plans` (
  `plan_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `billing_cycle` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL,
  `description` text,
  `is_delete` bit(1) NOT NULL,
  `is_popular` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`plan_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `plans`
--

LOCK TABLES `plans` WRITE;
INSERT INTO `plans` (`plan_id`, `billing_cycle`, `created_at`, `description`, `is_delete`, `is_popular`, `name`, `price`, `updated_at`) VALUES
(1, 'Month', '2025-08-21 12:18:48', 'For solo designers launching standout portfolios.', 0x00, 0x00, 'Lite', '1000.00', '2025-08-22 16:35:59'),
(2, 'Yearly', '2025-08-21 13:22:03', 'AAA', 0x01, 0x01, 'E-Lite', '30000.00', '2025-08-21 13:31:28'),
(3, 'month', '2025-08-22 11:16:00', 'Custom solutions for large teams & organizations.', 0x00, 0x01, 'Premium', '20000.00', '2025-08-22 16:46:17'),
(4, 'Month', '2025-08-22 12:21:15', ' Ideal for growing agencies or collaborative design teams who need scalable templates.', 0x00, 0x00, 'ELite', '15000.00', '2025-08-22 16:30:03'),
(5, 'Week', '2025-09-05 17:43:51', 'Free Trial', 0x00, 0x01, 'demo', '1.00', NULL);
UNLOCK TABLES;

--
-- Table structure for table `quotation_items`
--

DROP TABLE IF EXISTS `quotation_items`;
CREATE TABLE `quotation_items` (
  `quotation_item_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount` decimal(10,2) DEFAULT NULL,
  `extra_pax` int(11) DEFAULT NULL,
  `function_date` datetime DEFAULT NULL,
  `function_name` varchar(255) DEFAULT NULL,
  `pax` int(11) DEFAULT NULL,
  `rate_per_plate` decimal(10,2) DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `is_delete` bit(1) NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `quotation_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`quotation_item_id`),
  KEY `FK7y587hckcncga8qtka1i06cbp` (`quotation_id`),
  CONSTRAINT `FK7y587hckcncga8qtka1i06cbp` FOREIGN KEY (`quotation_id`) REFERENCES `quotations` (`quotation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `quotation_items`
--

LOCK TABLES `quotation_items` WRITE;
INSERT INTO `quotation_items` (`quotation_item_id`, `amount`, `extra_pax`, `function_date`, `function_name`, `pax`, `rate_per_plate`, `created_at`, `is_delete`, `updated_at`, `quotation_id`) VALUES
(1, '1600.00', 30, '2025-09-14 22:00:00', 'Dinner', 130, '15.00', '2025-09-08 22:55:09', 0x00, '2025-09-08 22:55:55', 1);
UNLOCK TABLES;

--
-- Table structure for table `quotations`
--

DROP TABLE IF EXISTS `quotations`;
CREATE TABLE `quotations` (
  `quotation_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `advance_payment` decimal(19,2) DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `discount` decimal(19,2) DEFAULT NULL,
  `grand_total` decimal(19,2) DEFAULT NULL,
  `gst` varchar(255) DEFAULT NULL,
  `gst_amnt` decimal(10,2) DEFAULT NULL,
  `is_delete` bit(1) NOT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `remaining_amount` decimal(19,2) DEFAULT NULL,
  `round_off` decimal(19,2) DEFAULT NULL,
  `total_amount` decimal(10,2) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `event_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`quotation_id`),
  KEY `FKg5ydtq1cutmdrllguhbucga4v` (`event_id`),
  CONSTRAINT `FKg5ydtq1cutmdrllguhbucga4v` FOREIGN KEY (`event_id`) REFERENCES `events` (`event_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `quotations`
--

LOCK TABLES `quotations` WRITE;
INSERT INTO `quotations` (`quotation_id`, `advance_payment`, `created_at`, `discount`, `grand_total`, `gst`, `gst_amnt`, `is_delete`, `notes`, `remaining_amount`, `round_off`, `total_amount`, `updated_at`, `event_id`) VALUES
(1, '0.00', '2025-09-08 22:55:09', '0.00', '1599.00', '2.5%', '40.00', 0x00, 'abc', '1599.00', '1599.00', '1560.00', '2025-09-08 22:55:55', 1),
(2, '0.00', '2025-09-09 00:51:54', '0.00', '0.00', '', '0.00', 0x00, '', '0.00', '0.00', '0.00', NULL, 2),
(3, '0.00', '2025-09-09 00:52:13', '0.00', '0.00', '', '0.00', 0x00, '', '0.00', '0.00', '0.00', NULL, 3);
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `is_delete` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`role_id`),
  KEY `FK97mxvrajhkq19dmvboprimeg1` (`user_id`),
  CONSTRAINT `FK97mxvrajhkq19dmvboprimeg1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
INSERT INTO `roles` (`role_id`, `created_at`, `is_delete`, `name`, `updated_at`, `user_id`) VALUES
(1, '2025-08-21 15:17:44', 0x00, 'Super Admin', '2025-08-21 15:18:16', 2),
(2, '2025-08-21 15:55:02', 0x00, 'Admin', NULL, 1),
(3, '2025-08-22 18:22:20', 0x00, 'Team Member', NULL, 1),
(4, '2025-08-22 18:22:34', 0x00, 'Manager', NULL, 1),
(5, '2025-08-25 17:42:47', 0x00, 'sales', NULL, 2),
(8, '2025-08-25 18:02:40', 0x00, 'sales manager', NULL, 1),
(9, '2025-08-25 18:02:57', 0x00, 'SALES', '2025-08-25 18:03:25', 1),
(10, '2025-09-03 13:18:50', 0x00, 'Hr', NULL, 1),
(11, '2025-09-03 13:30:00', 0x00, 'sdf', NULL, 1),
(12, '2025-09-03 13:32:35', 0x00, 'asdf', NULL, 1),
(13, '2025-09-03 13:35:11', 0x00, 'qwefghgfds', NULL, 1),
(14, '2025-09-03 13:37:45', 0x00, 'asdfsddsd', NULL, 1),
(15, '2025-09-03 13:38:06', 0x00, 'rahul', NULL, 1);
UNLOCK TABLES;

--
-- Table structure for table `states`
--

DROP TABLE IF EXISTS `states`;
CREATE TABLE `states` (
  `state_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NOT NULL,
  `is_delete` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `country_id` bigint(20) NOT NULL,
  PRIMARY KEY (`state_id`),
  KEY `FKskkdphjml9vjlrqn4m5hi251y` (`country_id`),
  CONSTRAINT `FKskkdphjml9vjlrqn4m5hi251y` FOREIGN KEY (`country_id`) REFERENCES `countries` (`country_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `states`
--

LOCK TABLES `states` WRITE;
INSERT INTO `states` (`state_id`, `created_at`, `is_delete`, `name`, `updated_at`, `country_id`) VALUES
(1, '2025-08-21 17:15:03', 0x00, 'GUJARAT', '2025-08-21 17:16:29', 1),
(2, '2025-09-08 21:00:00', 0x00, 'ANDHRA PRADESH', '2025-09-08 21:00:00', 1),
(3, '2025-09-08 21:00:00', 0x00, 'ARUNACHAL PRADESH', '2025-09-08 21:00:00', 1),
(4, '2025-09-08 21:00:00', 0x00, 'ASSAM', '2025-09-08 21:00:00', 1),
(5, '2025-09-08 21:00:00', 0x00, 'BIHAR', '2025-09-08 21:00:00', 1),
(6, '2025-09-08 21:00:00', 0x00, 'CHHATTISGARH', '2025-09-08 21:00:00', 1),
(7, '2025-09-08 21:00:00', 0x00, 'GOA', '2025-09-08 21:00:00', 1),
(9, '2025-09-08 21:00:00', 0x00, 'HARYANA', '2025-09-08 21:00:00', 1),
(10, '2025-09-08 21:00:00', 0x00, 'HIMACHAL PRADESH', '2025-09-08 21:00:00', 1),
(11, '2025-09-08 21:00:00', 0x00, 'JHARKHAND', '2025-09-08 21:00:00', 1),
(12, '2025-09-08 21:00:00', 0x00, 'KARNATAKA', '2025-09-08 21:00:00', 1),
(13, '2025-09-08 21:00:00', 0x00, 'KERALA', '2025-09-08 21:00:00', 1),
(14, '2025-09-08 21:00:00', 0x00, 'MADHYA PRADESH', '2025-09-08 21:00:00', 1),
(15, '2025-09-08 21:00:00', 0x00, 'MAHARASHTRA', '2025-09-08 21:00:00', 1),
(16, '2025-09-08 21:00:00', 0x00, 'MANIPUR', '2025-09-08 21:00:00', 1),
(17, '2025-09-08 21:00:00', 0x00, 'MEGHALAYA', '2025-09-08 21:00:00', 1),
(18, '2025-09-08 21:00:00', 0x00, 'MIZORAM', '2025-09-08 21:00:00', 1),
(19, '2025-09-08 21:00:00', 0x00, 'NAGALAND', '2025-09-08 21:00:00', 1),
(20, '2025-09-08 21:00:00', 0x00, 'ODISHA', '2025-09-08 21:00:00', 1),
(21, '2025-09-08 21:00:00', 0x00, 'PUNJAB', '2025-09-08 21:00:00', 1),
(22, '2025-09-08 21:00:00', 0x00, 'RAJASTHAN', '2025-09-08 21:00:00', 1),
(23, '2025-09-08 21:00:00', 0x00, 'SIKKIM', '2025-09-08 21:00:00', 1),
(24, '2025-09-08 21:00:00', 0x00, 'TAMIL NADU', '2025-09-08 21:00:00', 1),
(25, '2025-09-08 21:00:00', 0x00, 'TELANGANA', '2025-09-08 21:00:00', 1),
(26, '2025-09-08 21:00:00', 0x00, 'TRIPURA', '2025-09-08 21:00:00', 1),
(27, '2025-09-08 21:00:00', 0x00, 'UTTAR PRADESH', '2025-09-08 21:00:00', 1),
(28, '2025-09-08 21:00:00', 0x00, 'UTTARAKHAND', '2025-09-08 21:00:00', 1),
(29, '2025-09-08 21:00:00', 0x00, 'WEST BENGAL', '2025-09-08 21:00:00', 1);
UNLOCK TABLES;

--
-- Table structure for table `user_basic_details`
--

DROP TABLE IF EXISTS `user_basic_details`;
CREATE TABLE `user_basic_details` (
  `user_basic_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `company_name` varchar(255) DEFAULT NULL,
  `country_code` varchar(255) DEFAULT NULL,
  `is_attendance_leave_access` bit(1) NOT NULL,
  `is_task_access` bit(1) NOT NULL,
  `reporting_manager_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `city_id` bigint(20) DEFAULT NULL,
  `country_id` bigint(20) DEFAULT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  `state_id` bigint(20) DEFAULT NULL,
  `company_email` varchar(255) DEFAULT NULL,
  `office_no` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `is_delete` bit(1) NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`user_basic_id`),
  KEY `FK1nijwbg4a7lspjgayhmgtbjgk` (`user_id`),
  KEY `FKbigf43b2iyrpd5k90vcukmuto` (`city_id`),
  KEY `FK8xexcn1iw9q3ag5x85qydarr1` (`country_id`),
  KEY `FKs0wbgkw4rqily9k81mjppkq2x` (`role_id`),
  KEY `FK17jpol7ql7qaw10w2mk6l0d69` (`state_id`),
  CONSTRAINT `FK17jpol7ql7qaw10w2mk6l0d69` FOREIGN KEY (`state_id`) REFERENCES `states` (`state_id`),
  CONSTRAINT `FK1nijwbg4a7lspjgayhmgtbjgk` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FK8xexcn1iw9q3ag5x85qydarr1` FOREIGN KEY (`country_id`) REFERENCES `countries` (`country_id`),
  CONSTRAINT `FKbigf43b2iyrpd5k90vcukmuto` FOREIGN KEY (`city_id`) REFERENCES `cities` (`city_id`),
  CONSTRAINT `FKs0wbgkw4rqily9k81mjppkq2x` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_basic_details`
--

LOCK TABLES `user_basic_details` WRITE;
INSERT INTO `user_basic_details` (`user_basic_id`, `company_name`, `country_code`, `is_attendance_leave_access`, `is_task_access`, `reporting_manager_id`, `user_id`, `city_id`, `country_id`, `role_id`, `state_id`, `company_email`, `office_no`, `address`, `created_at`, `is_delete`, `updated_at`) VALUES
(1, 'Shree Info', '+91', 0x01, 0x01, 0, 1, 1, 1, 1, 1, 'shreeinfo11@gmail.com', '9900998899', 'Ahmedabad', '2025-08-22 10:56:40', 0x00, '2025-09-06 16:02:28'),
(2, 'Jaival', '+91', 0x01, 0x01, 0, 2, 1, 1, 2, 1, 'jayval@gmail.com', '1234567890', 'ahmedabad', '2025-08-24 11:48:14', 0x00, '2025-09-02 16:24:05'),
(3, 'Shree Infotech', '+91', 0x01, 0x01, 1, 3, 1, 1, 4, 1, 'shreeinfo@gmail.com', '9988779900', 'patan', '2025-08-24 18:43:14', 0x00, NULL),
(4, 'abc', '+91', 0x01, 0x01, 0, 4, 1, 1, 2, 1, 'abc@gmail.com', '9695457488', 'rajkot', '2025-08-30 16:08:27', 0x00, NULL),
(5, 'info', '+91', 0x01, 0x01, 0, 5, 1, 1, 1, 1, 'info@gmail.com', '1234567890', 'abc', '2025-08-30 16:19:25', 0x00, NULL),
(6, 'string', '+91', 0x01, 0x01, 0, 6, 1, 1, 2, 1, 'st@gmail.com', 'string', 'string', '2025-09-01 11:38:13', 0x00, '2025-09-01 17:50:37'),
(7, 'shree', '+91', 0x01, 0x01, 0, 7, 1, 1, 1, 1, 'shreeinfo@gmail.com', '9887765443', 'Ahmedabad', '2025-09-01 12:34:00', 0x00, NULL),
(8, 'Shree Info', '+91', 0x01, 0x01, 0, 8, 1, 1, 2, 1, 'shree@gmail.com', '9887765443', 'Ahmedabad', '2025-09-01 12:38:16', 0x00, NULL),
(9, 'shree', '+91', 0x01, 0x01, 0, 9, 1, 1, 2, 1, 'geeta@gmail.com', '9887765443', 'Ahmedabad', '2025-09-01 12:55:20', 0x00, NULL),
(10, 'shree', '+91', 0x00, 0x00, 0, 10, 1, 1, 2, 1, 'pratham@gamil.com', '9887765443', 'Ahmedabad', '2025-09-01 13:27:41', 0x00, NULL),
(11, 'shree', '+91', 0x01, 0x01, 0, 11, 1, 1, 2, 1, 'shreeinfo@gmail.com', '9887765443', 'porbandar', '2025-09-01 16:08:15', 0x00, NULL),
(12, 'Shree Info', '+91', 0x01, 0x01, 0, 12, 1, 1, 2, 1, 'shreeinfo@gmail.com', '9887765443', 'Ahmedabad', '2025-09-01 17:43:28', 0x00, NULL),
(13, 'Shree Info', '+91', 0x01, 0x01, 0, 13, 1, 1, 2, 1, 'diay@gmail.com', '9887765443', 'Ahmedabad', '2025-09-01 19:31:41', 0x00, NULL),
(14, 'shree infotech', '+91', 0x01, 0x01, 0, 14, 1, 1, 2, 1, 'shree@gmail.com', '7458569654', 'asdf', '2025-09-02 14:10:49', 0x00, '2025-09-02 14:25:15'),
(15, 'Shree', '+91', 0x01, 0x01, 0, 15, 1, 1, 3, 1, 'shree@gmail.com', '7458625633', 'asdfg', '2025-09-02 18:58:30', 0x00, NULL),
(16, 'Shree', '+91', 0x01, 0x01, 0, 16, 1, 1, 3, 1, 'shree@gmail.com', '7458625633', 'asdfg', '2025-09-02 19:05:31', 0x00, NULL),
(17, 'Shree Info', '+91', 0x01, 0x01, 0, 17, 1, 1, 4, 1, 'rahulshree@gmail.com', '9900998899', 'Ahmedabad', '2025-09-02 23:03:40', 0x00, NULL),
(18, 'Shree Info', '+91', 0x01, 0x00, 0, 18, 1, 1, 3, 1, 'aaryashree@gmail.com', '9900998899', 'Ahmedabad', '2025-09-02 23:05:30', 0x00, NULL),
(19, 'Shree Info', '+91', 0x01, 0x01, 0, 19, 1, 1, 3, 1, 'aaryashree@gmail.com', '9900998899', 'Ahmedabad', '2025-09-02 23:08:26', 0x00, NULL),
(20, 'Shree Info', '+91', 0x00, 0x01, 0, 20, 1, 1, 2, 1, 'rahul.rahulgohel2091@gmail.com', '9900998899', 'Ahmedabad', '2025-09-02 23:14:55', 0x00, '2025-09-06 16:03:54'),
(21, 'Shree Info', '+91', 0x01, 0x01, 0, 21, 1, 1, 4, 1, 'ankitwe@gmail.com', '9900998899', 'Ahmedabad', '2025-09-03 00:17:06', 0x00, NULL),
(22, 'Shree Info', '+91', 0x01, 0x01, 0, 22, 1, 1, 3, 1, 'kaushiloff@gmail.com', '9900998899', 'Ahmedabad', '2025-09-03 11:34:18', 0x00, '2025-09-04 16:20:06'),
(23, 'Shree Info', '+91', 0x01, 0x01, 0, 23, 1, 1, 2, 1, 'mananoffice@gmail.com', '9900998899', 'Ahmedabad', '2025-09-04 16:13:01', 0x00, NULL),
(24, 'Shree Info', '+91', 0x01, 0x01, 0, 24, 1, 1, 2, 1, 'shree@gmail.com', '9887765443', 'Ahmedabad', '2025-09-05 17:57:45', 0x00, NULL),
(25, 'Shree Info', '+91', 0x01, 0x01, 0, 25, 1, 1, 2, 1, 'shree.aarya1234@gmail.com', '9887765443', 'Ahmedabad', '2025-09-05 18:18:27', 0x00, NULL),
(26, 'Shree Info', '+91', 0x01, 0x01, 0, 26, 1, 1, 2, 1, 'geeta@gmail.com', '9887765443', 'Ahmedabad', '2025-09-06 14:45:36', 0x00, NULL),
(27, 'Shree Info', '+91', 0x01, 0x01, 0, 27, 1, 1, 2, 1, 'geeta@gmail.com', '9887765443', 'Ahmedabad', '2025-09-06 15:19:51', 0x00, NULL),
(28, 'Shree Info', '+91', 0x01, 0x01, 0, 28, 1, 1, 2, 1, 'shree.aayushi25@gmail', '9900998899', 'Ahmedabad', '2025-09-06 15:38:03', 0x00, '2025-09-06 17:26:46'),
(29, 'Shree Info', '+91', 0x00, 0x01, 0, 29, 1, 1, 3, 1, 'hitenshree@gmail.com', '9900998899', 'Ahmedabad', '2025-09-06 15:51:36', 0x00, '2025-09-06 15:51:47'),
(30, 'tcs', '+91', 0x01, 0x01, 0, 30, 1, 1, 2, 1, 'zainab@gmail.com', '1234567890', 'sdf', '2025-09-06 16:26:19', 0x00, NULL),
(31, 'TCS', '+91', 0x01, 0x01, 0, 31, 1, 1, 2, 1, 'rahul.rahulgohel2091@gmail.com', '1234567890', 'cvbnm', '2025-09-06 16:38:46', 0x00, NULL),
(32, 'Shree Info', '+91', 0x01, 0x01, 0, 32, 1, 1, 2, 1, 'shreeinfo@gmail.com', '9887765443', 'Ahmedabad', '2025-09-06 17:28:27', 0x00, NULL),
(33, 'Shree Infotech', '+91', 0x01, 0x01, 17, 33, 1, 1, 2, 1, 'shreedigvijay14@gmail.com', '7458569654', 'Ashok Chok', '2025-09-07 16:37:24', 0x00, NULL),
(34, 'shree infotech', '+91', 0x01, 0x01, 17, 34, 1, 1, 2, 1, 'info@justwedding.in', '7458569654', 'memnagar', '2025-09-07 17:10:43', 0x00, NULL),
(35, 'dd', '+91', 0x01, 0x01, 3, 35, 1, 1, 2, 1, 'krish.patel@example.com', 'N/A', 'Ahmedabad', '2025-09-08 11:31:21', 0x00, NULL),
(36, 'dd', '+91', 0x01, 0x01, 3, 36, 1, 1, 2, 1, 'shree@gmail.com', '9875202337', 'Ahmedabad', '2025-09-08 11:33:54', 0x00, NULL),
(37, 'Shree Infotech', '+91', 0x01, 0x01, 18, 37, 1, 1, 2, 1, 'tatariyazainab@gmail.com', '8209792623', 'Kuwait', '2025-09-08 11:43:45', 0x00, NULL),
(38, 'Shree Info', '+91', 0x01, 0x01, 18, 38, 1, 1, 2, 1, 'zainab.shree15@gmail.com', '8209792623', 'Kuwait', '2025-09-08 11:48:02', 0x00, NULL),
(39, 'Shree Info', '+91', 0x01, 0x01, 3, 39, 1, 1, 2, 1, 'trix@gmail.com', '9876543210', 'Ahmedabad', '2025-09-08 12:31:08', 0x00, NULL),
(40, 'Shree Info', '+91', 0x01, 0x01, 3, 40, 1, 1, 2, 1, 'dharma@gmail.com', '9723607808', 'Ahmedabad', '2025-09-08 12:40:10', 0x00, NULL),
(41, 'Shree Info', '+91', 0x01, 0x01, 3, 41, 1, 1, 2, 1, 'shree.depp109@gmail.com', '9313309498', 'Ahmedabad', '2025-09-08 13:32:11', 0x00, NULL),
(42, 'shree infotech', '+91', 0x01, 0x01, 16, 43, 1, 1, 2, 1, 'admin@gmail.com', '6351914169', 'MITHILA DHAM,BEHIND RELIANCE PETROL PUMP,BOKHIRA,PORBANDAR, PORBANDAR', '2025-09-08 15:10:27', 0x00, NULL),
(43, 'Shree Info', '+91', 0x01, 0x01, 3, 42, 1, 1, 2, 1, 'kirtan@gmail.com', '6351914169', 'Ahmedabad', '2025-09-08 15:10:27', 0x00, NULL),
(44, 'Shree Info', '+91', 0x01, 0x01, 3, 44, 2, 1, 2, 1, 'shree.swapnil1010@gmail.com', '9173288306', 'Ahmedabad', '2025-09-09 11:03:10', 0x00, NULL);
UNLOCK TABLES;

--
-- Table structure for table `user_otp`
--

DROP TABLE IF EXISTS `user_otp`;
CREATE TABLE `user_otp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `expiry_time` datetime DEFAULT NULL,
  `otp` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20) DEFAULT NULL,
  `contact_no` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `is_delete` bit(1) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `otp` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `is_approve` bit(1) DEFAULT NULL,
  `plan_id` bigint(20) DEFAULT NULL,
  `usercode` varchar(255) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `is_first_time` bit(1) NOT NULL,
  PRIMARY KEY (`user_id`),
  KEY `FKi4ttvfkp7tl33d90ldkkagrxu` (`plan_id`),
  CONSTRAINT `FKi4ttvfkp7tl33d90ldkkagrxu` FOREIGN KEY (`plan_id`) REFERENCES `plans` (`plan_id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
INSERT INTO `users` (`user_id`, `client_id`, `contact_no`, `created_at`, `email`, `first_name`, `is_delete`, `last_name`, `otp`, `password`, `updated_at`, `is_active`, `is_approve`, `plan_id`, `usercode`, `remarks`, `end_date`, `start_date`, `is_first_time`) VALUES
(1, 0, '8530696935', '2025-08-22 10:56:40', 'rahul.shree0101@gmail.com', 'Vivek', 0x00, 'Prajapati', NULL, '$2a$10$XfcCrWuGdacwq47XAZvujOSnhBfVppSgX/is842Qz0mI58eWhMbBC', '2025-09-06 16:02:28', 0x01, 0x01, 1, NULL, NULL, '2025-09-10 11:20:00', '2025-09-06 16:54:36', 0x00),
(2, 0, '9875202337', '2025-08-24 11:48:14', 'shree.tarun2025@gmail.com', 'Jay', 0x00, 'Patel', NULL, '$2a$10$.MGNTKkbokW4R09.oK76gOVDrU3q3Rybq/Mdm4h8KDDUgwFIzaIPi', '2025-09-02 16:24:05', 0x01, 0x01, 4, NULL, NULL, '2025-09-09 22:06:43', '2025-09-06 16:54:36', 0x00),
(3, 1, '9900990099', '2025-08-24 18:43:14', 'prajapativivek949@gmail.com', 'Vraj', 0x00, 'Prajapati', NULL, '$2a$10$heohS9q62z/KL8oGht4aWOYf.Z0yhlYBS1HeOKVelgcyPH/E36Lii', NULL, 0x00, 0x00, 1, NULL, NULL, '2025-09-10 11:20:00', '2025-09-06 16:54:36', 0x00),
(4, 0, '7585969545', '2025-08-30 16:08:27', 'xyz@gmail.com', 'rahul', 0x00, 'gohel', NULL, '$2a$10$Pd/FXuIFFrix0CXu5Osmfeq9oCHMvs3uEKUcVeRj6kxQayiWiNDem', NULL, 0x01, 0x01, 1, NULL, NULL, NULL, NULL, 0x00),
(5, 0, '1234567890', '2025-08-30 16:19:25', 'krishna@gmail.com', 'krishna', 0x00, 'shah', NULL, '$2a$10$rgrX3RxZFsMZYGvm7AZQPOz2BN.9LUjYXEYUX22RjhlvU6BcUpoQm', NULL, 0x00, 0x00, 1, NULL, NULL, NULL, NULL, 0x00),
(6, 0, '92345678990', '2025-09-01 11:38:13', 'abc@gmail.com', 'string', 0x00, 'string', NULL, '$2a$10$rFKrfX/X4N/SRaWIPgX/Sel3PudRS28WI0N6g8I5sTG7Xtych3gT2', '2025-09-01 17:50:37', 0x00, 0x00, 1, NULL, NULL, NULL, NULL, 0x00),
(7, 0, '9876543210', '2025-09-01 12:34:00', 'knv4n@somoj.com', 'krishna', 0x00, 'patel', NULL, '$2a$10$HgYb8RRtd.4QApJflW/DcuiUSpax6z1VFPJGltqmYf0FeNbO0YkS2', NULL, 0x00, 0x00, 1, NULL, NULL, NULL, NULL, 0x00),
(8, 0, '9876543210', '2025-09-01 12:38:16', 'meet@gmail.com', 'meet', 0x00, 'patel', NULL, '$2a$10$f1aFrlzTQGqHX6A0cDR/uulSWURfYfEHj9JMpMJk19.pCGoJwtijC', NULL, 0x00, 0x00, 1, NULL, NULL, NULL, NULL, 0x00),
(9, 0, '9876543210', '2025-09-01 12:55:20', 'geeta@gmail.com', 'geeta', 0x00, 'patel', NULL, '$2a$10$adL4U8Nub/KvrQ15ywwLGOPL3.LKb.niOcvTZ1KC01Txc1.EUaEVa', NULL, 0x00, 0x00, 1, NULL, NULL, NULL, NULL, 0x00),
(10, 0, '9876543210', '2025-09-01 13:27:41', 'pratham@gmail.com', 'pratham', 0x00, 'patel', NULL, '$2a$10$GI0VHasSBl4YhGd80Qs3..yWor68eNnb2Iih/snHeH/TqbMOY5H2e', NULL, 0x01, 0x01, 1, NULL, NULL, NULL, NULL, 0x00),
(11, 0, '9876543210', '2025-09-01 16:08:15', 'deep123@gmail.com', 'Deep', 0x00, 'sharma', NULL, '$2a$10$McJjLfQzC1bMDN6QOnbfPerHyg5wCvPtv3JQl1LHIsA/nazRmzds2', NULL, 0x00, 0x00, 4, NULL, NULL, NULL, NULL, 0x00),
(12, 0, '9876543210', '2025-09-01 17:43:28', 'nikki@gmail.com', 'nikki', 0x00, 'nikki', NULL, '$2a$10$Vj7NHJN34BkBObLb1rSeoO0/aQBdG43bfta94deKLXBwdaCtz8eFC', NULL, 0x00, 0x00, 1, NULL, NULL, NULL, NULL, 0x00),
(13, 0, '9876543210', '2025-09-01 19:31:41', 'diya@gmail.com', 'diya', 0x00, 'diya', NULL, '$2a$10$0YFuWAfXeLa7YNiye5O/VePhjY1gjYvboDLpDIS3PC/hJ7xR0U/9m', NULL, 0x00, 0x00, 1, NULL, NULL, NULL, NULL, 0x00),
(14, 0, '7458569545', '2025-09-02 14:10:49', 'swapnil@gmail.com', 'Swapnil', 0x00, 'Kumar', NULL, '$2a$10$TjElSx32vsbhwcrCXaAiYOr597o538mh50TdGWpBPmIjKo4.5ITCO', '2025-09-02 14:25:15', 0x00, 0x00, 3, NULL, NULL, NULL, NULL, 0x00),
(15, 13, '7896956585', '2025-09-02 18:58:30', 'rahul@gmail.com', 'rahul', 0x00, 'gohel', NULL, '$2a$10$H3k7ctRNHSzZeOORcD5Wc.wCwlTTSiOe7p3i4uROaUBRk2KO8w9mC', NULL, 0x00, 0x00, 4, NULL, NULL, NULL, NULL, 0x00),
(16, 1, '7896956585', '2025-09-02 19:05:31', 'rahulgohel@gmail.com', 'rahul', 0x00, 'gohel', NULL, '$2a$10$zYFoNOPUcKjtRNgNPxkxtu4bztJfIijnh0mM.mm5CH5pR0zFTGrYe', NULL, 0x00, 0x00, 4, NULL, NULL, NULL, NULL, 0x00),
(17, 1, '7777777777', '2025-09-02 23:03:40', 'rahulgohel20@gmail.com', 'rahul', 0x00, 'gohel', NULL, '$2a$10$WRKNIl22W0zkvW1tUlr0.u6k8ob1jSUDD.vr3CcJsyWCrKGfpjhqe', NULL, 0x00, 0x00, 1, NULL, NULL, NULL, NULL, 0x00),
(18, 1, '8796965458', '2025-09-02 23:05:30', 'aarya@gmail.com', 'Aarya', 0x00, 'Kansara', NULL, '$2a$10$165PBfY3PYUkeRrNbh0b1uF.rh5HOA7KsLbhGv7mqWGalk9h03diW', NULL, 0x00, 0x00, 1, NULL, NULL, NULL, NULL, 0x00),
(19, 1, '8796965458', '2025-09-02 23:08:26', 'aarya2@gmail.com', 'Aarya', 0x00, 'Kansara', NULL, '$2a$10$MSpcho50viIbyNkCVInaou9wprqgC3G0httXvT7Vad.Yuk4T3tZYW', NULL, 0x00, 0x00, 1, NULL, NULL, NULL, NULL, 0x00),
(20, 1, '1234567890', '2025-09-02 23:14:55', 'rahul.rahulgohel2091@gmail.com', 'Deep ', 0x00, 'Kumar', NULL, '$2a$10$WwhufqPbXj4AVRbMCYkBLOPuyj.Krb6W3f8BJxAw5B4TPf1ViV29G', '2025-09-06 16:03:54', 0x00, 0x00, 1, NULL, NULL, NULL, NULL, 0x00),
(21, 1, '7589695854', '2025-09-03 00:17:06', 'ankit@gmail.com', 'Ankit', 0x00, 'Patel', NULL, '$2a$10$GoSTym4x368Cu/0/YS4sdOQmnM4BzmCclvnhCZvfLLe48hGZzBaJS', NULL, 0x00, 0x00, 1, NULL, NULL, NULL, NULL, 0x00),
(22, 1, '7458695455', '2025-09-03 11:34:18', 'kaushil@gmail.com', 'Kaushils', 0x00, 'sharma', NULL, '$2a$10$cU0EOKqu8sJ2HDGllI.aIupnu1hXY5iL3lT1IkFyR.UgMVa6J.xKe', '2025-09-04 16:20:06', 0x00, 0x00, 1, NULL, NULL, NULL, NULL, 0x00),
(23, 0, '7486458744', '2025-09-04 16:13:01', 'manan@gmail.com', 'manan', 0x00, 'gandhi', NULL, '$2a$10$Xau3tR./mMLt/uFrakR0muoyu44KyEs5s98XBVmhgFsZo7sJu5wSW', NULL, 0x00, 0x00, 1, NULL, NULL, NULL, NULL, 0x00),
(24, 0, '9876543210', '2025-09-05 17:57:45', 'deep@gmail.com', 'deep', 0x00, 'patel', NULL, NULL, NULL, 0x00, 0x00, 1, NULL, NULL, NULL, NULL, 0x00),
(25, 0, '6351914169', '2025-09-05 18:18:27', 'shree.aarya1234@gmail.com', 'aarya', 0x00, 'kansara', NULL, '$2a$10$IjAtTZLD8.B36hxFKm8lu.C.vVn6cCjao3YI7x/LItm0lT648Q4.a', NULL, 0x01, 0x01, 1, NULL, NULL, NULL, NULL, 0x00),
(26, 0, '9876543210', '2025-09-06 14:45:36', 'digvijay@gmail.com', 'digvijay', 0x00, 'patel', NULL, NULL, NULL, 0x00, 0x00, 1, 'JCL250001', '', NULL, NULL, 0x00),
(27, 0, '9876543210', '2025-09-06 15:19:51', 'aaa@gmail.com', 'digvijay', 0x00, 'patel', NULL, NULL, NULL, 0x00, 0x00, 5, 'JCD250002', '', NULL, NULL, 0x00),
(28, 1, '9090909090', '2025-09-06 15:38:03', 'shree.aayushi25@gmail', 'Aayushi', 0x00, 'Sharmaa', NULL, '$2a$10$IHrQBPMhq42rHpPoXl22POq.M1eG9sCL.Xu56mGFPTbwbYX6mZoi2', '2025-09-06 17:26:46', 0x00, 0x00, 1, '', NULL, NULL, NULL, 0x00),
(29, 1, '7458966985', '2025-09-06 15:51:36', 'hiten@gmail.com', 'Hiten', 0x00, 'Sharma', NULL, NULL, '2025-09-06 15:51:47', 0x00, 0x00, 1, '', NULL, NULL, NULL, 0x00),
(30, 0, '7458969857', '2025-09-06 16:26:19', 'zainab@gmail.com', 'Zainab', 0x00, 'Tatariya', NULL, NULL, NULL, 0x00, 0x00, 5, 'JCD250002', NULL, NULL, NULL, 0x00),
(31, 0, '09974998392', '2025-09-06 16:38:46', 'shree.nikkita1234@gmail.com', 'niiki', 0x00, 'did', NULL, '$2a$10$ygsH2jlbTYdXMJLQrkhFPOKV9JVoZcPQRMLN/8pCHD912nVgANyaO', NULL, 0x01, 0x01, 5, 'JCD250002', NULL, NULL, NULL, 0x00),
(32, 0, '9876543210', '2025-09-06 17:28:27', 'harsh@gmail.com', 'harsh', 0x00, 'patel', NULL, NULL, NULL, 0x00, 0x00, 5, 'JCD250003', 'abc', NULL, NULL, 0x00),
(33, 0, '8569654455', '2025-09-07 16:37:24', 'shreedigvijay14@gmail.com', 'Digvijay', 0x00, 'Kumar', NULL, '$2a$10$rSsrHeQ40jmZGOVDhkKBiusFjQokkpzxOOZIEBfY.mciEk1Ylcdr.', NULL, 0x01, 0x01, 5, 'JCD250004', 'sd', '2025-09-14 16:38:21', '2025-09-07 16:38:21', 0x00),
(34, 0, '8866889580', '2025-09-07 17:10:43', 'info@justwedding.in', 'Manan', 0x00, 'Gandhi', NULL, '$2a$10$dFJuvFxjbzvzGsh5/xgSieR/E/o2/gl1YofpT9OOec2IGp8RCd5EK', NULL, 0x01, 0x01, 5, 'JCD250005', 'sdfg', '2025-09-14 17:11:42', '2025-09-07 17:11:42', 0x00),
(35, 0, '9876625689', '2025-09-08 11:31:21', 'krish@gmail.com', 'krish', 0x00, 'patel', NULL, NULL, NULL, 0x00, 0x00, 5, 'JCD250006', 'dd', NULL, NULL, 0x01),
(36, 0, '9875202337', '2025-09-08 11:33:54', 'shree@gmail.com', 'krish', 0x00, 'patel', NULL, NULL, NULL, 0x00, 0x00, 5, 'JCD250007', 'dd', NULL, NULL, 0x01),
(37, 0, '8209792623', '2025-09-08 11:43:41', 'tatariyazainab@gmail.com', 'Zanab', 0x00, 'Tatariya', NULL, '$2a$10$vSpqYrGmLd9a0bh/ZmEFbeRMMW6n67LgqPZi01ZNqYRQHuPJPUEzu', NULL, 0x01, 0x01, 5, 'JCD250008', 'Manan Kaka', '2025-09-15 11:45:01', '2025-09-08 11:45:01', 0x01),
(38, 0, '8209792623', '2025-09-08 11:47:58', 'zainab.shree15@gmail.com', 'Zainab', 0x00, 'Tatariya', NULL, '$2a$10$YY9iS9CH7cA21irr6ePi/ek/iMS5D32HpsgI7sZyPV3.noiokzZdS', NULL, 0x01, 0x01, 5, 'JCD250009', 'f', '2025-09-15 11:48:18', '2025-09-08 11:48:18', 0x00),
(39, 0, '9876543210', '2025-09-08 12:31:04', 'trix@gmail.com', 'trix', 0x00, 'trix', NULL, NULL, NULL, 0x00, 0x00, 5, 'JCD250010', 'acc', NULL, NULL, 0x01),
(40, 0, '9723607808', '2025-09-08 12:40:06', 'dharma@gmail.com', 'dharma', 0x00, 'karma', NULL, NULL, NULL, 0x00, 0x00, 5, 'JCD250011', 'this is digvijay', NULL, NULL, 0x01),
(41, 0, '9313309498', '2025-09-08 13:32:07', 'shree.depp109@gmail.com', 'deep', 0x00, 'patel', NULL, NULL, NULL, 0x00, 0x00, 5, 'JCD250012', 'this is digvijay', NULL, NULL, 0x01),
(42, 0, '6351914169', '2025-09-08 15:10:23', 'kirtan@gmail.com', 'kritan', 0x00, 'patel', NULL, NULL, NULL, 0x00, 0x00, 5, 'JCD250013', 'aa', NULL, NULL, 0x01),
(43, 0, '6351914169', '2025-09-08 15:10:23', 'admin@gmail.com', 'akshay', 0x00, 'kumar', NULL, NULL, NULL, 0x00, 0x00, 5, 'JCD250013', 'aa', NULL, NULL, 0x01),
(44, 0, '9173288306', '2025-09-09 11:03:06', 'shree.swapnil1010@gmail.com', 'acer', 0x00, 'patel', NULL, NULL, NULL, 0x00, 0x00, 1, 'JCL250014', 'this is digvijay', NULL, NULL, 0x01);
UNLOCK TABLES;

SET FOREIGN_KEY_CHECKS=1;



-- ======================================================
-- POS (Point of Sale) Dedicated Tables
-- ======================================================

--
-- Table structure for table `pos_floor`
--
DROP TABLE IF EXISTS `pos_floor`;
CREATE TABLE `pos_floor` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,                       -- Admin/Owner account (client_id = 0 in users table)
  `code` varchar(30) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `shortcode` varchar(20) NOT NULL,
  `sort_order` int(11) DEFAULT 1,
  `is_active` bit(1) DEFAULT b'1',
  `created_by_user_id` bigint(20) DEFAULT NULL,        -- Staff member who created this record
  `updated_by_user_id` bigint(20) DEFAULT NULL,        -- Staff member who last updated this record
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_pos_floor_user` (`user_id`),
  KEY `FK_pos_floor_creator` (`created_by_user_id`),
  CONSTRAINT `FK_pos_floor_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `FK_pos_floor_creator` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `pos_floor` WRITE;
INSERT INTO `pos_floor` (`id`, `user_id`, `code`, `name`, `shortcode`, `sort_order`, `is_active`, `created_by_user_id`) VALUES
(1, 1, 'FL-001', 'Ground Floor', 'GF', 1, 0x01, 1),
(2, 1, 'FL-002', 'Rooftop Lounge', 'RT', 2, 0x01, 1),
(3, 1, 'FL-003', 'Private Dining Hall', 'PH', 3, 0x01, 1);
UNLOCK TABLES;

--
-- Table structure for table `pos_table`
--
DROP TABLE IF EXISTS `pos_table`;
CREATE TABLE `pos_table` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,                       -- Admin/Owner account
  `code` varchar(30) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `shortcode` varchar(20) NOT NULL,
  `capacity` int(11) NOT NULL DEFAULT 4,
  `floor_id` bigint(20) DEFAULT NULL,
  `floor_code` varchar(30) DEFAULT NULL,
  `status` varchar(30) DEFAULT 'available',
  `current_order_id` bigint(20) DEFAULT NULL,
  `current_order_code` varchar(40) DEFAULT NULL,
  `is_active` bit(1) DEFAULT b'1',
  `created_by_user_id` bigint(20) DEFAULT NULL,
  `updated_by_user_id` bigint(20) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_pos_table_user` (`user_id`),
  KEY `FK_pos_table_floor` (`floor_id`),
  KEY `FK_pos_table_creator` (`created_by_user_id`),
  CONSTRAINT `FK_pos_table_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `FK_pos_table_floor` FOREIGN KEY (`floor_id`) REFERENCES `pos_floor` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_pos_table_creator` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `pos_table` WRITE;
INSERT INTO `pos_table` (`id`, `user_id`, `code`, `name`, `shortcode`, `capacity`, `floor_id`, `floor_code`, `status`, `is_active`, `created_by_user_id`) VALUES
(1, 1, 'TBL-001', 'Table 1', 'T-01', 2, 1, 'FL-001', 'available', 0x01, 1),
(2, 1, 'TBL-002', 'Table 2', 'T-02', 4, 1, 'FL-001', 'available', 0x01, 1),
(3, 1, 'TBL-003', 'Table 3', 'T-03', 4, 1, 'FL-001', 'available', 0x01, 1),
(4, 1, 'TBL-004', 'Table 4', 'T-04', 6, 1, 'FL-001', 'available', 0x01, 1),
(5, 1, 'TBL-005', 'Table 5', 'T-05', 8, 1, 'FL-001', 'available', 0x01, 1),
(6, 1, 'TBL-006', 'Table 6 (Rooftop)', 'RT-01', 4, 2, 'FL-002', 'available', 0x01, 1),
(7, 1, 'TBL-007', 'Table 7 (Rooftop)', 'RT-02', 4, 2, 'FL-002', 'available', 0x01, 1),
(8, 1, 'TBL-008', 'Table 8 (Rooftop)', 'RT-03', 6, 2, 'FL-002', 'available', 0x01, 1),
(9, 1, 'TBL-009', 'Private Suite 1', 'PH-01', 10, 3, 'FL-003', 'available', 0x01, 1),
(10, 1, 'TBL-010', 'Private Suite 2', 'PH-02', 12, 3, 'FL-003', 'available', 0x01, 1);
UNLOCK TABLES;

--
-- Table structure for table `pos_category`
--
DROP TABLE IF EXISTS `pos_category`;
CREATE TABLE `pos_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,                       -- Admin/Owner account
  `code` varchar(30) DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `sort_order` int(11) DEFAULT 1,
  `is_active` bit(1) DEFAULT b'1',
  `created_by_user_id` bigint(20) DEFAULT NULL,
  `updated_by_user_id` bigint(20) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_pos_category_user` (`user_id`),
  KEY `FK_pos_category_creator` (`created_by_user_id`),
  CONSTRAINT `FK_pos_category_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `FK_pos_category_creator` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `pos_category` WRITE;
INSERT INTO `pos_category` (`id`, `user_id`, `code`, `name`, `sort_order`, `is_active`, `created_by_user_id`) VALUES
(1, 1, 'CAT-001', 'Starters', 1, 0x01, 1),
(2, 1, 'CAT-002', 'Main Course', 2, 0x01, 1),
(3, 1, 'CAT-003', 'Rice & Biryani', 3, 0x01, 1),
(4, 1, 'CAT-004', 'Breads', 4, 0x01, 1),
(5, 1, 'CAT-005', 'Desserts', 5, 0x01, 1),
(6, 1, 'CAT-006', 'Beverages', 6, 0x01, 1);
UNLOCK TABLES;

--
-- Table structure for table `pos_item`
--
DROP TABLE IF EXISTS `pos_item`;
CREATE TABLE `pos_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,                       -- Admin/Owner account
  `code` varchar(30) DEFAULT NULL,
  `name` varchar(150) NOT NULL,
  `category_id` bigint(20) DEFAULT NULL,
  `category_code` varchar(30) DEFAULT NULL,
  `is_veg` bit(1) DEFAULT b'1',
  `pricing_type` varchar(30) DEFAULT 'portion',        -- 'portion' (per plate), 'kg' (bulk weight), 'both' (dual mode)
  `price` double DEFAULT 0,                            -- Standard portion / plate price
  `price_per_kg` double DEFAULT NULL,                  -- Rate per Kg when sold in bulk / weight
  `portion_weight_grams` int(11) DEFAULT NULL,         -- Approximate weight in grams for 1 plate
  `tax_type` varchar(20) DEFAULT 'exclusive',          -- 'exclusive', 'inclusive'
  `gst_rate` double DEFAULT 5,                         -- GST percentage (5, 12, 18, etc.)
  `tag` varchar(200) DEFAULT NULL,
  `station` varchar(60) DEFAULT 'Kitchen',             -- 'Kitchen', 'Bar', 'Dessert Counter', 'Live Counter'
  `is_active` bit(1) DEFAULT b'1',
  `created_by_user_id` bigint(20) DEFAULT NULL,
  `updated_by_user_id` bigint(20) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_pos_item_user` (`user_id`),
  KEY `FK_pos_item_cat` (`category_id`),
  KEY `FK_pos_item_creator` (`created_by_user_id`),
  CONSTRAINT `FK_pos_item_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `FK_pos_item_cat` FOREIGN KEY (`category_id`) REFERENCES `pos_category` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_pos_item_creator` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `pos_item` WRITE;
INSERT INTO `pos_item` (`id`, `user_id`, `code`, `name`, `category_id`, `category_code`, `is_veg`, `pricing_type`, `price`, `price_per_kg`, `portion_weight_grams`, `tax_type`, `gst_rate`, `station`, `is_active`, `created_by_user_id`) VALUES
(1, 1, 'PNT', 'Paneer Tikka', 1, 'CAT-001', 0x01, 'both', 220, 600, 350, 'exclusive', 5, 'Kitchen', 0x01, 1),
(2, 1, 'HBK', 'Hara Bhara Kebab', 1, 'CAT-001', 0x01, 'both', 190, 520, 300, 'exclusive', 5, 'Kitchen', 0x01, 1),
(3, 1, 'PBM', 'Paneer Butter Masala', 2, 'CAT-002', 0x01, 'both', 240, 650, 400, 'exclusive', 5, 'Kitchen', 0x01, 1),
(4, 1, 'DMK', 'Dal Makhani', 2, 'CAT-002', 0x01, 'both', 220, 550, 400, 'exclusive', 5, 'Kitchen', 0x01, 1),
(5, 1, 'VDB', 'Veg Dum Biryani', 3, 'CAT-003', 0x01, 'both', 230, 580, 500, 'exclusive', 5, 'Kitchen', 0x01, 1),
(6, 1, 'BN', 'Butter Naan', 4, 'CAT-004', 0x01, 'both', 55, 280, 100, 'exclusive', 5, 'Kitchen', 0x01, 1),
(7, 1, 'GN', 'Garlic Naan', 4, 'CAT-004', 0x01, 'both', 70, 320, 110, 'exclusive', 5, 'Kitchen', 0x01, 1),
(8, 1, 'GJ', 'Gulab Jamun', 5, 'CAT-005', 0x01, 'both', 90, 480, 150, 'inclusive', 5, 'Dessert Counter', 0x01, 1),
(9, 1, 'VM', 'Virgin Mojito', 6, 'CAT-006', 0x01, 'portion', 130, NULL, 300, 'inclusive', 18, 'Bar', 0x01, 1);
UNLOCK TABLES;

--
-- Table structure for table `pos_item_variant`
--
DROP TABLE IF EXISTS `pos_item_variant`;
CREATE TABLE `pos_item_variant` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,                       -- Admin/Owner account
  `item_id` bigint(20) NOT NULL,                       -- Reference to pos_item
  `group_name` varchar(60) NOT NULL DEFAULT 'Size',   -- 'Size', 'Portion', 'Crust', 'Add-on', 'Preparation'
  `variant_name` varchar(80) NOT NULL,                 -- 'Half', 'Full', 'Extra Cheese', 'Jain'
  `price` double NOT NULL DEFAULT 0,                   -- Variant price or delta/addon price
  `price_type` varchar(20) DEFAULT 'fixed',            -- 'fixed' (overrides base price) or 'addon' (added to base price)
  `selection_type` varchar(20) DEFAULT 'single',       -- 'single' (exclusive radio) or 'multiple' (checkbox addons)
  `is_default` bit(1) DEFAULT b'0',                    -- Default selected option
  `sort_order` int(11) DEFAULT 1,
  `is_active` bit(1) DEFAULT b'1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_pos_item_variant_user` (`user_id`),
  KEY `FK_pos_item_variant_item` (`item_id`),
  CONSTRAINT `FK_pos_item_variant_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `FK_pos_item_variant_item` FOREIGN KEY (`item_id`) REFERENCES `pos_item` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `pos_item_variant` WRITE;
INSERT INTO `pos_item_variant` (`id`, `user_id`, `item_id`, `group_name`, `variant_name`, `price`, `price_type`, `selection_type`, `is_default`, `sort_order`, `is_active`) VALUES
(1, 1, 1, 'Portion Size', 'Half (4 pcs)', 130, 'fixed', 'single', 0x00, 1, 0x01),
(2, 1, 1, 'Portion Size', 'Full (8 pcs)', 220, 'fixed', 'single', 0x01, 2, 0x01),
(3, 1, 1, 'Add-ons', 'Extra Mint Chutney', 20, 'addon', 'multiple', 0x00, 1, 0x01),
(4, 1, 3, 'Portion Size', 'Half', 140, 'fixed', 'single', 0x00, 1, 0x01),
(5, 1, 3, 'Portion Size', 'Full', 240, 'fixed', 'single', 0x01, 2, 0x01),
(6, 1, 3, 'Add-ons', 'Extra Butter Cubes', 30, 'addon', 'multiple', 0x00, 1, 0x01),
(7, 1, 5, 'Portion Size', 'Half (300g)', 140, 'fixed', 'single', 0x00, 1, 0x01),
(8, 1, 5, 'Portion Size', 'Full (600g)', 230, 'fixed', 'single', 0x01, 2, 0x01),
(9, 1, 5, 'Add-ons', 'Extra Raita', 35, 'addon', 'multiple', 0x00, 1, 0x01);
UNLOCK TABLES;

--
-- Table structure for table `pos_order`
--
DROP TABLE IF EXISTS `pos_order`;
CREATE TABLE `pos_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,                       -- Admin/Owner account (client_id = 0 in users)
  `order_code` varchar(40) NOT NULL,
  `order_type` varchar(30) NOT NULL DEFAULT 'dine-in', -- 'dine-in', 'takeaway', 'delivery'
  `outlet_code` varchar(30) DEFAULT 'MAIN',             -- Multi-outlet support
  `table_id` bigint(20) DEFAULT NULL,
  `table_label` varchar(30) DEFAULT NULL,
  `customer_name` varchar(120) DEFAULT NULL,
  `customer_phone` varchar(30) DEFAULT NULL,
  `delivery_address` text DEFAULT NULL,
  
  -- Staff Role-based User ID Tracking (Team members with client_id = user_id)
  `created_by_user_id` bigint(20) DEFAULT NULL,        -- Waiter/Captain who punched the order
  `waiter_id` bigint(20) DEFAULT NULL,                 -- Assigned Captain/Waiter
  `cashier_id` bigint(20) DEFAULT NULL,                -- Cashier who settled payment
  `delivery_boy_id` bigint(20) DEFAULT NULL,           -- Rider assigned to delivery
  `approved_by_user_id` bigint(20) DEFAULT NULL,       -- Admin/Manager who approved discount/cancellation
  `updated_by_user_id` bigint(20) DEFAULT NULL,        -- Staff member who last edited order
  
  -- Delivery Tracking
  `delivery_status` varchar(30) DEFAULT 'unassigned',   -- 'unassigned', 'assigned', 'out_for_delivery', 'delivered', 'failed'
  `delivery_assigned_at` datetime DEFAULT NULL,
  `delivered_at` datetime DEFAULT NULL,
  
  -- Pricing & Status
  `discount_type` varchar(20) DEFAULT 'pct',
  `discount_val` double DEFAULT 0,
  `status` varchar(30) DEFAULT 'open',                 -- 'open', 'completed', 'cancelled'
  `reservation_id` bigint(20) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_pos_order_user` (`user_id`),
  KEY `FK_pos_order_creator` (`created_by_user_id`),
  KEY `FK_pos_order_waiter` (`waiter_id`),
  KEY `FK_pos_order_cashier` (`cashier_id`),
  KEY `FK_pos_order_delivery` (`delivery_boy_id`),
  KEY `FK_pos_order_approver` (`approved_by_user_id`),
  KEY `IDX_pos_order_outlet_status` (`outlet_code`, `status`),
  CONSTRAINT `FK_pos_order_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `FK_pos_order_creator` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL,
  CONSTRAINT `FK_pos_order_waiter` FOREIGN KEY (`waiter_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL,
  CONSTRAINT `FK_pos_order_cashier` FOREIGN KEY (`cashier_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL,
  CONSTRAINT `FK_pos_order_delivery` FOREIGN KEY (`delivery_boy_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL,
  CONSTRAINT `FK_pos_order_approver` FOREIGN KEY (`approved_by_user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pos_order_item`
--
DROP TABLE IF EXISTS `pos_order_item`;
CREATE TABLE `pos_order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,                       -- Admin/Owner account
  `order_id` bigint(20) NOT NULL,
  `item_id` bigint(20) DEFAULT NULL,
  `item_key` varchar(80) NOT NULL,
  `item_name` varchar(150) NOT NULL,
  `unit_mode` varchar(20) DEFAULT 'portion',           -- 'portion' (plate) or 'kg' (bulk weight)
  `weight_kg` double DEFAULT NULL,                     -- Weight in kg if unit_mode = 'kg'
  `variant_label` varchar(150) DEFAULT NULL,           -- e.g. 'Half', 'Full • Extra Cheese'
  `price` double NOT NULL DEFAULT 0,
  `qty` int(11) NOT NULL DEFAULT 0,
  `sent_qty` int(11) NOT NULL DEFAULT 0,
  `kot_id` bigint(20) DEFAULT NULL,
  `item_status` varchar(30) DEFAULT 'new',             -- 'new', 'sent', 'preparing', 'served'
  `note` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_pos_order_item_user` (`user_id`),
  KEY `FK_pos_order_item_order` (`order_id`),
  CONSTRAINT `FK_pos_order_item_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `FK_pos_order_item_order` FOREIGN KEY (`order_id`) REFERENCES `pos_order` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pos_order_activity`
--
DROP TABLE IF EXISTS `pos_order_activity`;
CREATE TABLE `pos_order_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,                   -- User who executed the action
  `order_id` bigint(20) NOT NULL,
  `role_name` varchar(40) DEFAULT NULL,
  `action` varchar(50) NOT NULL,                       -- 'created', 'kot_sent', 'delivery_assigned', 'out_for_delivery', 'delivered', 'settled', 'cancelled'
  `remarks` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_pos_activity_order` (`order_id`),
  KEY `FK_pos_activity_user` (`user_id`),
  CONSTRAINT `FK_pos_activity_order` FOREIGN KEY (`order_id`) REFERENCES `pos_order` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_pos_activity_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pos_kot`
--
DROP TABLE IF EXISTS `pos_kot`;
CREATE TABLE `pos_kot` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,                       -- Admin/Owner account
  `kot_code` varchar(40) NOT NULL,
  `order_id` bigint(20) DEFAULT NULL,
  `order_code` varchar(40) DEFAULT NULL,
  `table_label` varchar(40) DEFAULT NULL,
  `order_type` varchar(30) DEFAULT 'dine-in',
  `station` varchar(60) DEFAULT 'Kitchen',
  `status` varchar(30) DEFAULT 'new',                 -- 'new', 'preparing', 'ready', 'served', 'cancelled'
  `created_by_user_id` bigint(20) DEFAULT NULL,        -- Staff member who punched KOT
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_pos_kot_user` (`user_id`),
  KEY `FK_pos_kot_creator` (`created_by_user_id`),
  CONSTRAINT `FK_pos_kot_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `FK_pos_kot_creator` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pos_kot_item`
--
DROP TABLE IF EXISTS `pos_kot_item`;
CREATE TABLE `pos_kot_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,                       -- Admin/Owner account
  `kot_id` bigint(20) NOT NULL,
  `item_name` varchar(150) NOT NULL,
  `unit_mode` varchar(20) DEFAULT 'portion',
  `weight_kg` double DEFAULT NULL,
  `variant_label` varchar(150) DEFAULT NULL,
  `qty` int(11) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  KEY `FK_pos_kot_item_user` (`user_id`),
  KEY `FK_pos_kot_item_kot` (`kot_id`),
  CONSTRAINT `FK_pos_kot_item_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `FK_pos_kot_item_kot` FOREIGN KEY (`kot_id`) REFERENCES `pos_kot` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pos_invoice`
--
DROP TABLE IF EXISTS `pos_invoice`;
CREATE TABLE `pos_invoice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,                       -- Admin/Owner account
  `invoice_code` varchar(40) NOT NULL,
  `outlet_code` varchar(30) DEFAULT 'MAIN',
  `order_id` bigint(20) DEFAULT NULL,
  `order_code` varchar(40) DEFAULT NULL,
  `order_type` varchar(30) DEFAULT 'dine-in',
  `table_label` varchar(40) DEFAULT NULL,
  `customer_name` varchar(120) DEFAULT NULL,
  `customer_phone` varchar(30) DEFAULT NULL,
  `subtotal` double DEFAULT 0,
  `discount` double DEFAULT 0,
  `cgst` double DEFAULT 0,
  `sgst` double DEFAULT 0,
  `total` double DEFAULT 0,
  `status` varchar(30) DEFAULT 'unpaid',
  `payment_mode` varchar(40) DEFAULT NULL,
  `cashier_id` bigint(20) DEFAULT NULL,                -- Cashier who collected payment
  `created_by_user_id` bigint(20) DEFAULT NULL,        -- Staff who generated invoice
  `items_json` longtext DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_pos_invoice_user` (`user_id`),
  KEY `FK_pos_invoice_cashier` (`cashier_id`),
  KEY `FK_pos_invoice_creator` (`created_by_user_id`),
  CONSTRAINT `FK_pos_invoice_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `FK_pos_invoice_cashier` FOREIGN KEY (`cashier_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL,
  CONSTRAINT `FK_pos_invoice_creator` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pos_reservation`
--
DROP TABLE IF EXISTS `pos_reservation`;
CREATE TABLE `pos_reservation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,                       -- Admin/Owner account
  `res_code` varchar(40) NOT NULL,
  `guest_name` varchar(120) NOT NULL,
  `phone` varchar(30) NOT NULL,
  `pax` int(11) NOT NULL DEFAULT 2,
  `duration` int(11) DEFAULT 90,
  `res_date` varchar(20) NOT NULL,
  `res_time` varchar(20) NOT NULL,
  `floor_id` bigint(20) DEFAULT NULL,
  `table_id` bigint(20) DEFAULT NULL,
  `notes` varchar(300) DEFAULT NULL,
  `status` varchar(30) DEFAULT 'upcoming',
  `order_id` bigint(20) DEFAULT NULL,
  `created_by_user_id` bigint(20) DEFAULT NULL,        -- Staff who booked reservation
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_pos_res_user` (`user_id`),
  KEY `FK_pos_res_creator` (`created_by_user_id`),
  CONSTRAINT `FK_pos_res_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `FK_pos_res_creator` FOREIGN KEY (`created_by_user_id`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Table structure for table `pos_tax`
--
DROP TABLE IF EXISTS `pos_tax`;
CREATE TABLE `pos_tax` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,                       -- Admin/Owner account
  `tax_name` varchar(100) NOT NULL,
  `percentage` double NOT NULL DEFAULT 0,
  `status` varchar(30) DEFAULT 'active',
  `created_by_user_id` bigint(20) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `FK_pos_tax_user` (`user_id`),
  CONSTRAINT `FK_pos_tax_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

LOCK TABLES `pos_tax` WRITE;
INSERT INTO `pos_tax` (`id`, `user_id`, `tax_name`, `percentage`, `status`, `created_by_user_id`) VALUES
(1, 1, 'CGST', 2.5, 'active', 1),
(2, 1, 'SGST', 2.5, 'active', 1);
UNLOCK TABLES;

SET FOREIGN_KEY_CHECKS=1;
