/*
 Navicat Premium Dump SQL

 Source Server         : localhost - mysql
 Source Server Type    : MySQL
 Source Server Version : 80403 (8.4.3)
 Source Host           : localhost:3306
 Source Schema         : big_market

 Target Server Type    : MySQL
 Target Server Version : 80403 (8.4.3)
 File Encoding         : 65001

 Date: 24/11/2024 12:04:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for Award
-- ----------------------------
DROP TABLE IF EXISTS `Award`;
CREATE TABLE `Award`  (
  `awardRate` double NULL DEFAULT NULL,
  `awardSort` int NULL DEFAULT NULL,
  `awardCount` bigint NULL DEFAULT NULL,
  `awardId` bigint NULL DEFAULT NULL,
  `createTime` datetime(6) NULL DEFAULT NULL,
  `id` bigint NOT NULL,
  `strategyId` bigint NULL DEFAULT NULL,
  `updateTime` datetime(6) NULL DEFAULT NULL,
  `awardSubtitle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `awardTitle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `IDXby462r777g4s8bki2waeghb0i`(`strategyId` ASC) USING BTREE,
  INDEX `IDXnlh7tf9u7pis1h9m2ht4123pc`(`awardId` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Award
-- ----------------------------
INSERT INTO `Award` VALUES (74, 1, 80000, 101, '2024-11-24 12:02:20.064602', 1, 10001, '2024-11-24 12:02:20.064602', NULL, '随机积分');
INSERT INTO `Award` VALUES (4, 2, 50000, 102, '2024-11-24 12:02:20.064602', 2, 10001, '2024-11-24 12:02:20.064602', NULL, '淘宝优惠券');
INSERT INTO `Award` VALUES (4, 3, 50000, 103, '2024-11-24 12:02:20.064602', 3, 10001, '2024-11-24 12:02:20.064602', NULL, '京东优惠券');
INSERT INTO `Award` VALUES (4, 4, 50000, 104, '2024-11-24 12:02:20.064602', 4, 10001, '2024-11-24 12:02:20.064602', NULL, '1 天 VIP');
INSERT INTO `Award` VALUES (4, 5, 50000, 105, '2024-11-24 12:02:20.064602', 5, 10001, '2024-11-24 12:02:20.064602', NULL, '高额随机积分');
INSERT INTO `Award` VALUES (3, 6, 10000, 106, '2024-11-24 12:02:20.064602', 6, 10001, '2024-11-24 12:02:20.064602', '抽奖 10 次后解锁', '付费音乐 30 天免费听');
INSERT INTO `Award` VALUES (3, 7, 10000, 107, '2024-11-24 12:02:20.064602', 7, 10001, '2024-11-24 12:02:20.064602', '抽奖 10 次后解锁', '付费电影 30 天免费看');
INSERT INTO `Award` VALUES (3, 8, 10000, 108, '2024-11-24 12:02:20.064602', 8, 10001, '2024-11-24 12:02:20.064602', '抽奖 10 次后解锁', '付费小说 30 天免费看');
INSERT INTO `Award` VALUES (1, 9, 100, 109, '2024-11-24 12:02:20.064602', 9, 10001, '2024-11-24 12:02:20.064602', '抽奖 20 次后解锁', 'iPhone 15 Pro Max');

-- ----------------------------
-- Table structure for Award_SEQ
-- ----------------------------
DROP TABLE IF EXISTS `Award_SEQ`;
CREATE TABLE `Award_SEQ`  (
  `next_val` bigint NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Award_SEQ
-- ----------------------------
INSERT INTO `Award_SEQ` VALUES (101);

-- ----------------------------
-- Table structure for RafflePool
-- ----------------------------
DROP TABLE IF EXISTS `RafflePool`;
CREATE TABLE `RafflePool`  (
  `createTime` datetime(6) NULL DEFAULT NULL,
  `id` bigint NOT NULL,
  `normalTimeEndValue` bigint NULL DEFAULT NULL,
  `normalTimeStartValue` bigint NULL DEFAULT NULL,
  `specialTimeValue` bigint NULL DEFAULT NULL,
  `strategyId` bigint NULL DEFAULT NULL,
  `updateTime` datetime(6) NULL DEFAULT NULL,
  `awardIds` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `rafflePoolName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `ruleDescription` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `rafflePoolType` enum('NormalTime','SpecialTime','SpecialRule') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of RafflePool
-- ----------------------------
INSERT INTO `RafflePool` VALUES ('2024-11-24 12:02:20.201995', 1, 9223372036854775807, 20, -1, 10001, '2024-11-24 12:02:20.201995', '[101,102,103,104,105,106,107,108,109]', 'AllAwardPool', '所有奖品。抽奖次数大于等于 20 时', 'NormalTime');
INSERT INTO `RafflePool` VALUES ('2024-11-24 12:02:20.201995', 2, 19, 10, -1, 10001, '2024-11-24 12:02:20.201995', '[101,102,103,104,105,106,107,108]', 'No1stAwardPool', '没有 109 大奖。抽奖次数在 10-19 次时', 'NormalTime');
INSERT INTO `RafflePool` VALUES ('2024-11-24 12:02:20.201995', 3, 9, 0, -1, 10001, '2024-11-24 12:02:20.201995', '[101,102,103,104,105]', 'No1stAnd2ndAwardPool', '没有 105，106，107，108，109 大奖。抽奖次数在 0-9 次时', 'NormalTime');
INSERT INTO `RafflePool` VALUES ('2024-11-24 12:02:20.201995', 4, -1, -1, 50, 10001, '2024-11-24 12:02:20.201995', '[106,107,108,109]', '1stAnd2ndAwardPool', '都是一二级的大奖。抽奖第 50 次，必中大奖', 'SpecialTime');
INSERT INTO `RafflePool` VALUES ('2024-11-24 12:02:20.201995', 5, -1, -1, -1, 10001, '2024-11-24 12:02:20.201995', '[101]', 'BlacklistPool', '黑名单用户专属抽奖池', 'SpecialRule');

-- ----------------------------
-- Table structure for RafflePool_SEQ
-- ----------------------------
DROP TABLE IF EXISTS `RafflePool_SEQ`;
CREATE TABLE `RafflePool_SEQ`  (
  `next_val` bigint NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of RafflePool_SEQ
-- ----------------------------
INSERT INTO `RafflePool_SEQ` VALUES (101);

-- ----------------------------
-- Table structure for Strategy
-- ----------------------------
DROP TABLE IF EXISTS `Strategy`;
CREATE TABLE `Strategy`  (
  `createTime` datetime(6) NULL DEFAULT NULL,
  `id` bigint NOT NULL,
  `strategyId` bigint NULL DEFAULT NULL,
  `updateTime` datetime(6) NULL DEFAULT NULL,
  `strategyDesc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Strategy
-- ----------------------------
INSERT INTO `Strategy` VALUES ('2024-11-24 12:02:19.923165', 1, 10001, '2024-11-24 12:02:19.923165', '策略 1');

-- ----------------------------
-- Table structure for Strategy_SEQ
-- ----------------------------
DROP TABLE IF EXISTS `Strategy_SEQ`;
CREATE TABLE `Strategy_SEQ`  (
  `next_val` bigint NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Strategy_SEQ
-- ----------------------------
INSERT INTO `Strategy_SEQ` VALUES (51);

-- ----------------------------
-- Table structure for User
-- ----------------------------
DROP TABLE IF EXISTS `User`;
CREATE TABLE `User`  (
  `userRole` tinyint NULL DEFAULT NULL,
  `createTime` datetime(6) NULL DEFAULT NULL,
  `id` bigint NOT NULL,
  `raffleTimes` bigint NULL DEFAULT NULL,
  `updateTime` datetime(6) NULL DEFAULT NULL,
  `userId` bigint NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `userName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  CONSTRAINT `User_chk_1` CHECK (`userRole` between 0 and 2)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of User
-- ----------------------------
INSERT INTO `User` VALUES (2, '2024-11-24 12:02:20.141266', 1, 0, '2024-11-24 12:02:20.141266', 404, NULL, '404用户');
INSERT INTO `User` VALUES (0, '2024-11-24 12:02:20.141266', 2, 0, '2024-11-24 12:02:20.141266', 111, NULL, '管理员');
INSERT INTO `User` VALUES (1, '2024-11-24 12:02:20.141266', 3, 0, '2024-11-24 12:02:20.141266', 222, NULL, '普通用户');

-- ----------------------------
-- Table structure for UserPurchaseHistory
-- ----------------------------
DROP TABLE IF EXISTS `UserPurchaseHistory`;
CREATE TABLE `UserPurchaseHistory`  (
  `createTime` datetime(6) NULL DEFAULT NULL,
  `id` bigint NOT NULL,
  `updateTime` datetime(6) NULL DEFAULT NULL,
  `userId` bigint NULL DEFAULT NULL,
  `purchaseName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of UserPurchaseHistory
-- ----------------------------

-- ----------------------------
-- Table structure for UserPurchaseHistory_SEQ
-- ----------------------------
DROP TABLE IF EXISTS `UserPurchaseHistory_SEQ`;
CREATE TABLE `UserPurchaseHistory_SEQ`  (
  `next_val` bigint NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of UserPurchaseHistory_SEQ
-- ----------------------------
INSERT INTO `UserPurchaseHistory_SEQ` VALUES (1);

-- ----------------------------
-- Table structure for UserRaffleHistory
-- ----------------------------
DROP TABLE IF EXISTS `UserRaffleHistory`;
CREATE TABLE `UserRaffleHistory`  (
  `awardId` bigint NULL DEFAULT NULL,
  `createTime` datetime(6) NULL DEFAULT NULL,
  `id` bigint NOT NULL,
  `strategyId` bigint NULL DEFAULT NULL,
  `updateTime` datetime(6) NULL DEFAULT NULL,
  `userId` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of UserRaffleHistory
-- ----------------------------

-- ----------------------------
-- Table structure for UserRaffleHistory_SEQ
-- ----------------------------
DROP TABLE IF EXISTS `UserRaffleHistory_SEQ`;
CREATE TABLE `UserRaffleHistory_SEQ`  (
  `next_val` bigint NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of UserRaffleHistory_SEQ
-- ----------------------------
INSERT INTO `UserRaffleHistory_SEQ` VALUES (1);

-- ----------------------------
-- Table structure for User_SEQ
-- ----------------------------
DROP TABLE IF EXISTS `User_SEQ`;
CREATE TABLE `User_SEQ`  (
  `next_val` bigint NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of User_SEQ
-- ----------------------------
INSERT INTO `User_SEQ` VALUES (101);

SET FOREIGN_KEY_CHECKS = 1;
