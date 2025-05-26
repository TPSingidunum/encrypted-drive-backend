-- --------------------------------------------------------
-- Host:                         madman.masofino.com
-- Server version:               8.0.42-0ubuntu0.24.04.1 - (Ubuntu)
-- Server OS:                    Linux
-- HeidiSQL Version:             12.10.0.7000
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for encrypted_drive
CREATE DATABASE IF NOT EXISTS `encrypted_drive` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `encrypted_drive`;

-- Dumping structure for table encrypted_drive.envelope
CREATE TABLE IF NOT EXISTS `envelope` (
  `envelope_id` int unsigned NOT NULL,
  `file_id` int unsigned NOT NULL,
  `user_id` int unsigned NOT NULL,
  `key` varchar(2048) NOT NULL DEFAULT '',
  `iv` varchar(2048) NOT NULL DEFAULT '',
  `created_at` datetime NOT NULL DEFAULT (0),
  `updated_at` datetime NOT NULL DEFAULT (0),
  PRIMARY KEY (`envelope_id`),
  UNIQUE KEY `uq_envelope_file_id_user_id` (`file_id`,`user_id`),
  KEY `fk_envelope_user_id` (`user_id`),
  CONSTRAINT `fk_envelope_file_id` FOREIGN KEY (`file_id`) REFERENCES `file` (`file_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_envelope_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table encrypted_drive.file
CREATE TABLE IF NOT EXISTS `file` (
  `file_id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `path` varchar(255) NOT NULL,
  `workspace_id` int unsigned NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `parent_id` int unsigned DEFAULT NULL,
  PRIMARY KEY (`file_id`),
  KEY `fk_file_workspace_id_idx` (`workspace_id`),
  KEY `fk_file_parent_id_idx` (`parent_id`),
  CONSTRAINT `fk_file_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `folder` (`folder_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_file_workspace_id` FOREIGN KEY (`workspace_id`) REFERENCES `workspace` (`workspace_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table encrypted_drive.folder
CREATE TABLE IF NOT EXISTS `folder` (
  `folder_id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `workspace_id` int unsigned NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `parent_id` int unsigned DEFAULT NULL,
  PRIMARY KEY (`folder_id`),
  KEY `fk_folder_workspace_id_idx` (`workspace_id`),
  KEY `fk_folder_parent_id_idx` (`parent_id`),
  CONSTRAINT `fk_folder_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `folder` (`folder_id`),
  CONSTRAINT `fk_folder_workspace_id` FOREIGN KEY (`workspace_id`) REFERENCES `workspace` (`workspace_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table encrypted_drive.user
CREATE TABLE IF NOT EXISTS `user` (
  `user_id` int unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(128) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `public_key` text NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  UNIQUE KEY `password_UNIQUE` (`password`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.

-- Dumping structure for table encrypted_drive.workspace
CREATE TABLE IF NOT EXISTS `workspace` (
  `workspace_id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `user_id` int unsigned NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`workspace_id`),
  UNIQUE KEY `uq_workspace_user_id_name` (`name`,`user_id`) USING BTREE,
  KEY `fk_workspace_user_id_idx` (`user_id`),
  CONSTRAINT `fk_workspace_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Data exporting was unselected.

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
