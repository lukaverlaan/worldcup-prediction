-- MySQL dump 10.13  Distrib 9.6.0, for macos15.7 (arm64)
--
-- Host: acela.proxy.rlwy.net    Database: railway
-- ------------------------------------------------------
-- Server version	9.4.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `matches`
--

DROP TABLE IF EXISTS `matches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `matches` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `checksum` int DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `date_time` datetime(6) NOT NULL,
  `official_scorea` int DEFAULT NULL,
  `official_scoreb` int DEFAULT NULL,
  `stadium` varchar(255) DEFAULT NULL,
  `stadium_code` varchar(4) DEFAULT NULL,
  `teama` varchar(255) NOT NULL,
  `teamb` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `matches`
--

LOCK TABLES `matches` WRITE;
/*!40000 ALTER TABLE `matches` DISABLE KEYS */;
INSERT INTO `matches` VALUES (6,46,'Mexico City','2026-06-11 21:00:00.000000',NULL,NULL,'Estadio Azteca','4702','Mexico','South Africa'),(7,32,'Zapopan','2026-06-12 04:00:00.000000',NULL,NULL,'Estadio Akron','1002','South Korea','Czech Republic'),(8,31,'Toronto','2026-06-12 21:00:00.000000',NULL,NULL,'BMO Field','1001','Canada','Bosnia and Herzegovina'),(9,78,'Los Angeles','2026-06-13 03:00:00.000000',NULL,NULL,'SoFi Stadium','2891','USA','Paraguay'),(10,36,'Santa Clara','2026-06-13 21:00:00.000000',NULL,NULL,'Levi\'s Stadium','1006','Qatar','Switzerland'),(11,38,'New Jersey','2026-06-14 00:00:00.000000',NULL,NULL,'MetLife Stadium','3142','Brazil','Morocco'),(12,33,'Foxborough','2026-06-14 03:00:00.000000',NULL,NULL,'Gillette Stadium','1003','Haiti','Scotland'),(13,34,'Vancouver','2026-06-14 06:00:00.000000',NULL,NULL,'BC Place','1004','Australia','Turkey'),(14,35,'Houston','2026-06-14 19:00:00.000000',NULL,NULL,'NRG Stadium','1005','Germany','Curaçao'),(15,60,'Arlington','2026-06-14 22:00:00.000000',NULL,NULL,'AT&T Stadium','6074','Netherlands','Japan'),(16,37,'Philadelphia','2026-06-15 01:00:00.000000',NULL,NULL,'Lincoln Financial Field','1007','Ivory Coast','Ecuador'),(17,38,'Guadalupe','2026-06-15 04:00:00.000000',NULL,NULL,'Estadio BBVA','1008','Sweden','Tunisia'),(18,39,'Atlanta','2026-06-15 18:00:00.000000',NULL,NULL,'Mercedes-Benz Stadium','1009','Spain','Cape Verde'),(19,40,'Seattle','2026-06-15 21:00:00.000000',NULL,NULL,'Lumen Field','1010','Belgium','Egypt'),(20,89,'Miami','2026-06-16 00:00:00.000000',NULL,NULL,'Hard Rock Stadium','5230','Saudi Arabia','Uruguay'),(21,78,'Los Angeles','2026-06-16 03:00:00.000000',NULL,NULL,'SoFi Stadium','2891','Iran','New Zealand'),(22,38,'New Jersey','2026-06-16 21:00:00.000000',NULL,NULL,'MetLife Stadium','3142','France','Senegal'),(23,33,'Foxborough','2026-06-17 00:00:00.000000',NULL,NULL,'Gillette Stadium','1003','Iraq','Norway'),(24,41,'Kansas City','2026-06-17 03:00:00.000000',NULL,NULL,'Arrowhead Stadium','1011','Argentina','Algeria'),(25,36,'Santa Clara','2026-06-17 06:00:00.000000',NULL,NULL,'Levi\'s Stadium','1006','Austria','Jordan'),(26,35,'Houston','2026-06-17 19:00:00.000000',NULL,NULL,'NRG Stadium','1005','Portugal','DR Congo'),(27,60,'Arlington','2026-06-17 22:00:00.000000',NULL,NULL,'AT&T Stadium','6074','England','Croatia'),(28,31,'Toronto','2026-06-18 01:00:00.000000',NULL,NULL,'BMO Field','1001','Ghana','Panama'),(29,46,'Mexico City','2026-06-18 04:00:00.000000',NULL,NULL,'Estadio Azteca','4702','Uzbekistan','Colombia'),(30,39,'Atlanta','2026-06-18 18:00:00.000000',NULL,NULL,'Mercedes-Benz Stadium','1009','Czech Republic','South Africa'),(31,78,'Los Angeles','2026-06-18 21:00:00.000000',NULL,NULL,'SoFi Stadium','2891','Switzerland','Bosnia and Herzegovina'),(32,34,'Vancouver','2026-06-19 00:00:00.000000',NULL,NULL,'BC Place','1004','Canada','Qatar'),(33,32,'Zapopan','2026-06-19 03:00:00.000000',NULL,NULL,'Estadio Akron','1002','Mexico','South Korea'),(34,40,'Seattle','2026-06-19 21:00:00.000000',NULL,NULL,'Lumen Field','1010','USA','Australia'),(35,33,'Foxborough','2026-06-20 00:00:00.000000',NULL,NULL,'Gillette Stadium','1003','Scotland','Morocco'),(36,37,'Philadelphia','2026-06-20 02:30:00.000000',NULL,NULL,'Lincoln Financial Field','1007','Brazil','Haiti'),(37,36,'Santa Clara','2026-06-20 05:00:00.000000',NULL,NULL,'Levi\'s Stadium','1006','Turkey','Paraguay'),(38,35,'Houston','2026-06-20 19:00:00.000000',NULL,NULL,'NRG Stadium','1005','Netherlands','Sweden'),(39,31,'Toronto','2026-06-20 22:00:00.000000',NULL,NULL,'BMO Field','1001','Germany','Ivory Coast'),(40,41,'Kansas City','2026-06-21 02:00:00.000000',NULL,NULL,'Arrowhead Stadium','1011','Ecuador','Curaçao'),(41,38,'Guadalupe','2026-06-21 06:00:00.000000',NULL,NULL,'Estadio BBVA','1008','Tunisia','Japan'),(42,39,'Atlanta','2026-06-21 18:00:00.000000',NULL,NULL,'Mercedes-Benz Stadium','1009','Spain','Saudi Arabia'),(43,78,'Los Angeles','2026-06-21 21:00:00.000000',NULL,NULL,'SoFi Stadium','2891','Belgium','Iran'),(44,89,'Miami','2026-06-22 00:00:00.000000',NULL,NULL,'Hard Rock Stadium','5230','Uruguay','Cape Verde'),(45,34,'Vancouver','2026-06-22 03:00:00.000000',NULL,NULL,'BC Place','1004','New Zealand','Egypt'),(46,60,'Arlington','2026-06-22 19:00:00.000000',NULL,NULL,'AT&T Stadium','6074','Argentina','Austria'),(47,37,'Philadelphia','2026-06-22 23:00:00.000000',NULL,NULL,'Lincoln Financial Field','1007','France','Iraq'),(48,31,'Toronto','2026-06-23 02:00:00.000000',NULL,NULL,'BMO Field','1001','Norway','Senegal'),(49,36,'Santa Clara','2026-06-23 05:00:00.000000',NULL,NULL,'Levi\'s Stadium','1006','Jordan','Algeria'),(50,35,'Houston','2026-06-23 19:00:00.000000',NULL,NULL,'NRG Stadium','1005','Portugal','Uzbekistan'),(51,33,'Foxborough','2026-06-23 22:00:00.000000',NULL,NULL,'Gillette Stadium','1003','England','Ghana'),(52,33,'Foxborough','2026-06-24 01:00:00.000000',NULL,NULL,'Gillette Stadium','1003','Panama','Croatia'),(53,32,'Zapopan','2026-06-24 04:00:00.000000',NULL,NULL,'Estadio Akron','1002','Colombia','DR Congo'),(54,34,'Vancouver','2026-06-24 21:00:00.000000',NULL,NULL,'BC Place','1004','Switzerland','Canada'),(55,40,'Seattle','2026-06-24 21:00:00.000000',NULL,NULL,'Lumen Field','1010','Bosnia and Herzegovina','Qatar'),(56,39,'Atlanta','2026-06-25 00:00:00.000000',NULL,NULL,'Mercedes-Benz Stadium','1009','Morocco','Haiti'),(57,89,'Miami','2026-06-25 00:00:00.000000',NULL,NULL,'Hard Rock Stadium','5230','Scotland','Brazil'),(58,38,'Guadalupe','2026-06-25 03:00:00.000000',NULL,NULL,'Estadio BBVA','1008','South Africa','South Korea'),(59,46,'Mexico City','2026-06-25 03:00:00.000000',NULL,NULL,'Estadio Azteca','4702','Czech Republic','Mexico'),(60,37,'Philadelphia','2026-06-25 22:00:00.000000',NULL,NULL,'Lincoln Financial Field','1007','Curaçao','Ivory Coast'),(61,38,'New Jersey','2026-06-25 22:00:00.000000',NULL,NULL,'MetLife Stadium','3142','Ecuador','Germany'),(62,41,'Kansas City','2026-06-26 01:00:00.000000',NULL,NULL,'Arrowhead Stadium','1011','Tunisia','Netherlands'),(63,60,'Arlington','2026-06-26 01:00:00.000000',NULL,NULL,'AT&T Stadium','6074','Japan','Sweden'),(64,78,'Los Angeles','2026-06-26 04:00:00.000000',NULL,NULL,'SoFi Stadium','2891','Turkey','USA'),(65,36,'Santa Clara','2026-06-26 04:00:00.000000',NULL,NULL,'Levi\'s Stadium','1006','Paraguay','Australia'),(66,33,'Foxborough','2026-06-26 21:00:00.000000',NULL,NULL,'Gillette Stadium','1003','Norway','France'),(67,31,'Toronto','2026-06-26 21:00:00.000000',NULL,NULL,'BMO Field','1001','Senegal','Iraq'),(68,35,'Houston','2026-06-27 02:00:00.000000',NULL,NULL,'NRG Stadium','1005','Cape Verde','Saudi Arabia'),(69,32,'Zapopan','2026-06-27 02:00:00.000000',NULL,NULL,'Estadio Akron','1002','Uruguay','Spain'),(70,34,'Vancouver','2026-06-27 05:00:00.000000',NULL,NULL,'BC Place','1004','New Zealand','Belgium'),(71,40,'Seattle','2026-06-27 05:00:00.000000',NULL,NULL,'Lumen Field','1010','Egypt','Iran'),(72,38,'New Jersey','2026-06-27 23:00:00.000000',NULL,NULL,'MetLife Stadium','3142','Panama','England'),(73,37,'Philadelphia','2026-06-27 23:00:00.000000',NULL,NULL,'Lincoln Financial Field','1007','Croatia','Ghana'),(74,89,'Miami','2026-06-28 01:30:00.000000',NULL,NULL,'Hard Rock Stadium','5230','Colombia','Portugal'),(75,39,'Atlanta','2026-06-28 01:30:00.000000',NULL,NULL,'Mercedes-Benz Stadium','1009','DR Congo','Uzbekistan'),(76,41,'Kansas City','2026-06-28 04:00:00.000000',NULL,NULL,'Arrowhead Stadium','1011','Algeria','Austria'),(77,60,'Arlington','2026-06-28 04:00:00.000000',NULL,NULL,'AT&T Stadium','6074','Jordan','Argentina');
/*!40000 ALTER TABLE `matches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `predictions`
--

DROP TABLE IF EXISTS `predictions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `predictions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `points` int DEFAULT NULL,
  `predicted_scorea` int NOT NULL,
  `predicted_scoreb` int NOT NULL,
  `match_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKi3q1q52x1vf4qukjb7laoth4k` (`user_id`,`match_id`),
  KEY `FK5gyk6l61eh61hmb9u1mr6hd7v` (`match_id`),
  CONSTRAINT `FK5ehjwkl57ibsn56fjmwj892ju` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK5gyk6l61eh61hmb9u1mr6hd7v` FOREIGN KEY (`match_id`) REFERENCES `matches` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `predictions`
--

LOCK TABLES `predictions` WRITE;
/*!40000 ALTER TABLE `predictions` DISABLE KEYS */;
INSERT INTO `predictions` VALUES (1,NULL,2,0,6,3),(2,NULL,2,1,7,3),(3,NULL,3,0,6,5),(4,NULL,3,0,6,4),(5,NULL,2,1,7,4),(6,NULL,2,0,8,4),(7,NULL,2,0,6,8),(8,NULL,2,1,9,4),(9,NULL,0,2,10,4),(10,NULL,2,1,11,4),(11,NULL,0,3,12,4),(12,NULL,1,2,13,4),(13,NULL,6,0,14,4),(14,NULL,1,1,15,4),(15,NULL,1,2,16,4),(16,NULL,2,0,17,4),(17,NULL,3,1,6,2),(18,NULL,2,1,7,2),(19,NULL,3,0,18,4),(20,NULL,1,1,8,2),(21,NULL,3,1,9,2),(22,NULL,2,1,19,4),(23,NULL,0,3,10,2),(24,NULL,2,2,11,2),(25,NULL,0,2,12,2),(26,NULL,0,2,20,4),(27,NULL,1,3,13,2),(28,NULL,3,0,21,4),(29,NULL,5,0,14,2),(30,NULL,1,2,15,2),(31,NULL,2,2,16,2),(32,NULL,3,1,6,7),(33,NULL,1,1,7,7),(34,NULL,2,0,8,7),(35,NULL,3,0,9,7),(36,NULL,0,4,10,7),(37,NULL,2,2,11,7),(38,NULL,1,1,12,7),(39,NULL,0,2,13,7),(40,NULL,5,0,14,7),(41,NULL,1,2,15,7),(42,NULL,2,1,22,4),(43,NULL,0,3,23,4),(44,NULL,2,0,24,4),(45,NULL,3,0,25,4),(46,NULL,3,0,26,4),(47,NULL,2,0,27,4),(48,NULL,0,1,28,4),(49,NULL,0,2,29,4),(50,NULL,2,0,30,4),(51,NULL,3,0,31,4),(52,NULL,3,0,32,4),(53,NULL,2,1,33,4),(54,NULL,2,0,34,4),(55,NULL,0,2,35,4),(56,NULL,5,0,36,4),(57,NULL,2,1,37,4),(58,NULL,2,0,38,4),(59,NULL,3,1,39,4),(60,NULL,4,0,40,4),(61,NULL,0,2,41,4),(62,NULL,3,0,42,4),(63,NULL,3,0,43,4),(64,NULL,2,0,44,4),(65,NULL,0,3,45,4),(66,NULL,2,0,46,4),(67,NULL,4,0,47,4),(68,NULL,1,0,48,4),(69,NULL,0,1,49,4),(70,NULL,4,0,50,4),(71,NULL,3,0,51,4),(72,NULL,0,2,52,4),(73,NULL,2,0,53,4),(74,NULL,1,0,54,4),(75,NULL,1,1,55,4),(76,NULL,3,0,56,4),(77,NULL,0,2,57,4),(78,NULL,0,2,58,4),(79,NULL,0,2,59,4),(80,NULL,0,3,60,4),(81,NULL,0,2,61,4),(82,NULL,0,2,62,4),(83,NULL,1,1,63,4),(84,NULL,1,1,64,4),(85,NULL,1,1,65,4),(86,NULL,1,2,66,4),(87,NULL,2,0,67,4),(88,NULL,1,2,69,4),(89,NULL,1,2,68,4),(90,NULL,0,4,70,4),(91,NULL,1,0,71,4),(92,NULL,0,4,72,4),(93,NULL,2,0,73,4),(94,NULL,1,2,74,4),(95,NULL,1,1,75,4),(96,NULL,1,0,76,4),(97,NULL,0,3,77,4);
/*!40000 ALTER TABLE `predictions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team_members`
--

DROP TABLE IF EXISTS `team_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `team_members` (
  `team_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`team_id`,`user_id`),
  KEY `FKee8x7x5026imwmma9kndkxs36` (`user_id`),
  CONSTRAINT `FKee8x7x5026imwmma9kndkxs36` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKtgca08el3ofisywcf11f0f76t` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team_members`
--

LOCK TABLES `team_members` WRITE;
/*!40000 ALTER TABLE `team_members` DISABLE KEYS */;
INSERT INTO `team_members` VALUES (1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(2,11);
/*!40000 ALTER TABLE `team_members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teams`
--

DROP TABLE IF EXISTS `teams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teams` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `invite_code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `owner_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKa510no6sjwqcx153yd5sm4jrr` (`name`),
  KEY `FKde03in0noals71lom04bmfgit` (`owner_id`),
  CONSTRAINT `FKde03in0noals71lom04bmfgit` FOREIGN KEY (`owner_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teams`
--

LOCK TABLES `teams` WRITE;
/*!40000 ALTER TABLE `teams` DISABLE KEYS */;
INSERT INTO `teams` VALUES (1,'AB9D4F6B','BEP',2),(2,'7BB7A322','olyx',11);
/*!40000 ALTER TABLE `teams` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` bigint NOT NULL,
  `role` varchar(255) DEFAULT NULL,
  KEY `FKhfh9dx7w3ubf1co1vdev94g3f` (`user_id`),
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (1,'ADMIN'),(2,'USER'),(3,'USER'),(4,'USER'),(5,'USER'),(6,'USER'),(7,'USER'),(8,'USER'),(9,'USER'),(10,'USER'),(11,'USER'),(12,'USER');
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin@worldcup.be','$2a$10$F563cFZRFIZAnD6KHxJnKun39GZHiz5INxZ4pqHsnPsmAu61H3FJ.','admin'),(2,'thibovandekerkhove.thibo@gmail.com','$2a$10$w1YC9jGoEVFg5HHCUHXhNun2q89CLdzi3Ybav9yzag6FbP1Z5U1Hq','tvandekerkhove'),(3,'matteo.dutoict@gmail.com','$2a$10$eCvADKYkAVEVrDDS2m/Ru.hBAeM7MKJwVfH.PztiLEiM2jr6LdXmu','Matteo'),(4,'jochenvanoverstraeten402@gmail.com','$2a$10$oZDOeq8YwBAtgb1KtdoV9uu4miWL1216la81aqVDBQg5YyRZbZyEm','JocbabaTTV'),(5,'verlaanluka@gmail.com','$2a$10$sfZOvETgVOImsPwCS5vaSO3GN1Ohjm3LAeUBPrCr/l2gitqMXBysm','lukavln'),(6,'mathias.demets@gmail.com','$2a$10$lVclC18qab4P/gr.c2Kcnu2iB9GH4P7HY43/tt5eixgBJdhkrKJBq','mathias_demets'),(7,'aaronvn80@gmail.com','$2a$10$EVNadAFOmwyHEGCuknITzOmOlyq0JhsCeHnTXKLMS9VLZtpQ2Kmwa','Ahroen'),(8,'jarnipvp@gmail.com','$2a$10$B8CCDsy3gGcSpMAUNrSjwevY3n.PEyAn4YwxtSzFJBuMgsZYAL23a','Jarni'),(9,'emmelien.schiettekatte@gmail.com','$2a$10$QEFHAQZ3dj8D0noToOd35.XickdRbZzS59L5FUNXnUKuRgUsfo3K.','emms'),(10,'test@gmail.com','$2a$10$xd2sOQWKemDCRYgXSKhOxe4NtrzDdxKw0zoSZuSy1s1ndxRYv2mju','testuser'),(11,'hamidboulaajoul@outlook.com','$2a$10$3JMDZ2MiWs1RM4Fn/MifW.MEJ9XqJgNpAdWM9RYarBGFCIVEXIwF6','hamid'),(12,'loprete_matteo15@hotmail.com','$2a$10$XcllFZ4ZNmxNw3QFwn.dVegILv2RcLrIeTRP8816rJ1R0LQH/xse.','Iciparisxl');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'railway'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-07  8:51:58
