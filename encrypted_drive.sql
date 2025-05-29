/*
 Navicat Premium Dump SQL

 Source Server         : Madman
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42-0ubuntu0.24.04.1)
 Source Host           : mage.masofino.com:3306
 Source Schema         : encrypted_drive

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42-0ubuntu0.24.04.1)
 File Encoding         : 65001

 Date: 29/05/2025 23:20:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for envelope
-- ----------------------------
DROP TABLE IF EXISTS `envelope`;
CREATE TABLE `envelope`  (
  `envelope_id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `file_id` int UNSIGNED NOT NULL,
  `user_id` int UNSIGNED NOT NULL,
  `encryption_key` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `iv` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `created_at` datetime NOT NULL DEFAULT (0),
  `updated_at` datetime NOT NULL DEFAULT (0),
  PRIMARY KEY (`envelope_id`) USING BTREE,
  UNIQUE INDEX `uq_envelope_file_id_user_id`(`file_id` ASC, `user_id` ASC) USING BTREE,
  INDEX `fk_envelope_user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_envelope_file_id` FOREIGN KEY (`file_id`) REFERENCES `file` (`file_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_envelope_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file`  (
  `file_id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `workspace_id` int UNSIGNED NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  `parent_id` int UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`file_id`) USING BTREE,
  INDEX `fk_file_workspace_id_idx`(`workspace_id` ASC) USING BTREE,
  INDEX `fk_file_parent_id_idx`(`parent_id` ASC) USING BTREE,
  CONSTRAINT `fk_file_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `folder` (`folder_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_file_workspace_id` FOREIGN KEY (`workspace_id`) REFERENCES `workspace` (`workspace_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for folder
-- ----------------------------
DROP TABLE IF EXISTS `folder`;
CREATE TABLE `folder`  (
  `folder_id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `workspace_id` int UNSIGNED NOT NULL,
  `created_at` datetime NULL DEFAULT NULL,
  `updated_at` datetime NULL DEFAULT NULL,
  `parent_id` int UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`folder_id`) USING BTREE,
  INDEX `fk_folder_workspace_id_idx`(`workspace_id` ASC) USING BTREE,
  INDEX `fk_folder_parent_id_idx`(`parent_id` ASC) USING BTREE,
  CONSTRAINT `fk_folder_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `folder` (`folder_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_folder_workspace_id` FOREIGN KEY (`workspace_id`) REFERENCES `workspace` (`workspace_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `public_key` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `email_UNIQUE`(`email` ASC) USING BTREE,
  UNIQUE INDEX `password_UNIQUE`(`password` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for workspace
-- ----------------------------
DROP TABLE IF EXISTS `workspace`;
CREATE TABLE `workspace`  (
  `workspace_id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` int UNSIGNED NOT NULL,
  `created_at` datetime NULL DEFAULT NULL,
  `updated_at` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`workspace_id`) USING BTREE,
  UNIQUE INDEX `uq_workspace_user_id_name`(`name` ASC, `user_id` ASC) USING BTREE,
  INDEX `fk_workspace_user_id_idx`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_workspace_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
