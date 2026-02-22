-- MySQL dump 10.13  Distrib 8.0.29, for Win64 (x86_64)
--
-- Host: localhost    Database: project_db
-- ------------------------------------------------------
-- Server version	8.0.29

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
-- Table structure for table `access_token`
--

DROP TABLE IF EXISTS `access_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `access_token` (
  `token_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '临时访问牌ID',
  `token` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '临时访问牌',
  `info` text CHARACTER SET utf8mb3 COLLATE utf8_general_ci COMMENT '信息',
  `maxage` int NOT NULL DEFAULT '2' COMMENT '最大寿命：默认2小时',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `user_id` int unsigned NOT NULL DEFAULT '0' COMMENT '用户编号',
  PRIMARY KEY (`token_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='登陆访问时长';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `access_token`
--

LOCK TABLES `access_token` WRITE;
/*!40000 ALTER TABLE `access_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `access_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `achievements`
--

DROP TABLE IF EXISTS `achievements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `achievements` (
  `achievements_id` int NOT NULL AUTO_INCREMENT COMMENT '玩家成就ID',
  `game_user` int DEFAULT '0' COMMENT '游戏玩家',
  `player_screen_name` varchar(64) DEFAULT NULL COMMENT '玩家网名',
  `achievement_name` varchar(64) DEFAULT NULL COMMENT '成就名称',
  `gold_coin_rewards` varchar(64) DEFAULT NULL COMMENT '金币奖励',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`achievements_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='玩家成就';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `achievements`
--

LOCK TABLES `achievements` WRITE;
/*!40000 ALTER TABLE `achievements` DISABLE KEYS */;
/*!40000 ALTER TABLE `achievements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auth`
--

DROP TABLE IF EXISTS `auth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auth` (
  `auth_id` int NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `user_group` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '用户组',
  `auth_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '权限名称',
  `get` int DEFAULT '0' COMMENT '获取标志',
  `position` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '位置',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`auth_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auth`
--

LOCK TABLES `auth` WRITE;
/*!40000 ALTER TABLE `auth` DISABLE KEYS */;
INSERT INTO `auth` VALUES (1,'游客','查看首页',1,'top','2026-01-24 18:49:52','2026-01-24 18:49:52'),(2,'游客','查看公告',1,'top','2026-01-24 18:49:52','2026-01-24 18:49:52'),(3,'游戏玩家','玩游戏',1,'top','2026-01-24 18:49:52','2026-01-24 18:49:52'),(4,'游戏玩家','查看排行榜',1,'top','2026-01-24 18:49:52','2026-01-24 18:49:52'),(5,'管理员','管理用户',1,'top','2026-01-24 18:49:52','2026-01-24 18:49:52'),(6,'管理员','管理游戏',1,'top','2026-01-24 18:49:52','2026-01-24 18:49:52');
/*!40000 ALTER TABLE `auth` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `code_token`
--

DROP TABLE IF EXISTS `code_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `code_token` (
  `code_token_id` int NOT NULL AUTO_INCREMENT COMMENT '验证码ID',
  `token` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL COMMENT '令牌',
  `code` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL COMMENT '验证码',
  `expire_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '失效时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`code_token_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=COMPACT COMMENT='验证码';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `code_token`
--

LOCK TABLES `code_token` WRITE;
/*!40000 ALTER TABLE `code_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `code_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `comment_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `user_id` int unsigned NOT NULL DEFAULT '0' COMMENT '评论人ID',
  `reply_to_id` int unsigned NOT NULL DEFAULT '0' COMMENT '回复评论ID',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8_general_ci COMMENT '内容',
  `nickname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '头像地址',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `source_table` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '来源表',
  `source_field` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '来源字段',
  `source_id` int unsigned NOT NULL DEFAULT '0' COMMENT '来源ID',
  PRIMARY KEY (`comment_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='评论';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `culture_tips`
--

DROP TABLE IF EXISTS `culture_tips`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `culture_tips` (
  `tip_id` int NOT NULL AUTO_INCREMENT COMMENT '科普内容ID',
  `track_id` int NOT NULL COMMENT '关联赛道ID',
  `level_id` int DEFAULT '0' COMMENT '关联关卡ID（0表示赛道通用）',
  `tip_title` varchar(128) DEFAULT NULL COMMENT '科普标题',
  `tip_content` text COMMENT '科普内容（一句话）',
  `tip_image` varchar(255) DEFAULT NULL COMMENT '配图路径',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序',
  `is_enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`tip_id`),
  KEY `idx_track_level` (`track_id`,`level_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文化科普内容表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `culture_tips`
--

LOCK TABLES `culture_tips` WRITE;
/*!40000 ALTER TABLE `culture_tips` DISABLE KEYS */;
INSERT INTO `culture_tips` VALUES (1,1,0,'结绳计数','结绳计数是古代最早的计数方式之一，通过绳结的数量和位置来记录数字','/static/culture/rope_tip.png',1,1,'2026-01-25 02:49:52','2026-01-24 18:49:52'),(2,2,0,'筹算','算筹是中国古代重要的计算工具，用不同长度的棍子表示不同的数字','/static/culture/counting_rods_tip.png',1,1,'2026-01-25 02:49:52','2026-01-24 18:49:52'),(3,3,0,'珠算','算盘是古代最先进的计算工具，至今仍在一些地方使用','/static/culture/abacus_tip.png',1,1,'2026-01-25 02:49:52','2026-01-24 18:49:52');
/*!40000 ALTER TABLE `culture_tips` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_achievements`
--

DROP TABLE IF EXISTS `game_achievements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_achievements` (
  `game_achievements_id` int NOT NULL AUTO_INCREMENT COMMENT '游戏成就ID',
  `achievement_name` varchar(64) DEFAULT NULL COMMENT '成就名称',
  `type_of_achievement` varchar(64) DEFAULT NULL COMMENT '成就类型',
  `score_settings` double(8,2) DEFAULT '0.00' COMMENT '分数设置',
  `gold_coin_rewards` varchar(64) DEFAULT NULL COMMENT '金币奖励',
  `achievements_limit_times` int NOT NULL DEFAULT '0' COMMENT '发放限制次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`game_achievements_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='游戏成就';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_achievements`
--

LOCK TABLES `game_achievements` WRITE;
/*!40000 ALTER TABLE `game_achievements` DISABLE KEYS */;
/*!40000 ALTER TABLE `game_achievements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_levels`
--

DROP TABLE IF EXISTS `game_levels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_levels` (
  `game_levels_id` int NOT NULL AUTO_INCREMENT COMMENT '游戏关卡ID',
  `track_id` int DEFAULT '0' COMMENT '所属赛道ID',
  `level_order` int DEFAULT '0' COMMENT '关卡顺序（同一赛道内）',
  `unlock_condition` varchar(255) DEFAULT NULL COMMENT '解锁条件（如：需要完成上一关卡）',
  `level_type` varchar(32) DEFAULT 'interactive' COMMENT '关卡类型（interactive-交互/quiz-问答）',
  `target_number` int DEFAULT '0' COMMENT '目标数字（需要达到的计数结果）',
  `level_name` varchar(64) DEFAULT NULL COMMENT '关卡名称',
  `level_picture` varchar(255) DEFAULT NULL COMMENT '关卡图片',
  `limit_number_of_steps` double(8,2) DEFAULT '0.00' COMMENT '限制步数',
  `elimination_quantity` double(8,2) DEFAULT '0.00' COMMENT '消除数量',
  `game_duration` double(8,2) DEFAULT '0.00' COMMENT '游戏时长',
  `gold_coin_rewards` double(8,2) DEFAULT '0.00' COMMENT '金币奖励',
  `level_description` text COMMENT '关卡说明',
  `comment_len` int NOT NULL DEFAULT '0' COMMENT '评论数',
  `game_record_limit_times` int NOT NULL DEFAULT '0' COMMENT '开始游戏限制次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`game_levels_id`),
  KEY `idx_track_order` (`track_id`,`level_order`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='游戏关卡';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_levels`
--

LOCK TABLES `game_levels` WRITE;
/*!40000 ALTER TABLE `game_levels` DISABLE KEYS */;
INSERT INTO `game_levels` VALUES (1,1,1,NULL,'interactive',0,'关卡一：绳结标记入门',NULL,0.00,0.00,120.00,10.00,'学习用不同装饰表示不同事件',0,0,'2026-02-13 16:14:09','2026-02-13 08:14:09'),(2,1,2,NULL,'interactive',0,'关卡二：颜色和位置',NULL,0.00,0.00,120.00,10.00,'用颜色+位置区分事件',0,0,'2026-02-13 16:14:09','2026-02-13 08:14:09'),(3,1,3,NULL,'interactive',0,'关卡三：结形和次数',NULL,0.00,0.00,120.00,10.00,'用结形和辅助绳记录次数',0,0,'2026-02-13 16:14:09','2026-02-13 08:14:09'),(4,1,4,NULL,'interactive',0,'关卡四：综合练习',NULL,0.00,0.00,120.00,20.00,'综合三种方式进行记忆',0,0,'2026-02-13 16:14:09','2026-02-13 08:14:09'),(5,1,5,NULL,'interactive',0,'关卡五：挑战模式',NULL,0.00,0.00,120.00,30.00,'限时挑战所有知识点',0,0,'2026-02-13 16:14:09','2026-02-13 08:14:09');
/*!40000 ALTER TABLE `game_levels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_record`
--

DROP TABLE IF EXISTS `game_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_record` (
  `game_record_id` int NOT NULL AUTO_INCREMENT COMMENT '游戏记录ID',
  `track_id` int DEFAULT '0' COMMENT '赛道ID',
  `level_name` varchar(64) DEFAULT NULL COMMENT '关卡名称',
  `gamer` int DEFAULT '0' COMMENT '游戏玩家',
  `player_avatar` varchar(255) DEFAULT NULL COMMENT '玩家头像',
  `player_screen_name` varchar(64) DEFAULT NULL COMMENT '玩家网名',
  `gold_coin_rewards` double(8,2) DEFAULT '0.00' COMMENT '金币奖励',
  `game_time` double(8,2) DEFAULT '0.00' COMMENT '游戏时间',
  `number_of_steps_used` double(8,2) DEFAULT '0.00' COMMENT '使用步数',
  `total_game_score` double(8,2) DEFAULT '0.00' COMMENT '游戏总分',
  `clearance_status` varchar(64) DEFAULT NULL COMMENT '通关状态',
  `is_passed` tinyint(1) DEFAULT '0' COMMENT '是否通关（0-未通关，1-已通关）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`game_record_id`),
  KEY `idx_gamer_track` (`gamer`,`track_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='游戏记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_record`
--

LOCK TABLES `game_record` WRITE;
/*!40000 ALTER TABLE `game_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `game_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_tracks`
--

DROP TABLE IF EXISTS `game_tracks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_tracks` (
  `track_id` int NOT NULL AUTO_INCREMENT COMMENT '赛道ID',
  `track_name` varchar(64) NOT NULL COMMENT '赛道名称（结绳计数/筹算/珠算/综合）',
  `track_code` varchar(32) NOT NULL COMMENT '赛道代码（rope/counting_rods/abacus/comprehensive）',
  `track_icon` varchar(255) DEFAULT NULL COMMENT '赛道图标',
  `track_description` text COMMENT '赛道描述',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序',
  `is_enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`track_id`),
  UNIQUE KEY `track_code` (`track_code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='游戏赛道表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_tracks`
--

LOCK TABLES `game_tracks` WRITE;
/*!40000 ALTER TABLE `game_tracks` DISABLE KEYS */;
INSERT INTO `game_tracks` VALUES (1,'结绳计数','rope','/static/tracks/rope.png','通过打结的方式记录数字，体验古代计数智慧',1,1,'2026-01-25 02:49:52','2026-01-24 18:49:52'),(2,'筹算','counting_rods','/static/tracks/counting_rods.png','使用算筹进行计数和运算，感受古代数学工具',2,1,'2026-01-25 02:49:52','2026-01-24 18:49:52'),(3,'珠算','abacus','/static/tracks/abacus.png','操作算盘进行计算，学习传统计算方式',3,1,'2026-01-25 02:49:52','2026-01-24 18:49:52'),(4,'综合挑战','comprehensive','/static/tracks/comprehensive.png','综合运用三种计数方式，挑战更高难度',4,1,'2026-01-25 02:49:52','2026-01-24 18:49:52');
/*!40000 ALTER TABLE `game_tracks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gamer`
--

DROP TABLE IF EXISTS `gamer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gamer` (
  `gamer_id` int NOT NULL AUTO_INCREMENT COMMENT '游戏玩家ID',
  `player_screen_name` varchar(64) NOT NULL COMMENT '玩家网名',
  `gold_coin_balance` double(8,2) DEFAULT '0.00' COMMENT '金币余额',
  `examine_state` varchar(16) NOT NULL DEFAULT '已通过' COMMENT '审核状态',
  `user_id` int NOT NULL DEFAULT '0' COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`gamer_id`),
  UNIQUE KEY `player_screen_name` (`player_screen_name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='游戏玩家';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gamer`
--

LOCK TABLES `gamer` WRITE;
/*!40000 ALTER TABLE `gamer` DISABLE KEYS */;
INSERT INTO `gamer` VALUES (2,'user1',0.00,'已通过',3,'2026-02-13 15:39:22','2026-02-13 07:39:22'),(6,'lisi',0.00,'已通过',5,'2026-02-21 15:40:52','2026-02-21 07:40:52'),(7,'wang1',0.00,'已通过',6,'2026-02-21 17:22:59','2026-02-21 09:22:59');
/*!40000 ALTER TABLE `gamer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hits`
--

DROP TABLE IF EXISTS `hits`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hits` (
  `hits_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '点击记录ID',
  `user_id` int unsigned NOT NULL DEFAULT '0' COMMENT '点击用户ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `source_table` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '来源表',
  `source_field` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '来源字段',
  `source_id` int unsigned NOT NULL DEFAULT '0' COMMENT '来源ID',
  PRIMARY KEY (`hits_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户点击';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hits`
--

LOCK TABLES `hits` WRITE;
/*!40000 ALTER TABLE `hits` DISABLE KEYS */;
/*!40000 ALTER TABLE `hits` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `level_star_record`
--

DROP TABLE IF EXISTS `level_star_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `level_star_record` (
  `star_record_id` int NOT NULL AUTO_INCREMENT COMMENT '星级记录ID',
  `gamer_id` int DEFAULT NULL COMMENT '玩家ID',
  `level_id` int DEFAULT NULL COMMENT '关卡ID',
  `track_id` int DEFAULT NULL COMMENT '赛道ID',
  `total_stars` int DEFAULT '0' COMMENT '累计获得星级（0-3星）',
  `best_time` double(8,2) DEFAULT '999999.00' COMMENT '最佳完成时间（秒）',
  `best_stars` int DEFAULT '0' COMMENT '单次最佳星级',
  `complete_count` int DEFAULT '0' COMMENT '完成次数',
  `last_complete_time` datetime DEFAULT NULL COMMENT '最后完成时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`star_record_id`),
  UNIQUE KEY `uk_gamer_level` (`gamer_id`,`level_id`),
  KEY `idx_gamer_track` (`gamer_id`,`track_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='关卡星级记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `level_star_record`
--

LOCK TABLES `level_star_record` WRITE;
/*!40000 ALTER TABLE `level_star_record` DISABLE KEYS */;
INSERT INTO `level_star_record` VALUES (1,6,1,1,30,2.74,3,10,'2026-02-21 15:42:18','2026-02-21 15:41:29','2026-02-21 07:41:29'),(2,7,1,1,10,5.79,3,4,'2026-02-22 15:30:47','2026-02-21 17:34:37','2026-02-21 09:34:37');
/*!40000 ALTER TABLE `level_star_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medals`
--

DROP TABLE IF EXISTS `medals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medals` (
  `medal_id` int NOT NULL AUTO_INCREMENT COMMENT '勋章ID',
  `medal_name` varchar(64) NOT NULL COMMENT '勋章名称',
  `medal_code` varchar(32) NOT NULL COMMENT '勋章代码',
  `medal_icon` varchar(255) DEFAULT NULL COMMENT '勋章图标',
  `medal_description` text COMMENT '勋章描述',
  `unlock_condition` varchar(255) DEFAULT NULL COMMENT '解锁条件描述',
  `track_id` int DEFAULT '0' COMMENT '关联赛道ID（0表示全赛道）',
  `level_id` int DEFAULT '0' COMMENT '关联关卡ID（0表示不关联特定关卡）',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`medal_id`),
  UNIQUE KEY `medal_code` (`medal_code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='勋章表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medals`
--

LOCK TABLES `medals` WRITE;
/*!40000 ALTER TABLE `medals` DISABLE KEYS */;
INSERT INTO `medals` VALUES (1,'结绳新手','rope_beginner','/static/medals/rope_beginner.png','完成结绳计数赛道第一关','完成结绳计数赛道第一关',1,0,1,'2026-01-25 02:49:52','2026-01-24 18:49:52'),(2,'筹算达人','counting_rods_master','/static/medals/counting_rods_master.png','完成筹算赛道所有关卡','完成筹算赛道所有关卡',2,0,2,'2026-01-25 02:49:52','2026-01-24 18:49:52'),(3,'珠算高手','abacus_expert','/static/medals/abacus_expert.png','完成珠算赛道所有关卡','完成珠算赛道所有关卡',3,0,3,'2026-01-25 02:49:52','2026-01-24 18:49:52'),(4,'全能冠军','all_champion','/static/medals/all_champion.png','完成所有赛道','完成所有赛道',0,0,4,'2026-01-25 02:49:52','2026-01-24 18:49:52');
/*!40000 ALTER TABLE `medals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notice`
--

DROP TABLE IF EXISTS `notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notice` (
  `notice_id` mediumint unsigned NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(125) CHARACTER SET utf8mb3 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '标题',
  `content` longtext CHARACTER SET utf8mb3 COLLATE utf8_general_ci COMMENT '正文',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='公告';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notice`
--

LOCK TABLES `notice` WRITE;
/*!40000 ALTER TABLE `notice` DISABLE KEYS */;
/*!40000 ALTER TABLE `notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player_level_unlock`
--

DROP TABLE IF EXISTS `player_level_unlock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `player_level_unlock` (
  `unlock_id` int NOT NULL AUTO_INCREMENT COMMENT '解锁记录ID',
  `gamer_id` int NOT NULL COMMENT '玩家ID',
  `game_levels_id` int NOT NULL COMMENT '关卡ID',
  `track_id` int NOT NULL COMMENT '赛道ID',
  `is_unlocked` tinyint(1) DEFAULT '0' COMMENT '是否已解锁（0-未解锁，1-已解锁）',
  `is_completed` tinyint(1) DEFAULT '0' COMMENT '是否已完成（0-未完成，1-已完成）',
  `best_score` double(8,2) DEFAULT '0.00' COMMENT '最佳分数',
  `complete_times` int DEFAULT '0' COMMENT '完成次数',
  `first_complete_time` datetime DEFAULT NULL COMMENT '首次完成时间',
  `last_complete_time` datetime DEFAULT NULL COMMENT '最后完成时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`unlock_id`),
  UNIQUE KEY `uk_gamer_level` (`gamer_id`,`game_levels_id`),
  KEY `idx_gamer_track` (`gamer_id`,`track_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='玩家关卡解锁状态表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player_level_unlock`
--

LOCK TABLES `player_level_unlock` WRITE;
/*!40000 ALTER TABLE `player_level_unlock` DISABLE KEYS */;
INSERT INTO `player_level_unlock` VALUES (1,2,1,1,1,0,0.00,0,NULL,NULL,'2026-02-13 16:23:33','2026-02-13 08:23:33'),(2,6,1,1,1,0,0.00,0,NULL,NULL,'2026-02-21 15:41:23','2026-02-21 07:41:23'),(3,6,2,1,1,0,0.00,0,NULL,NULL,'2026-02-21 15:46:30','2026-02-21 07:46:30'),(4,7,1,1,1,0,0.00,0,NULL,NULL,'2026-02-21 17:25:59','2026-02-21 09:25:59');
/*!40000 ALTER TABLE `player_level_unlock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player_medals`
--

DROP TABLE IF EXISTS `player_medals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `player_medals` (
  `player_medal_id` int NOT NULL AUTO_INCREMENT COMMENT '玩家勋章ID',
  `gamer_id` int NOT NULL COMMENT '玩家ID',
  `medal_id` int NOT NULL COMMENT '勋章ID',
  `unlock_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '解锁时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`player_medal_id`),
  UNIQUE KEY `uk_gamer_medal` (`gamer_id`,`medal_id`),
  KEY `idx_gamer` (`gamer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='玩家勋章表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player_medals`
--

LOCK TABLES `player_medals` WRITE;
/*!40000 ALTER TABLE `player_medals` DISABLE KEYS */;
/*!40000 ALTER TABLE `player_medals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `player_question_record`
--

DROP TABLE IF EXISTS `player_question_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `player_question_record` (
  `record_id` int NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `gamer_id` int DEFAULT NULL COMMENT '玩家ID',
  `question_id` int DEFAULT NULL COMMENT '题目ID',
  `level_id` int DEFAULT NULL COMMENT '关卡ID',
  `track_id` int DEFAULT NULL COMMENT '赛道ID',
  `user_answer` text COMMENT '玩家答案（JSON格式）',
  `is_correct` tinyint(1) DEFAULT '0' COMMENT '是否正确（0-错误，1-正确）',
  `answer_time` double(8,2) DEFAULT '0.00' COMMENT '答题耗时（秒）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '答题时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  KEY `idx_gamer_question` (`gamer_id`,`question_id`),
  KEY `idx_gamer_level` (`gamer_id`,`level_id`),
  KEY `idx_gamer_track` (`gamer_id`,`track_id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='玩家答题记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `player_question_record`
--

LOCK TABLES `player_question_record` WRITE;
/*!40000 ALTER TABLE `player_question_record` DISABLE KEYS */;
INSERT INTO `player_question_record` VALUES (1,2,17,1,1,'1',0,7.74,'2026-02-14 16:13:18','2026-02-14 08:13:18'),(2,2,17,1,1,'7',0,6.78,'2026-02-14 16:21:25','2026-02-14 08:21:25'),(3,2,17,1,1,'7',0,30.87,'2026-02-14 16:21:49','2026-02-14 08:21:49'),(4,2,17,1,1,'{\"answer\":\"7\"}',1,5.15,'2026-02-14 16:25:51','2026-02-14 08:25:51'),(5,2,17,1,1,'{\"answer\":\"7\"}',1,16.37,'2026-02-14 16:26:03','2026-02-14 08:26:03'),(6,2,17,1,1,'{\"answer\":\"7\"}',1,30.81,'2026-02-14 16:26:17','2026-02-14 08:26:17'),(7,2,17,1,1,'{\"answer\":\"7\"}',1,73.52,'2026-02-14 16:27:00','2026-02-14 08:27:00'),(8,2,17,1,1,'{\"answer\":\"7\"}',1,108.88,'2026-02-14 16:27:35','2026-02-14 08:27:35'),(9,2,17,1,1,'{\"answer\":\"7\"}',1,4.75,'2026-02-14 16:30:19','2026-02-14 08:30:19'),(10,2,17,1,1,'{\"answer\":\"7\"}',1,17.22,'2026-02-14 16:30:32','2026-02-14 08:30:32'),(11,2,17,1,1,'{\"answer\":\"7\"}',1,20.56,'2026-02-14 16:30:35','2026-02-14 08:30:35'),(12,2,14,1,1,'{\"answer\":\"4\"}',1,5.55,'2026-02-14 16:35:04','2026-02-14 08:35:04'),(13,2,14,1,1,'{\"answer\":\"4\"}',1,100.28,'2026-02-14 16:36:23','2026-02-14 08:36:23'),(14,2,14,1,1,'{\"answer\":\"4\"}',1,116.38,'2026-02-14 16:36:39','2026-02-14 08:36:39'),(15,2,14,1,1,'{\"answer\":\"4\"}',1,146.72,'2026-02-14 16:37:14','2026-02-14 08:37:14'),(16,2,14,1,1,'{\"answer\":\"4\"}',1,5.55,'2026-02-14 16:39:45','2026-02-14 08:39:45'),(17,2,12,1,1,'{\"answer\":\"2\"}',1,5.87,'2026-02-14 16:39:52','2026-02-14 08:39:52'),(18,2,15,1,1,'{\"answer\":\"5\"}',1,5.12,'2026-02-14 16:40:00','2026-02-14 08:40:00'),(19,2,18,1,1,'{\"answer\":\"1\"}',0,5.66,'2026-02-14 16:40:07','2026-02-14 08:40:07'),(20,2,18,1,1,'{\"answer\":\"8\"}',1,12.05,'2026-02-14 16:40:14','2026-02-14 08:40:14'),(21,2,13,1,1,'{\"answer\":\"2\"}',0,6.31,'2026-02-14 16:42:40','2026-02-14 08:42:40'),(22,2,13,1,1,'{\"answer\":\"3\"}',1,10.10,'2026-02-14 16:42:44','2026-02-14 08:42:44'),(23,2,20,1,1,'{\"answer\":\"10\"}',1,33.40,'2026-02-14 16:43:19','2026-02-14 08:43:19'),(24,2,11,1,1,'{\"answer\":\"1\"}',1,4.37,'2026-02-14 16:48:11','2026-02-14 08:48:11'),(25,2,16,1,1,'{\"answer\":\"6\"}',1,10.62,'2026-02-14 16:48:26','2026-02-14 08:48:26'),(26,2,19,1,1,'{\"answer\":\"9\"}',1,7.01,'2026-02-21 14:37:06','2026-02-21 06:37:06'),(27,6,13,1,1,'{\"answer\":\"3\"}',1,3.28,'2026-02-21 15:41:29','2026-02-21 07:41:29'),(28,6,14,1,1,'{\"answer\":\"4\"}',1,3.23,'2026-02-21 15:41:33','2026-02-21 07:41:33'),(29,6,19,1,1,'{\"answer\":\"9\"}',1,5.96,'2026-02-21 15:41:40','2026-02-21 07:41:40'),(30,6,20,1,1,'{\"answer\":\"10\"}',1,8.44,'2026-02-21 15:41:49','2026-02-21 07:41:49'),(31,6,17,1,1,'{\"answer\":\"7\"}',1,4.12,'2026-02-21 15:41:54','2026-02-21 07:41:54'),(32,6,12,1,1,'{\"answer\":\"2\"}',1,2.74,'2026-02-21 15:41:58','2026-02-21 07:41:58'),(33,6,11,1,1,'{\"answer\":\"1\"}',1,4.43,'2026-02-21 15:42:03','2026-02-21 07:42:03'),(34,6,15,1,1,'{\"answer\":\"5\"}',1,3.40,'2026-02-21 15:42:08','2026-02-21 07:42:08'),(35,6,18,1,1,'{\"answer\":\"8\"}',1,4.04,'2026-02-21 15:42:13','2026-02-21 07:42:13'),(36,6,16,1,1,'{\"answer\":\"6\"}',1,3.60,'2026-02-21 15:42:17','2026-02-21 07:42:17'),(37,7,14,1,1,'{\"answer\":\"0\"}',0,505.08,'2026-02-21 17:34:26','2026-02-21 09:34:26'),(38,7,14,1,1,'{\"answer\":\"1\"}',0,509.03,'2026-02-21 17:34:30','2026-02-21 09:34:30'),(39,7,14,1,1,'{\"answer\":\"4\"}',1,515.68,'2026-02-21 17:34:37','2026-02-21 09:34:37'),(40,7,14,1,1,'{\"answer\":\"0\"}',0,21.43,'2026-02-21 17:42:12','2026-02-21 09:42:12'),(41,7,14,1,1,'{\"answer\":\"4\"}',1,5.79,'2026-02-22 14:15:45','2026-02-22 06:15:45'),(42,7,12,1,1,'{\"answer\":\"2\"}',1,22.90,'2026-02-22 14:24:00','2026-02-22 06:24:00'),(43,7,17,1,1,'{\"answer\":\"0\"}',0,9.05,'2026-02-22 15:01:20','2026-02-22 07:01:20'),(44,7,17,1,1,'{\"answer\":\"7\"}',1,16.70,'2026-02-22 15:30:47','2026-02-22 07:30:47');
/*!40000 ALTER TABLE `player_question_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `praise`
--

DROP TABLE IF EXISTS `praise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `praise` (
  `praise_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` int unsigned NOT NULL DEFAULT '0' COMMENT '点赞人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `source_table` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '来源表',
  `source_field` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '来源字段',
  `source_id` int unsigned NOT NULL DEFAULT '0' COMMENT '来源ID',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '点赞状态:1为点赞，0已取消',
  PRIMARY KEY (`praise_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='点赞';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `praise`
--

LOCK TABLES `praise` WRITE;
/*!40000 ALTER TABLE `praise` DISABLE KEYS */;
/*!40000 ALTER TABLE `praise` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `props`
--

DROP TABLE IF EXISTS `props`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `props` (
  `props_id` int NOT NULL AUTO_INCREMENT COMMENT '道具商店ID',
  `prop_name` varchar(64) DEFAULT NULL COMMENT '道具名称',
  `props_picture` varchar(255) DEFAULT NULL COMMENT '道具图片',
  `gold_coins_required` varchar(64) DEFAULT NULL COMMENT '所需金币',
  `props_use` varchar(64) DEFAULT NULL COMMENT '道具用途',
  `purchase_record_limit_times` int NOT NULL DEFAULT '0' COMMENT '购买限制次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`props_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='道具商店';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `props`
--

LOCK TABLES `props` WRITE;
/*!40000 ALTER TABLE `props` DISABLE KEYS */;
/*!40000 ALTER TABLE `props` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchase_record`
--

DROP TABLE IF EXISTS `purchase_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_record` (
  `purchase_record_id` int NOT NULL AUTO_INCREMENT COMMENT '购买记录ID',
  `purchase_user` int DEFAULT '0' COMMENT '购买用户',
  `player_screen_name` varchar(64) DEFAULT NULL COMMENT '玩家网名',
  `prop_name` varchar(64) DEFAULT NULL COMMENT '道具名称',
  `props_picture` varchar(64) DEFAULT NULL COMMENT '道具图片',
  `props_use` varchar(64) DEFAULT NULL COMMENT '道具用途',
  `gold_coins_required` varchar(64) DEFAULT NULL COMMENT '所需金币',
  `use_status` varchar(64) DEFAULT NULL COMMENT '使用状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`purchase_record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='购买记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_record`
--

LOCK TABLES `purchase_record` WRITE;
/*!40000 ALTER TABLE `purchase_record` DISABLE KEYS */;
/*!40000 ALTER TABLE `purchase_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question_bank`
--

DROP TABLE IF EXISTS `question_bank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question_bank` (
  `question_id` int NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `track_id` int DEFAULT NULL COMMENT '所属赛道ID（1-结绳计数，2-筹算，3-珠算）',
  `level_id` int DEFAULT NULL COMMENT '所属关卡ID（关联 game_levels.game_levels_id）',
  `question_type` varchar(32) DEFAULT NULL COMMENT '题目类型（rope_mark-绳结标记，rope_color-绳结颜色，rope_shape-绳结形状，counting_rods_basic-筹算基础，counting_rods_position-筹算位值，counting_rods_add-筹算加法，counting_rods_sub-筹算减法，counting_rods_mix-筹算混合，abacus_basic-珠算基础，abacus_digit-珠算拨数，abacus_add-珠算加法，abacus_sub-珠算减法，abacus_mix-珠算混合）',
  `question_title` varchar(255) DEFAULT NULL COMMENT '题目标题/描述',
  `question_content` text COMMENT '题目内容（JSON格式，存储题目详细配置）',
  `correct_answer` text COMMENT '正确答案（JSON格式，存储标准答案）',
  `difficulty` int DEFAULT '1' COMMENT '难度等级（1-简单，2-中等，3-困难）',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序',
  `is_enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`question_id`),
  KEY `idx_track_level` (`track_id`,`level_id`),
  KEY `idx_question_type` (`question_type`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='题目库表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question_bank`
--

LOCK TABLES `question_bank` WRITE;
/*!40000 ALTER TABLE `question_bank` DISABLE KEYS */;
INSERT INTO `question_bank` VALUES (11,1,1,'rope_mark','今天学习用绳结表示','{\"options\":[\"数字1，聪明的小朋友呀？用绳结怎么表示呢？\"]}','{\"answer\":\"1\"}',1,1,1,'2026-02-13 17:33:23','2026-02-22 06:03:38'),(12,1,1,'rope_mark','今天学习用绳结表示','{\"options\":[\"数字2，聪明的小朋友呀？用绳结怎么表示呢？\"]}','{\"answer\":\"2\"}',1,2,1,'2026-02-13 17:33:23','2026-02-22 06:03:38'),(13,1,1,'rope_mark','今天学习用绳结表示','{\"options\":[\"数字3，聪明的小朋友呀？用绳结怎么表示呢？\"]}','{\"answer\":\"3\"}',1,3,1,'2026-02-13 17:33:23','2026-02-22 06:03:38'),(14,1,1,'rope_mark','今天学习用绳结表示','{\"options\":[\"数字4，聪明的小朋友呀？用绳结怎么表示呢？\"]}','{\"answer\":\"4\"}',1,4,1,'2026-02-13 17:33:23','2026-02-22 06:03:38'),(15,1,1,'rope_mark','今天学习用绳结表示','{\"options\":[\"数字5，聪明的小朋友呀？用绳结怎么表示呢？\"]}','{\"answer\":\"5\"}',1,5,1,'2026-02-13 17:33:23','2026-02-22 06:03:38'),(16,1,1,'rope_mark','今天学习用绳结表示','{\"options\":[\"数字6，聪明的小朋友呀？用绳结怎么表示呢？\"]}','{\"answer\":\"6\"}',1,6,1,'2026-02-13 17:33:23','2026-02-22 06:03:38'),(17,1,1,'rope_mark','今天学习用绳结表示','{\"options\":[\"数字7，聪明的小朋友呀？用绳结怎么表示呢？\"]}','{\"answer\":\"7\"}',1,7,1,'2026-02-13 17:33:23','2026-02-22 06:03:38'),(18,1,1,'rope_mark','今天学习用绳结表示','{\"options\":[\"数字8，聪明的小朋友呀？用绳结怎么表示呢？\"]}','{\"answer\":\"8\"}',1,8,1,'2026-02-13 17:33:23','2026-02-22 06:03:38'),(19,1,1,'rope_mark','今天学习用绳结表示','{\"options\":[\"数字9，聪明的小朋友呀？用绳结怎么表示呢？\"]}','{\"answer\":\"9\"}',1,9,1,'2026-02-13 17:33:23','2026-02-22 06:03:38'),(20,1,1,'rope_mark','今天学习用绳结表示','{\"options\":[\"数字10，聪明的小朋友呀？用绳结怎么表示呢？\"]}','{\"answer\":\"10\"}',1,10,1,'2026-02-13 17:33:23','2026-02-22 06:03:38');
/*!40000 ALTER TABLE `question_bank` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `slides`
--

DROP TABLE IF EXISTS `slides`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `slides` (
  `slides_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '标题',
  `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '内容',
  `url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '链接',
  `img` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '轮播图',
  `hits` int unsigned NOT NULL DEFAULT '0' COMMENT '点击量',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`slides_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='轮播图';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `slides`
--

LOCK TABLES `slides` WRITE;
/*!40000 ALTER TABLE `slides` DISABLE KEYS */;
/*!40000 ALTER TABLE `slides` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `upload`
--

DROP TABLE IF EXISTS `upload`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `upload` (
  `upload_id` int NOT NULL AUTO_INCREMENT COMMENT '上传ID',
  `name` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '文件名',
  `path` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '访问路径',
  `file` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '文件路径',
  `display` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '显示顺序',
  `father_id` int DEFAULT '0' COMMENT '父级ID',
  `dir` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '文件夹',
  `type` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '文件类型',
  PRIMARY KEY (`upload_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='文件上传';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `upload`
--

LOCK TABLES `upload` WRITE;
/*!40000 ALTER TABLE `upload` DISABLE KEYS */;
INSERT INTO `upload` VALUES (1,'movie.mp4','/upload/movie.mp4','',NULL,0,NULL,'video');
/*!40000 ALTER TABLE `upload` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `user_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `state` smallint unsigned NOT NULL DEFAULT '1' COMMENT '账户状态：(1可用|2异常|3已冻结|4已注销)',
  `user_group` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '所在用户组',
  `login_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '上次登录时间',
  `phone` varchar(11) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '手机号码',
  `phone_state` smallint unsigned NOT NULL DEFAULT '0' COMMENT '手机认证：(0未认证|1审核中|2已认证)',
  `username` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '用户名',
  `nickname` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT '' COMMENT '昵称',
  `password` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '密码',
  `email` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT '' COMMENT '邮箱',
  `email_state` smallint unsigned NOT NULL DEFAULT '0' COMMENT '邮箱认证：(0未认证|1审核中|2已认证)',
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '头像地址',
  `open_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '针对获取用户信息字段',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户账户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,1,'管理员','2026-01-24 18:49:52',NULL,0,'admin','admin','123456','',0,'/api/upload/admin_avatar.jpg',NULL,'2026-01-24 18:49:52'),(3,1,'游戏玩家','2026-02-13 07:16:11',NULL,0,'user1','user1','123456','',0,NULL,NULL,'2026-02-13 07:16:11'),(5,1,'游戏玩家','2026-02-21 07:40:51',NULL,0,'zhangsan','user2','123456','',0,NULL,NULL,'2026-02-21 07:40:51'),(6,1,'游戏玩家','2026-02-21 09:22:58',NULL,0,'wang1','wang1','123456','',0,NULL,NULL,'2026-02-21 09:22:58');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_group`
--

DROP TABLE IF EXISTS `user_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_group` (
  `group_id` mediumint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户组ID',
  `display` smallint unsigned NOT NULL DEFAULT '100' COMMENT '显示顺序',
  `name` varchar(16) CHARACTER SET utf8mb3 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '名称',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '描述',
  `source_table` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '来源表',
  `source_field` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8_general_ci DEFAULT NULL COMMENT '来源字段',
  `source_id` int unsigned NOT NULL DEFAULT '0' COMMENT '来源ID',
  `register` smallint unsigned DEFAULT '0' COMMENT '注册位置',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`group_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='用户组';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_group`
--

LOCK TABLES `user_group` WRITE;
/*!40000 ALTER TABLE `user_group` DISABLE KEYS */;
INSERT INTO `user_group` VALUES (1,1,'游客','游客身份',NULL,NULL,0,2,'2026-01-24 18:49:52','2026-01-24 18:49:52'),(2,2,'游戏玩家','游戏玩家身份','gamer','gamer_id',0,2,'2026-01-24 18:49:52','2026-01-24 18:49:52'),(3,3,'管理员','管理员身份',NULL,NULL,0,3,'2026-01-24 18:49:52','2026-01-24 18:49:52');
/*!40000 ALTER TABLE `user_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'project_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-02-22 15:34:45
