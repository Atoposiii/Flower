-- MySQL dump 10.13  Distrib 8.4.8, for macos15.7 (arm64)
--
-- Host: localhost    Database: flower_db
-- ------------------------------------------------------
-- Server version	8.4.8

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
-- Table structure for table `activity_registration`
--

DROP TABLE IF EXISTS `activity_registration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_registration` (
  `id` int NOT NULL AUTO_INCREMENT,
  `activity_id` int NOT NULL,
  `volunteer_id` int NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'registered',
  `registration_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `confirm_time` datetime DEFAULT NULL,
  `actual_hours` int DEFAULT NULL,
  `feedback` text,
  `rating` int DEFAULT NULL,
  `is_member_joined` bit(1) DEFAULT NULL,
  `sign_in_status` varchar(20) DEFAULT NULL,
  `sign_in_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_volunteer` (`activity_id`,`volunteer_id`),
  KEY `idx_volunteer_id` (`volunteer_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `activity_registration_ibfk_1` FOREIGN KEY (`activity_id`) REFERENCES `volunteer_activity` (`id`) ON DELETE CASCADE,
  CONSTRAINT `activity_registration_ibfk_2` FOREIGN KEY (`volunteer_id`) REFERENCES `volunteer` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='活动报名表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_registration`
--

LOCK TABLES `activity_registration` WRITE;
/*!40000 ALTER TABLE `activity_registration` DISABLE KEYS */;
INSERT INTO `activity_registration` VALUES (1,5,2,'registered','2026-03-30 22:52:48',NULL,22,NULL,NULL,_binary '\0','signed','2026-03-30 22:58:00.989000'),(2,4,2,'completed','2026-03-30 22:52:51','2026-03-30 22:58:24',22,NULL,NULL,_binary '\0','signed','2026-03-30 22:57:59.761000');
/*!40000 ALTER TABLE `activity_registration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `user_id` int NOT NULL,
  `parent_id` int DEFAULT NULL,
  `content` text NOT NULL,
  `like_count` int DEFAULT '0',
  `status` varchar(20) NOT NULL DEFAULT 'published',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `reply_count` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`),
  CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE,
  CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,1,2,NULL,'dsa',1,'published','2026-03-29 00:02:35','2026-03-29 00:02:46',1),(2,1,2,NULL,'asd',1,'published','2026-03-29 00:02:38','2026-03-29 00:02:40',0),(3,1,3,NULL,'大叔的',0,'published','2026-03-29 00:21:09','2026-03-29 00:21:08',0),(4,1,3,NULL,'不养了',0,'published','2026-03-29 01:45:49','2026-03-29 01:45:48',0),(5,2,4,NULL,'top',1,'published','2026-03-30 22:13:25','2026-03-30 22:13:56',1);
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment_like`
--

DROP TABLE IF EXISTS `comment_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_like` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) DEFAULT NULL,
  `comment_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKo8urnlomri10dub10y8ket23c` (`comment_id`,`user_id`),
  KEY `FKl5wrmp8eoy5uegdo3473jqqi` (`user_id`),
  CONSTRAINT `FKl5wrmp8eoy5uegdo3473jqqi` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKqlv8phl1ibeh0efv4dbn3720p` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_like`
--

LOCK TABLES `comment_like` WRITE;
/*!40000 ALTER TABLE `comment_like` DISABLE KEYS */;
INSERT INTO `comment_like` VALUES (1,'2026-03-29 00:02:39.538000',1,2),(2,'2026-03-29 00:02:40.511000',2,2),(3,'2026-03-30 22:13:49.225000',5,2);
/*!40000 ALTER TABLE `comment_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment_reply`
--

DROP TABLE IF EXISTS `comment_reply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_reply` (
  `id` int NOT NULL AUTO_INCREMENT,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `like_count` int NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `comment_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjq5b6rqpn30c58kqrbv7k3ocs` (`comment_id`),
  KEY `FKnwcnav7kbsfgabljwaoxtcia` (`user_id`),
  CONSTRAINT `FKjq5b6rqpn30c58kqrbv7k3ocs` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`),
  CONSTRAINT `FKnwcnav7kbsfgabljwaoxtcia` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_reply`
--

LOCK TABLES `comment_reply` WRITE;
/*!40000 ALTER TABLE `comment_reply` DISABLE KEYS */;
INSERT INTO `comment_reply` VALUES (1,'dsf','2026-03-29 00:02:46.308000',0,'published',1,2),(2,'私我','2026-03-30 22:13:56.487000',0,'published',5,2);
/*!40000 ALTER TABLE `comment_reply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consultation_message`
--

DROP TABLE IF EXISTS `consultation_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consultation_message` (
  `id` int NOT NULL AUTO_INCREMENT,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `sender_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sender_id` int NOT NULL,
  `session_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6qjo1x4t2oxemt1u9fbchxc0w` (`sender_id`),
  KEY `FKojbg2vawvd7kcu6wysx9bliad` (`session_id`),
  CONSTRAINT `FK6qjo1x4t2oxemt1u9fbchxc0w` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKojbg2vawvd7kcu6wysx9bliad` FOREIGN KEY (`session_id`) REFERENCES `consultation_session` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consultation_message`
--

LOCK TABLES `consultation_message` WRITE;
/*!40000 ALTER TABLE `consultation_message` DISABLE KEYS */;
INSERT INTO `consultation_message` VALUES (1,'hhhh','2026-03-29 00:59:04.893000','user',3,4),(2,'发送了','2026-03-29 00:59:38.156000','user',3,4),(3,'姐姐家','2026-03-29 01:04:09.785000','user',3,4),(4,'你好亲','2026-03-29 01:04:24.935000','admin',1,4),(5,'这个多少钱','2026-03-29 01:04:41.043000','user',3,4),(6,'b','2026-03-29 02:49:20.416000','admin',1,3),(7,'牡丹多少钱','2026-03-29 02:56:01.691000','user',3,7),(8,'80一支','2026-03-29 02:56:12.129000','admin',1,7),(9,'你好','2026-03-30 22:10:32.517000','user',4,8),(10,'亲 有什么事情','2026-03-30 22:10:42.282000','admin',1,8),(11,'这个多少钱','2026-03-30 22:10:49.625000','user',4,8),(12,'100一支','2026-03-30 22:10:54.881000','admin',1,8);
/*!40000 ALTER TABLE `consultation_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consultation_session`
--

DROP TABLE IF EXISTS `consultation_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consultation_session` (
  `id` int NOT NULL AUTO_INCREMENT,
  `channel` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `last_message_time` datetime(6) DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_id` int NOT NULL,
  `flower_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK33vbqmj3hqi5yl77g25gotqwx` (`user_id`),
  CONSTRAINT `FK33vbqmj3hqi5yl77g25gotqwx` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consultation_session`
--

LOCK TABLES `consultation_session` WRITE;
/*!40000 ALTER TABLE `consultation_session` DISABLE KEYS */;
INSERT INTO `consultation_session` VALUES (1,'member','2026-03-28 22:13:49.121000','2026-03-28 22:13:49.121000','open','会员咨询-2026/3/28 22:13:49',2,NULL),(2,'non-member','2026-03-29 00:51:47.974000','2026-03-29 00:51:47.974000','open','test',1,1),(3,'non-member','2026-03-29 00:52:25.244000','2026-03-29 02:49:20.426000','open','test2',1,1),(4,'non-member','2026-03-29 00:58:55.289000','2026-03-29 01:04:41.046000','closed','花卉咨询-1-普通',3,1),(5,'non-member','2026-03-29 01:39:45.185000','2026-03-29 01:39:45.185000','open','花卉咨询-3-普通',3,3),(6,'member','2026-03-29 02:02:50.280000','2026-03-29 02:02:50.280000','open','会员咨询-2026/3/29 02:02:50',3,NULL),(7,'non-member','2026-03-29 02:55:25.543000','2026-03-29 02:56:12.133000','open','花卉咨询-2-普通',3,2),(8,'non-member','2026-03-30 22:10:20.288000','2026-03-30 22:10:54.884000','open','花卉咨询-1-普通',4,1);
/*!40000 ALTER TABLE `consultation_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `error_feedback`
--

DROP TABLE IF EXISTS `error_feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `error_feedback` (
  `id` int NOT NULL AUTO_INCREMENT,
  `admin_reply` text COLLATE utf8mb4_unicode_ci,
  `admin_reply_time` datetime(6) DEFAULT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `feedback_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `target_id` int DEFAULT NULL,
  `target_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `target_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKtoxy260rwoy37rlhn6c5b4y0n` (`user_id`),
  CONSTRAINT `FKtoxy260rwoy37rlhn6c5b4y0n` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `error_feedback`
--

LOCK TABLES `error_feedback` WRITE;
/*!40000 ALTER TABLE `error_feedback` DISABLE KEYS */;
INSERT INTO `error_feedback` VALUES (1,'hhh','2026-03-29 00:39:57.249000','别名错误','2026-03-29 00:22:13.388000','error_report','resolved',NULL,NULL,'flower',3),(2,NULL,NULL,'不叫这个名','2026-03-29 00:39:26.170000','name_error','pending',NULL,'','flower',1),(3,'好的，谢谢纠正','2026-03-30 22:10:17.236000','名字错误了','2026-03-30 22:09:51.192000','name_error','resolved',1,'玫瑰','flower',4);
/*!40000 ALTER TABLE `error_feedback` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flower`
--

DROP TABLE IF EXISTS `flower`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flower` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `scientific_name` varchar(200) DEFAULT NULL,
  `family` varchar(100) DEFAULT NULL,
  `origin` varchar(200) DEFAULT NULL,
  `description` text,
  `growth_habit` text,
  `flowering_period` varchar(100) DEFAULT NULL,
  `color` varchar(200) DEFAULT NULL,
  `uses` text,
  `image_url` varchar(500) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_recommended` tinyint(1) DEFAULT '0',
  `plant_form` varchar(200) DEFAULT NULL,
  `flower_language` text,
  `watering_tips` text,
  `fertilization_tips` text,
  `propagation` text,
  `common_problems` text,
  `is_city_flower` varchar(50) DEFAULT NULL,
  `legend` text,
  `is_edible` varchar(10) DEFAULT NULL,
  `medicinal_value` text,
  `fun_facts` text,
  `aliases` varchar(500) DEFAULT NULL,
  `growth_environment` varchar(200) DEFAULT NULL,
  `plant_type` varchar(50) DEFAULT NULL,
  `plant_height` varchar(100) DEFAULT NULL,
  `stem_features` text,
  `leaf_features` text,
  `flower_features` text,
  `flower_stamen_features` text,
  `fragrance_type` varchar(100) DEFAULT NULL,
  `fruit_seed_features` text,
  `ornamental_features` text,
  `flowering_frequency` varchar(100) DEFAULT NULL,
  `suitable_temperature` varchar(200) DEFAULT NULL,
  `light_requirements` varchar(100) DEFAULT NULL,
  `water_requirements` varchar(100) DEFAULT NULL,
  `soil_preference` varchar(200) DEFAULT NULL,
  `growth_speed` varchar(50) DEFAULT NULL,
  `suitable_season` varchar(100) DEFAULT NULL,
  `planting_method` varchar(50) DEFAULT NULL,
  `pruning_tips` text,
  `pest_control` text,
  `winter_care` text,
  `summer_care` text,
  `color_meanings` text,
  `suitable_recipients` text,
  `suitable_occasions` text,
  `good_companions` text,
  `taboos` text,
  `is_toxic` varchar(100) DEFAULT NULL,
  `air_purification` text,
  `beginner_friendly` varchar(50) DEFAULT NULL,
  `category` varchar(100) DEFAULT NULL,
  `city_flower_of` varchar(200) DEFAULT NULL,
  `cold_tolerance` varchar(100) DEFAULT NULL,
  `common_diseases` text,
  `common_pests` text,
  `crown_width` varchar(100) DEFAULT NULL,
  `cultural_meaning` text,
  `cut_flower_usage` text,
  `difficulty_level` varchar(50) DEFAULT NULL,
  `distribution` varchar(500) DEFAULT NULL,
  `dormancy_period` varchar(200) DEFAULT NULL,
  `edible_value` text,
  `fertilizer_amount` text,
  `fertilizer_period` text,
  `fertilizer_type` text,
  `festival_usage` text,
  `flower_color` varchar(200) DEFAULT NULL,
  `flower_fragrance` varchar(100) DEFAULT NULL,
  `flower_type` varchar(100) DEFAULT NULL,
  `fragrance_value` text,
  `fruit_period` varchar(100) DEFAULT NULL,
  `fruit_type` varchar(100) DEFAULT NULL,
  `genus` varchar(100) DEFAULT NULL,
  `growth_cycle` varchar(200) DEFAULT NULL,
  `growth_rate` varchar(100) DEFAULT NULL,
  `heat_tolerance` varchar(100) DEFAULT NULL,
  `landscape_usage` text,
  `leaf_color` varchar(200) DEFAULT NULL,
  `leaf_shape` varchar(100) DEFAULT NULL,
  `life_cycle` varchar(50) DEFAULT NULL,
  `lifespan` varchar(100) DEFAULT NULL,
  `no_flowering_causes` text,
  `occasion_usage` text,
  `ornamental_type` varchar(100) DEFAULT NULL,
  `ornamental_usage` text,
  `placement_suggestion` text,
  `plant_combinations` text,
  `propagation_methods` text,
  `pruning_method` text,
  `pruning_time` text,
  `purchase_tips` text,
  `repotting_tips` text,
  `root_rot_causes` text,
  `soil_ph` varchar(100) DEFAULT NULL,
  `soil_requirements` text,
  `soil_type` varchar(200) DEFAULT NULL,
  `temperature_requirements` varchar(200) DEFAULT NULL,
  `toxic_to_human` varchar(100) DEFAULT NULL,
  `toxic_to_pet` varchar(100) DEFAULT NULL,
  `watering_frequency` varchar(200) DEFAULT NULL,
  `watering_method` text,
  `yellow_leaf_causes` text,
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_family` (`family`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='花卉知识库表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flower`
--

LOCK TABLES `flower` WRITE;
/*!40000 ALTER TABLE `flower` DISABLE KEYS */;
INSERT INTO `flower` VALUES (1,'玫瑰','Rosa rugosa','蔷薇科',NULL,'玫瑰是世界上最著名的观赏花卉之一，象征着爱情和美丽。',NULL,'4-6月','红、粉、白、黄',NULL,'https://miaobi-lite.bj.bcebos.com/miaobi/5mao/b%275oSf5oGp6IqC6YCC5ZCI5LuA5LmI6IqxXzE3MzI5ODM1MDguOTM0Njk1NQ%3D%3D%27/0.png','2026-03-28 20:54:58','2026-03-29 02:25:16',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,'牡丹','Paeonia suffruticosa','芍药科',NULL,'牡丹为中国十大名花之一，被誉为花中之王。',NULL,'4-5月','红、粉、白、紫',NULL,'https://miaobi-lite.bj.bcebos.com/miaobi/5mao/b%275oSf5oGp6IqC6YCC5ZCI5LuA5LmI6IqxXzE3MzI5ODM1MDguOTM0Njk1NQ%3D%3D%27/0.png','2026-03-28 20:54:58','2026-03-29 02:25:16',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,'菊花','Chrysanthemum morifolium','菊科',NULL,'菊花是中国传统名花，品种繁多，花色丰富。',NULL,'9-11月','黄、白、红、紫',NULL,'https://miaobi-lite.bj.bcebos.com/miaobi/5mao/b%275oSf5oGp6IqC6YCC5ZCI5LuA5LmI6IqxXzE3MzI5ODM1MDguOTM0Njk1NQ%3D%3D%27/0.png','2026-03-28 20:54:58','2026-03-29 02:25:16',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(4,'兰花','Cymbidium','兰科',NULL,'兰花以其高雅的气质和独特的芳香而闻名。',NULL,'四季开花','白、黄、绿、粉',NULL,'https://miaobi-lite.bj.bcebos.com/miaobi/5mao/b%275oSf5oGp6IqC6YCC5ZCI5LuA5LmI6IqxXzE3MzI5ODM1MDguOTM0Njk1NQ%3D%3D%27/0.png','2026-03-28 20:54:58','2026-03-29 02:25:16',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(5,'樱花','Prunus serrulata','蔷薇科',NULL,'樱花是春季最具代表性的花卉，象征着生命的短暂与美丽。',NULL,'3-4月','粉、白',NULL,'https://miaobi-lite.bj.bcebos.com/miaobi/5mao/b%275oSf5oGp6IqC6YCC5ZCI5LuA5LmI6IqxXzE3MzI5ODM1MDguOTM0Njk1NQ%3D%3D%27/0.png','2026-03-28 20:54:58','2026-03-29 02:25:16',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(6,'郁金香6','Tulipa gesneriana','百合科','12','郁金香是世界著名的球根花卉，色彩艳丽。',NULL,'3-5月','红、黄、粉、紫、白',NULL,'https://miaobi-lite.bj.bcebos.com/miaobi/5mao/b%275oSf5oGp6IqC6YCC5ZCI5LuA5LmI6IqxXzE3MzI5ODM1MDguOTM0Njk1NQ%3D%3D%27/0.png','2026-03-28 20:54:58','2026-03-30 22:30:06',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'花',NULL,NULL,NULL,NULL,NULL,'212',NULL,NULL,'2121',NULL,NULL,NULL,NULL,NULL,NULL,'红色',NULL,'12',NULL,'121',NULL,'牛',NULL,NULL,NULL,NULL,'1',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `flower` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flower_collect`
--

DROP TABLE IF EXISTS `flower_collect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flower_collect` (
  `id` int NOT NULL AUTO_INCREMENT,
  `flower_id` int NOT NULL,
  `user_id` int NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_flower_user` (`flower_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `flower_collect_ibfk_1` FOREIGN KEY (`flower_id`) REFERENCES `flower` (`id`) ON DELETE CASCADE,
  CONSTRAINT `flower_collect_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='花卉收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flower_collect`
--

LOCK TABLES `flower_collect` WRITE;
/*!40000 ALTER TABLE `flower_collect` DISABLE KEYS */;
/*!40000 ALTER TABLE `flower_collect` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member_benefit`
--

DROP TABLE IF EXISTS `member_benefit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_benefit` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) DEFAULT NULL,
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `icon` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `link_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sort_order` int DEFAULT NULL,
  `title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_benefit`
--

LOCK TABLES `member_benefit` WRITE;
/*!40000 ALTER TABLE `member_benefit` DISABLE KEYS */;
/*!40000 ALTER TABLE `member_benefit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member_benefit_usage`
--

DROP TABLE IF EXISTS `member_benefit_usage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_benefit_usage` (
  `id` int NOT NULL AUTO_INCREMENT,
  `benefit_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `benefit_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `benefit_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `device_info` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ip_address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `member_record_id` int DEFAULT NULL,
  `usage_details` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usage_time` datetime(6) DEFAULT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_benefit_usage`
--

LOCK TABLES `member_benefit_usage` WRITE;
/*!40000 ALTER TABLE `member_benefit_usage` DISABLE KEYS */;
/*!40000 ALTER TABLE `member_benefit_usage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member_consultation`
--

DROP TABLE IF EXISTS `member_consultation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_consultation` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `consultation_type` varchar(50) NOT NULL,
  `title` varchar(200) NOT NULL,
  `content` text NOT NULL,
  `reply_content` text,
  `reply_time` datetime DEFAULT NULL,
  `replier_name` varchar(50) DEFAULT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'pending',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `member_consultation_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='会员咨询表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_consultation`
--

LOCK TABLES `member_consultation` WRITE;
/*!40000 ALTER TABLE `member_consultation` DISABLE KEYS */;
/*!40000 ALTER TABLE `member_consultation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member_record`
--

DROP TABLE IF EXISTS `member_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_record` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `member_level` varchar(20) NOT NULL DEFAULT 'VIP',
  `member_points` int DEFAULT '0',
  `join_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `expiry_date` datetime DEFAULT NULL,
  `discount_rate` decimal(5,2) DEFAULT '100.00',
  `free_shipping` tinyint(1) DEFAULT '0',
  `priority_service` tinyint(1) DEFAULT '0',
  `status` varchar(20) NOT NULL DEFAULT 'active',
  `consultation_count` int DEFAULT '0',
  `last_consultation_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `auto_renew` bit(1) DEFAULT NULL,
  `cancel_reason` varchar(255) DEFAULT NULL,
  `cancel_time` datetime(6) DEFAULT NULL,
  `last_payment_time` datetime(6) DEFAULT NULL,
  `member_type` varchar(20) DEFAULT NULL,
  `monthly_fee` decimal(10,2) DEFAULT NULL,
  `refund_amount` decimal(10,2) DEFAULT NULL,
  `total_paid` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `idx_member_level` (`member_level`),
  KEY `idx_status` (`status`),
  CONSTRAINT `member_record_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='会员记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_record`
--

LOCK TABLES `member_record` WRITE;
/*!40000 ALTER TABLE `member_record` DISABLE KEYS */;
INSERT INTO `member_record` VALUES (1,2,'yearly',0,'2026-03-28 22:09:07','2027-03-28 22:09:07',100.00,0,0,'active',0,NULL,'2026-03-28 22:08:22','2026-03-28 22:09:06',_binary '\0','学习养花',NULL,NULL,'vip',9.90,NULL,0.00),(2,3,'yearly',0,'2026-03-29 01:43:08','2031-03-29 01:43:08',100.00,0,0,'refunded',0,NULL,'2026-03-29 01:42:46','2026-03-29 02:05:53',_binary '\0','user_request','2026-03-29 02:05:53.504000','2026-03-29 02:05:51.510000','vip',9.90,9.90,39.60),(3,4,'yearly',0,'2026-03-30 22:12:14','2030-03-30 22:12:14',100.00,0,0,'active',0,NULL,'2026-03-30 22:12:06','2026-03-30 22:12:49',_binary '\0','的方式','2026-03-30 22:12:40.405000','2026-03-30 22:12:49.287000','vip',9.90,9.90,29.70);
/*!40000 ALTER TABLE `member_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member_refunds`
--

DROP TABLE IF EXISTS `member_refunds`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_refunds` (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `refund_amount` decimal(10,2) NOT NULL,
  `refund_channel` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `refunded_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_refunds`
--

LOCK TABLES `member_refunds` WRITE;
/*!40000 ALTER TABLE `member_refunds` DISABLE KEYS */;
INSERT INTO `member_refunds` VALUES (1,9,9.90,'alipay','2026-03-29 02:05:53.506000'),(2,11,9.90,'wechat','2026-03-30 22:12:40.407000');
/*!40000 ALTER TABLE `member_refunds` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_record`
--

DROP TABLE IF EXISTS `payment_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_record` (
  `id` int NOT NULL AUTO_INCREMENT,
  `amount` decimal(10,2) NOT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `order_number` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `payment_method` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `payment_status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `payment_time` datetime(6) DEFAULT NULL,
  `refund_amount` decimal(10,2) DEFAULT NULL,
  `refund_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `refund_time` datetime(6) DEFAULT NULL,
  `transaction_id` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6tvlwwmm12gtnmv7dyfx90stb` (`order_number`),
  KEY `FKop9nbvjbq5e79tw6hua9q8se7` (`user_id`),
  CONSTRAINT `FKop9nbvjbq5e79tw6hua9q8se7` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_record`
--

LOCK TABLES `payment_record` WRITE;
/*!40000 ALTER TABLE `payment_record` DISABLE KEYS */;
INSERT INTO `payment_record` VALUES (1,9.90,'2026-03-29 01:51:09.071000','PAY17747202690661580','wechat','pending',NULL,NULL,NULL,NULL,NULL,3),(2,9.90,'2026-03-29 01:51:15.591000','PAY17747202755915792','alipay','pending',NULL,NULL,NULL,NULL,NULL,3),(3,9.90,'2026-03-29 01:51:21.190000','PAY17747202811908390','bank_card','pending',NULL,NULL,NULL,NULL,NULL,3),(4,9.90,'2026-03-29 01:51:33.408000','PAY17747202934080488','alipay','pending',NULL,NULL,NULL,NULL,NULL,3),(5,9.90,'2026-03-29 01:51:56.356000','PAY17747203163562363','alipay','pending',NULL,NULL,NULL,NULL,NULL,3),(6,9.90,'2026-03-29 01:53:41.827000','PAY17747204218275127','alipay','completed','2026-03-29 01:53:50.695000',NULL,NULL,NULL,'TXN1774720430680',3),(7,9.90,'2026-03-29 01:57:40.605000','PAY17747206606051496','wechat','completed','2026-03-29 01:57:41.664000',NULL,NULL,NULL,'TXN1774720661658',3),(8,9.90,'2026-03-29 01:59:23.631000','PAY17747207636305142','wechat','completed','2026-03-29 01:59:24.890000',NULL,NULL,NULL,'TXN1774720764878',3),(9,9.90,'2026-03-29 02:05:50.359000','PAY17747211503522063','alipay','refunded','2026-03-29 02:05:51.504000',9.90,NULL,'2026-03-29 02:05:53.502000','TXN1774721151489',3),(10,9.90,'2026-03-30 22:12:27.725000','PAY17748799477222287','wechat','completed','2026-03-30 22:12:28.607000',NULL,NULL,NULL,'TXN1774879948596',4),(11,9.90,'2026-03-30 22:12:37.229000','PAY17748799572284323','wechat','refunded','2026-03-30 22:12:38.103000',9.90,NULL,'2026-03-30 22:12:40.403000','TXN1774879958095',4),(12,9.90,'2026-03-30 22:12:48.562000','PAY17748799685612879','wechat','completed','2026-03-30 22:12:49.285000',NULL,NULL,NULL,'TXN1774879969275',4);
/*!40000 ALTER TABLE `payment_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post`
--

DROP TABLE IF EXISTS `post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `content` text NOT NULL,
  `user_id` int NOT NULL,
  `view_count` int DEFAULT '0',
  `like_count` int DEFAULT '0',
  `comment_count` int DEFAULT '0',
  `collect_count` int DEFAULT '0',
  `category` varchar(50) DEFAULT NULL,
  `tags` varchar(200) DEFAULT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'published',
  `is_top` tinyint(1) DEFAULT '0',
  `is_essence` tinyint(1) DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `publish_time` datetime DEFAULT NULL,
  `cover_image` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  CONSTRAINT `post_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='帖子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post`
--

LOCK TABLES `post` WRITE;
/*!40000 ALTER TABLE `post` DISABLE KEYS */;
INSERT INTO `post` VALUES (1,'菊花怎么养？','求助',2,0,2,4,0,NULL,NULL,'published',0,0,'2026-03-28 22:23:56','2026-03-29 01:45:48',NULL,NULL),(2,' 玫瑰长什么样子','如题',4,0,0,1,0,NULL,NULL,'published',0,0,'2026-03-30 22:13:19','2026-03-30 22:13:25',NULL,NULL);
/*!40000 ALTER TABLE `post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_collect`
--

DROP TABLE IF EXISTS `post_collect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_collect` (
  `id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `user_id` int NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `post_collect_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE,
  CONSTRAINT `post_collect_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='帖子收藏表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_collect`
--

LOCK TABLES `post_collect` WRITE;
/*!40000 ALTER TABLE `post_collect` DISABLE KEYS */;
/*!40000 ALTER TABLE `post_collect` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_like`
--

DROP TABLE IF EXISTS `post_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_like` (
  `id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `user_id` int NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`,`user_id`),
  UNIQUE KEY `UKpmmko3h7yonaqhy5gxvnmdeue` (`post_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `post_like_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE,
  CONSTRAINT `post_like_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='帖子点赞表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_like`
--

LOCK TABLES `post_like` WRITE;
/*!40000 ALTER TABLE `post_like` DISABLE KEYS */;
INSERT INTO `post_like` VALUES (1,1,2,'2026-03-28 22:24:00'),(3,1,3,'2026-03-28 23:00:59');
/*!40000 ALTER TABLE `post_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reply_like`
--

DROP TABLE IF EXISTS `reply_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reply_like` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) DEFAULT NULL,
  `reply_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKoipnfls7h6j9sijt6eod0002r` (`reply_id`,`user_id`),
  KEY `FKif6ls8fh5u9b694bs5caprrdj` (`user_id`),
  CONSTRAINT `FK9q0nf232v0cwgcsd7y0s87f14` FOREIGN KEY (`reply_id`) REFERENCES `comment_reply` (`id`),
  CONSTRAINT `FKif6ls8fh5u9b694bs5caprrdj` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reply_like`
--

LOCK TABLES `reply_like` WRITE;
/*!40000 ALTER TABLE `reply_like` DISABLE KEYS */;
/*!40000 ALTER TABLE `reply_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sign_in_record`
--

DROP TABLE IF EXISTS `sign_in_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sign_in_record` (
  `id` int NOT NULL AUTO_INCREMENT,
  `hours_earned` int DEFAULT NULL,
  `remarks` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sign_in_time` datetime(6) DEFAULT NULL,
  `sign_out_time` datetime(6) DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `registration_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK23df29l7gmafhrori53b9sd03` (`registration_id`),
  CONSTRAINT `FK23df29l7gmafhrori53b9sd03` FOREIGN KEY (`registration_id`) REFERENCES `activity_registration` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sign_in_record`
--

LOCK TABLES `sign_in_record` WRITE;
/*!40000 ALTER TABLE `sign_in_record` DISABLE KEYS */;
INSERT INTO `sign_in_record` VALUES (1,22,NULL,'2026-03-30 22:57:59.761000',NULL,'completed',2),(2,0,NULL,'2026-03-30 22:58:00.989000',NULL,'signed_in',1);
/*!40000 ALTER TABLE `sign_in_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `nickname` varchar(50) DEFAULT NULL,
  `avatar` varchar(500) DEFAULT NULL,
  `role` varchar(20) NOT NULL DEFAULT 'USER',
  `is_member` tinyint(1) DEFAULT '0',
  `member_expire_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` varchar(20) NOT NULL DEFAULT 'active',
  `bio` varchar(200) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_username` (`username`),
  KEY `idx_role` (`role`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','$2a$10$y8PpinKVs6OGkCS33yTrmeeTkWuNrcEZiqq3vR1n3K3KwT2LEHA9i','admin@flower.com',NULL,'管理员',NULL,'ADMIN',0,NULL,'2026-03-28 21:26:25','2026-03-28 21:26:25','active',NULL,NULL),(2,'user','$2a$10$krC1zWqHp06Ui5h69adqKOnalGVwt5.9IGfKNbX6B.BDzYs0yBC3O','user@flower.com',NULL,'普通用户',NULL,'USER',0,NULL,'2026-03-28 21:26:25','2026-03-28 21:26:25','active',NULL,NULL),(3,'atopos','$2a$10$TRfshOQr.xUwry4.N80Jd.2E6G5n2FziwWx4zh67qnBgshIR9mslm','zhangsan1@163.com',NULL,'张三8',NULL,'USER',0,NULL,'2026-03-28 21:28:59','2026-03-29 02:18:42','active','哈哈哈','male'),(4,'qwe','$2a$10$vOdG5k35JzuKWT5cP4drKebNffT1rswVLWMkFqbXooxeJnbU8BWJq','1234@163.com',NULL,'qwe',NULL,'USER',0,NULL,'2026-03-30 22:08:17','2026-03-30 22:08:41','active','dfsd','male');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `volunteer`
--

DROP TABLE IF EXISTS `volunteer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `volunteer` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `real_name` varchar(50) DEFAULT NULL,
  `id_card` varchar(20) DEFAULT NULL,
  `gender` varchar(10) DEFAULT NULL,
  `age` int DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `skills` varchar(500) DEFAULT NULL,
  `service_hours` int DEFAULT '0',
  `service_count` int DEFAULT '0',
  `status` varchar(20) NOT NULL DEFAULT 'pending',
  `application_time` datetime DEFAULT NULL,
  `approval_time` datetime DEFAULT NULL,
  `last_service_time` datetime DEFAULT NULL,
  `quit_time` datetime DEFAULT NULL,
  `quit_reason` varchar(500) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `total_service_hours` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_service_hours` (`service_hours`),
  CONSTRAINT `volunteer_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='志愿者表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `volunteer`
--

LOCK TABLES `volunteer` WRITE;
/*!40000 ALTER TABLE `volunteer` DISABLE KEYS */;
INSERT INTO `volunteer` VALUES (1,2,'张三','420106199203154321',NULL,NULL,NULL,'打扫卫生',2,0,'approved','2026-03-28 21:58:01','2026-03-28 22:02:11','2026-03-28 22:06:20',NULL,NULL,'2026-03-28 21:58:00','2026-03-28 22:06:20',2),(2,3,NULL,NULL,NULL,NULL,NULL,NULL,26,0,'approved','2026-03-29 01:24:03','2026-03-29 01:25:29','2026-03-30 22:58:24',NULL,NULL,'2026-03-29 01:24:03','2026-03-30 22:58:23',26),(4,4,NULL,NULL,NULL,NULL,NULL,NULL,4,0,'approved','2026-03-30 22:11:12','2026-03-30 22:11:12','2026-03-30 22:11:34',NULL,NULL,'2026-03-30 22:11:11','2026-03-30 22:11:33',4);
/*!40000 ALTER TABLE `volunteer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `volunteer_activity`
--

DROP TABLE IF EXISTS `volunteer_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `volunteer_activity` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `activity_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `location` varchar(200) DEFAULT NULL,
  `max_participants` int DEFAULT NULL,
  `current_participants` int DEFAULT '0',
  `status` varchar(20) NOT NULL DEFAULT 'recruiting',
  `activity_type` varchar(50) DEFAULT NULL,
  `difficulty_level` varchar(20) DEFAULT NULL,
  `required_hours` int DEFAULT NULL,
  `reward_points` int DEFAULT '0',
  `organizer_name` varchar(100) DEFAULT NULL,
  `contact_phone` varchar(20) DEFAULT NULL,
  `contact_email` varchar(100) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_time` datetime(6) DEFAULT NULL,
  `is_allow_late_sign_in` bit(1) DEFAULT NULL,
  `is_member_only` bit(1) DEFAULT NULL,
  `sign_in_end_time` datetime(6) DEFAULT NULL,
  `sign_in_start_time` datetime(6) DEFAULT NULL,
  `updated_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_activity_time` (`activity_time`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='志愿者活动表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `volunteer_activity`
--

LOCK TABLES `volunteer_activity` WRITE;
/*!40000 ALTER TABLE `volunteer_activity` DISABLE KEYS */;
INSERT INTO `volunteer_activity` VALUES (4,'sew','weq','2026-03-30 00:00:00','2026-03-30 22:48:03','qwe',30,1,'active',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2026-03-30 22:48:03','2026-03-30 22:52:51','2026-03-30 22:48:03.876000',_binary '\0',_binary '\0',NULL,NULL,'2026-03-30 22:52:51.357000'),(5,'养花活动','养花活动。。','2026-03-30 00:00:00','2026-03-30 22:49:38','成都',30,1,'active',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2026-03-30 22:49:38','2026-03-30 22:52:48','2026-03-30 22:49:38.694000',_binary '\0',_binary '\0',NULL,NULL,'2026-03-30 22:52:48.479000');
/*!40000 ALTER TABLE `volunteer_activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `volunteer_activity_record`
--

DROP TABLE IF EXISTS `volunteer_activity_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `volunteer_activity_record` (
  `id` int NOT NULL AUTO_INCREMENT,
  `activity_id` int DEFAULT NULL,
  `activity_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `created_by` int DEFAULT NULL,
  `participation_status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `participation_time` datetime(6) DEFAULT NULL,
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `volunteer_id` int NOT NULL,
  `service_hours` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `volunteer_activity_record`
--

LOCK TABLES `volunteer_activity_record` WRITE;
/*!40000 ALTER TABLE `volunteer_activity_record` DISABLE KEYS */;
INSERT INTO `volunteer_activity_record` VALUES (1,NULL,'上海马拉松','2026-03-28 22:03:22.248000',NULL,'participated','2026-03-04 00:00:00.000000','',1,NULL),(2,NULL,'上海马拉松','2026-03-28 22:03:51.393000',NULL,'participated','2026-03-17 02:00:00.000000','',1,NULL),(3,NULL,'上马','2026-03-28 22:06:20.064000',NULL,'participated','2026-03-10 00:00:00.000000','',1,2),(4,NULL,'献血','2026-03-29 01:25:43.336000',NULL,'participated','2026-03-10 00:00:00.000000','',2,4),(5,NULL,'上马','2026-03-30 22:11:33.738000',NULL,'participated','2026-03-03 00:00:00.000000','',4,4);
/*!40000 ALTER TABLE `volunteer_activity_record` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-30 22:59:24
