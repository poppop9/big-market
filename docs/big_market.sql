/*
 Navicat Premium Dump SQL

 Source Server         : localhost - mysql
 Source Server Type    : MySQL
 Source Server Version : 90001 (9.0.1)
 Source Host           : localhost:3306
 Source Schema         : big_market

 Target Server Type    : MySQL
 Target Server Version : 90001 (9.0.1)
 File Encoding         : 65001

 Date: 09/11/2024 16:40:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for Award
-- ----------------------------
DROP TABLE IF EXISTS `Award`;
CREATE TABLE `Award`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `strategyId` int NOT NULL COMMENT '策略id',
  `awardId` int NOT NULL COMMENT '奖品id',
  `awardKey` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '奖品key',
  `awardConfig` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '奖品配置',
  `awardTitle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '奖品标题',
  `awardSubtitle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '奖品副标题',
  `awardCount` bigint NOT NULL COMMENT '奖品库存',
  `awardRate` double NOT NULL COMMENT '奖品被抽取到的概率',
  `awardSort` int NOT NULL COMMENT '奖品在前端的排序',
  `rules` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '奖品规则',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '奖品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Award
-- ----------------------------
INSERT INTO `Award` VALUES (1, 10001, 101, '', '', '随机积分', '', 79998, 74, 1, '{ \"rule_common_blacklist\": \"-1\" }', '2024-09-21 20:53:58', '2024-09-21 23:09:50');
INSERT INTO `Award` VALUES (2, 10001, 102, NULL, NULL, '淘宝优惠券', NULL, 50000, 4, 2, '{ \"rule_common\": \"-1\" }', '2024-09-21 21:01:52', '2024-09-21 23:07:46');
INSERT INTO `Award` VALUES (3, 10001, 103, NULL, NULL, '京东优惠券', NULL, 50000, 4, 3, '{ \"rule_common\": \"-1\" }', '2024-09-21 21:04:12', '2024-09-21 23:07:47');
INSERT INTO `Award` VALUES (4, 10001, 104, NULL, NULL, '1 天 VIP', NULL, 50000, 4, 4, '{ \"rule_common\": \"-1\" }', '2024-09-21 21:05:18', '2024-09-21 23:07:48');
INSERT INTO `Award` VALUES (5, 10001, 105, NULL, NULL, '高额随机积分', NULL, 50000, 4, 5, '{ \"rule_common\": \"-1\" }', '2024-09-21 21:05:41', '2024-09-21 23:07:49');
INSERT INTO `Award` VALUES (6, 10001, 106, NULL, NULL, '付费音乐 30 天免费听', '抽奖 10 次之后解锁', 10000, 3, 6, '{ \"rule_lock\": \"10\" }', '2024-09-21 21:06:24', '2024-09-21 21:31:04');
INSERT INTO `Award` VALUES (7, 10001, 107, NULL, NULL, '付费视频 30 天免费看', '抽奖 10 次之后解锁', 10000, 3, 7, '{ \"rule_lock\": \"10\" }', '2024-09-21 21:06:50', '2024-09-21 21:31:00');
INSERT INTO `Award` VALUES (8, 10001, 108, NULL, NULL, '电子书 30 天免费看', '抽奖 10 次之后解锁', 10000, 3, 8, '{ \"rule_lock\": \"10\" }', '2024-09-21 21:07:22', '2024-09-21 21:30:58');
INSERT INTO `Award` VALUES (9, 10001, 109, NULL, NULL, '30 天 VIP', '抽奖 20 次之后解锁', 1000, 1, 9, '{ \"rule_lock_long\": \"20\" }', '2024-09-21 21:07:54', '2024-09-21 21:31:13');

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
INSERT INTO `Award_SEQ` VALUES (1);

-- ----------------------------
-- Table structure for Strategy
-- ----------------------------
DROP TABLE IF EXISTS `Strategy`;
CREATE TABLE `Strategy`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `strategyId` int NOT NULL COMMENT '策略id',
  `strategyDesc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '策略描述',
  `rules` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '策略的规则，json格式',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '策略表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of Strategy
-- ----------------------------
INSERT INTO `Strategy` VALUES (1, 10001, '测试策略', '{ \"rule_grand\": \"50\" }', '2024-09-21 21:38:14', '2024-09-21 21:41:43');

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
INSERT INTO `Strategy_SEQ` VALUES (1);

SET FOREIGN_KEY_CHECKS = 1;
