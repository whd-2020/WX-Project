CREATE DATABASE IF NOT EXISTS project_db CHARACTER SET utf8mb4;
USE `project_db`;

DROP TABLE IF EXISTS `auth`;
CREATE TABLE `auth` (
  `auth_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `user_group` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '用户组',
  `auth_name` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '权限名称',
  `get` int(11) DEFAULT 0 COMMENT '获取标志',
  `position` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '位置',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`auth_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='权限表';

INSERT INTO `auth` VALUES (1, '游客', '查看首页', 1, 'top', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO `auth` VALUES (2, '游客', '查看公告', 1, 'top', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO `auth` VALUES (3, '游戏玩家', '玩游戏', 1, 'top', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO `auth` VALUES (4, '游戏玩家', '查看排行榜', 1, 'top', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO `auth` VALUES (5, '管理员', '管理用户', 1, 'top', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO `auth` VALUES (6, '管理员', '管理游戏', 1, 'top', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

DROP TABLE IF EXISTS `slides`;
CREATE TABLE `slides` (
  `slides_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` varchar(64) CHARACTER SET utf8 DEFAULT NULL COMMENT '标题',
  `content` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '内容',
  `url` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '链接',
  `img` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '轮播图',
  `hits` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '点击量',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`slides_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='轮播图';

DROP TABLE IF EXISTS `upload`;
CREATE TABLE `upload` (
  `upload_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '上传ID',
  `name` varchar(64) CHARACTER SET utf8 DEFAULT NULL COMMENT '文件名',
  `path` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '访问路径',
  `file` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '文件路径',
  `display` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '显示顺序',
  `father_id` int(11) DEFAULT '0' COMMENT '父级ID',
  `dir` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '文件夹',
  `type` varchar(32) CHARACTER SET utf8 DEFAULT NULL COMMENT '文件类型',
  PRIMARY KEY (`upload_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='文件上传';
INSERT INTO `upload` VALUES ('1','movie.mp4','/upload/movie.mp4','',null,'0',null,'video');

DROP TABLE IF EXISTS `user_group`;
CREATE TABLE `user_group` (
  `group_id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户组ID',
  `display` smallint(4) unsigned NOT NULL DEFAULT '100' COMMENT '显示顺序',
  `name` varchar(16) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '名称',
  `description` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '描述',
  `source_table` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '来源表',
  `source_field` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '来源字段',
  `source_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '来源ID',
  `register` smallint(1) unsigned DEFAULT '0' COMMENT '注册位置',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`group_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户组';

INSERT INTO `user_group` VALUES (1, 1, '游客', '游客身份', NULL, NULL, 0, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO `user_group` VALUES (2, 2, '游戏玩家', '游戏玩家身份', 'gamer', 'gamer_id', 0, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO `user_group` VALUES (3, 3, '管理员', '管理员身份', NULL, NULL, 0, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

DROP TABLE IF EXISTS `access_token`;
CREATE TABLE `access_token` (
  `token_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '临时访问牌ID',
  `token` varchar(64) CHARACTER SET utf8 DEFAULT NULL COMMENT '临时访问牌',
  `info` text CHARACTER SET utf8 COMMENT '信息',
  `maxage` int(2) NOT NULL DEFAULT '2' COMMENT '最大寿命：默认2小时',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `user_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '用户编号',
  PRIMARY KEY (`token_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='登陆访问时长';

DROP TABLE IF EXISTS `code_token`;
CREATE TABLE `code_token` (
  `code_token_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '验证码ID',
  `token` varchar(255) CHARACTER SET latin1 DEFAULT NULL COMMENT '令牌',
  `code` varchar(255) CHARACTER SET latin1 DEFAULT NULL COMMENT '验证码',
  `expire_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '失效时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`code_token_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='验证码';

DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `notice_id` mediumint(8) unsigned NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(125) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '标题',
  `content` longtext CHARACTER SET utf8 COMMENT '正文',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='公告';

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `user_id` int(8) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `state` smallint(1) unsigned NOT NULL DEFAULT '1' COMMENT '账户状态：(1可用|2异常|3已冻结|4已注销)',
  `user_group` varchar(32) CHARACTER SET utf8 DEFAULT NULL COMMENT '所在用户组',
  `login_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '上次登录时间',
  `phone` varchar(11) CHARACTER SET utf8 DEFAULT NULL COMMENT '手机号码',
  `phone_state` smallint(1) unsigned NOT NULL DEFAULT '0' COMMENT '手机认证：(0未认证|1审核中|2已认证)',
  `username` varchar(16) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '用户名',
  `nickname` varchar(16) CHARACTER SET utf8 DEFAULT '' COMMENT '昵称',
  `password` varchar(64) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '密码',
  `email` varchar(64) CHARACTER SET utf8 DEFAULT '' COMMENT '邮箱',
  `email_state` smallint(1) unsigned NOT NULL DEFAULT '0' COMMENT '邮箱认证：(0未认证|1审核中|2已认证)',
  `avatar` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '头像地址',
  `open_id` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '针对获取用户信息字段',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户账户';

INSERT INTO `user` VALUES (1, 1, '管理员', CURRENT_TIMESTAMP, NULL, 0, 'admin', 'admin', '123456', '', 0, '/api/upload/admin_avatar.jpg', NULL, CURRENT_TIMESTAMP);

DROP TABLE IF EXISTS `gamer`;
CREATE TABLE `gamer` (
  `gamer_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '游戏玩家ID',
  `player_screen_name` varchar(64) NOT NULL UNIQUE COMMENT '玩家网名',
  `gold_coin_balance` double(8,2) DEFAULT 0 COMMENT '金币余额',
  `examine_state` varchar(16) DEFAULT '已通过' NOT NULL COMMENT '审核状态',
  `user_id` int(11) DEFAULT '0' NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`gamer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏玩家';

-- ==================== 游戏赛道表 ====================
DROP TABLE IF EXISTS `game_tracks`;
CREATE TABLE `game_tracks` (
  `track_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '赛道ID',
  `track_name` varchar(64) NOT NULL COMMENT '赛道名称（结绳计数/筹算/珠算/综合）',
  `track_code` varchar(32) NOT NULL UNIQUE COMMENT '赛道代码（rope/counting_rods/abacus/comprehensive）',
  `track_icon` varchar(255) COMMENT '赛道图标',
  `track_description` text COMMENT '赛道描述',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序顺序',
  `is_enabled` tinyint(1) DEFAULT 1 COMMENT '是否启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`track_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏赛道表';

-- 初始化四大赛道数据
INSERT INTO `game_tracks` VALUES 
(1, '结绳计数', 'rope', '/static/tracks/rope.png', '通过打结的方式记录数字，体验古代计数智慧', 1, 1, NOW(), NOW()),
(2, '筹算', 'counting_rods', '/static/tracks/counting_rods.png', '使用算筹进行计数和运算，感受古代数学工具', 2, 1, NOW(), NOW()),
(3, '珠算', 'abacus', '/static/tracks/abacus.png', '操作算盘进行计算，学习传统计算方式', 3, 1, NOW(), NOW()),
(4, '综合挑战', 'comprehensive', '/static/tracks/comprehensive.png', '综合运用三种计数方式，挑战更高难度', 4, 1, NOW(), NOW());

DROP TABLE IF EXISTS `game_levels`;
CREATE TABLE `game_levels` (
  `game_levels_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '游戏关卡ID',
  `track_id` int(11) DEFAULT 0 COMMENT '所属赛道ID',
  `game_type` varchar(32) COMMENT '所属游戏类型代码（与 question_bank.game_type 对应，例如 rope_mode1-绳结+装饰）',
  `level_order` int(11) DEFAULT 0 COMMENT '关卡顺序（同一赛道内）',
  `unlock_condition` varchar(255) COMMENT '解锁条件（如：需要完成上一关卡）',
  `level_type` varchar(32) DEFAULT 'interactive' COMMENT '关卡类型（interactive-交互/quiz-问答）',
  `target_number` int(11) DEFAULT 0 COMMENT '目标数字（需要达到的计数结果）',
  `level_name` varchar(64) COMMENT '关卡名称',
  `level_picture` varchar(255) COMMENT '关卡图片',
  `limit_number_of_steps` double(8,2) DEFAULT 0 COMMENT '限制步数',
  `elimination_quantity` double(8,2) DEFAULT 0 COMMENT '消除数量',
  `game_duration` double(8,2) DEFAULT 0 COMMENT '游戏时长',
  `gold_coin_rewards` double(8,2) DEFAULT 0 COMMENT '金币奖励',
  `level_description` text COMMENT '关卡说明',
  `comment_len` int(11) DEFAULT 0 NOT NULL COMMENT '评论数',
  `game_record_limit_times` int(8) DEFAULT 0 NOT NULL COMMENT '开始游戏限制次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`game_levels_id`),
  INDEX `idx_track_order` (`track_id`, `level_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏关卡';

-- ==================== 玩家关卡解锁状态表（核心游戏进度表） ====================
DROP TABLE IF EXISTS `player_level_unlock`;
CREATE TABLE `player_level_unlock` (
  `unlock_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '解锁记录ID',
  `gamer_id` int(11) NOT NULL COMMENT '玩家ID',
  `game_levels_id` int(11) NOT NULL COMMENT '关卡ID',
  `track_id` int(11) NOT NULL COMMENT '赛道ID',
  `is_unlocked` tinyint(1) DEFAULT 0 COMMENT '是否已解锁（0-未解锁，1-已解锁）',
  `is_completed` tinyint(1) DEFAULT 0 COMMENT '是否已完成（0-未完成，1-已完成）',
  `best_score` double(8,2) DEFAULT 0 COMMENT '最佳分数',
  `complete_times` int(11) DEFAULT 0 COMMENT '完成次数',
  `first_complete_time` datetime COMMENT '首次完成时间',
  `last_complete_time` datetime COMMENT '最后完成时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`unlock_id`),
  UNIQUE KEY `uk_gamer_level` (`gamer_id`, `game_levels_id`),
  INDEX `idx_gamer_track` (`gamer_id`, `track_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家关卡解锁状态表';

DROP TABLE IF EXISTS `game_record`;
CREATE TABLE `game_record` (
  `game_record_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '游戏记录ID',
  `track_id` int(11) DEFAULT 0 COMMENT '赛道ID',
  `level_name` varchar(64) COMMENT '关卡名称',
  `gamer` int(11) DEFAULT 0 COMMENT '游戏玩家',
  `player_avatar` varchar(255) COMMENT '玩家头像',
  `player_screen_name` varchar(64) COMMENT '玩家网名',
  `gold_coin_rewards` double(8,2) DEFAULT 0 COMMENT '金币奖励',
  `game_time` double(8,2) DEFAULT 0 COMMENT '游戏时间',
  `number_of_steps_used` double(8,2) DEFAULT 0 COMMENT '使用步数',
  `total_game_score` double(8,2) DEFAULT 0 COMMENT '游戏总分',
  `clearance_status` varchar(64) COMMENT '通关状态',
  `is_passed` tinyint(1) DEFAULT 0 COMMENT '是否通关（0-未通关，1-已通关）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`game_record_id`),
  INDEX `idx_gamer_track` (`gamer`, `track_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏记录';

DROP TABLE IF EXISTS `props`;
CREATE TABLE `props` (
  `props_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '道具商店ID',
  `prop_name` varchar(64) COMMENT '道具名称',
  `props_picture` varchar(255) COMMENT '道具图片',
  `gold_coins_required` varchar(64) COMMENT '所需金币',
  `props_use` varchar(64) COMMENT '道具用途',
  `purchase_record_limit_times` int(8) DEFAULT 0 NOT NULL COMMENT '购买限制次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`props_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='道具商店';

DROP TABLE IF EXISTS `purchase_record`;
CREATE TABLE `purchase_record` (
  `purchase_record_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '购买记录ID',
  `purchase_user` int(11) DEFAULT 0 COMMENT '购买用户',
  `player_screen_name` varchar(64) COMMENT '玩家网名',
  `prop_name` varchar(64) COMMENT '道具名称',
  `props_picture` varchar(64) COMMENT '道具图片',
  `props_use` varchar(64) COMMENT '道具用途',
  `gold_coins_required` varchar(64) COMMENT '所需金币',
  `use_status` varchar(64) COMMENT '使用状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`purchase_record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购买记录';

DROP TABLE IF EXISTS `game_achievements`;
CREATE TABLE `game_achievements` (
  `game_achievements_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '游戏成就ID',
  `achievement_name` varchar(64) COMMENT '成就名称',
  `type_of_achievement` varchar(64) COMMENT '成就类型',
  `score_settings` double(8,2) DEFAULT 0 COMMENT '分数设置',
  `gold_coin_rewards` varchar(64) COMMENT '金币奖励',
  `achievements_limit_times` int(8) DEFAULT 0 NOT NULL COMMENT '发放限制次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`game_achievements_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏成就';

DROP TABLE IF EXISTS `achievements`;
CREATE TABLE `achievements` (
  `achievements_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '玩家成就ID',
  `game_user` int(11) DEFAULT 0 COMMENT '游戏玩家',
  `player_screen_name` varchar(64) COMMENT '玩家网名',
  `achievement_name` varchar(64) COMMENT '成就名称',
  `gold_coin_rewards` varchar(64) COMMENT '金币奖励',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`achievements_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家成就';

-- ==================== 勋章表 ====================
DROP TABLE IF EXISTS `medals`;
CREATE TABLE `medals` (
  `medal_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '勋章ID',
  `medal_name` varchar(64) NOT NULL COMMENT '勋章名称',
  `medal_code` varchar(32) NOT NULL UNIQUE COMMENT '勋章代码',
  `medal_icon` varchar(255) COMMENT '勋章图标',
  `medal_description` text COMMENT '勋章描述',
  `unlock_condition` varchar(255) COMMENT '解锁条件描述',
  `track_id` int(11) DEFAULT 0 COMMENT '关联赛道ID（0表示全赛道）',
  `level_id` int(11) DEFAULT 0 COMMENT '关联关卡ID（0表示不关联特定关卡）',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序顺序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`medal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='勋章表';

-- 示例勋章数据
INSERT INTO `medals` VALUES 
(1, '结绳新手', 'rope_beginner', '/static/medals/rope_beginner.png', '完成结绳计数赛道第一关', '完成结绳计数赛道第一关', 1, 0, 1, NOW(), NOW()),
(2, '筹算达人', 'counting_rods_master', '/static/medals/counting_rods_master.png', '完成筹算赛道所有关卡', '完成筹算赛道所有关卡', 2, 0, 2, NOW(), NOW()),
(3, '珠算高手', 'abacus_expert', '/static/medals/abacus_expert.png', '完成珠算赛道所有关卡', '完成珠算赛道所有关卡', 3, 0, 3, NOW(), NOW()),
(4, '全能冠军', 'all_champion', '/static/medals/all_champion.png', '完成所有赛道', '完成所有赛道', 0, 0, 4, NOW(), NOW());

-- ==================== 玩家勋章表 ====================
DROP TABLE IF EXISTS `player_medals`;
CREATE TABLE `player_medals` (
  `player_medal_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '玩家勋章ID',
  `gamer_id` int(11) NOT NULL COMMENT '玩家ID',
  `medal_id` int(11) NOT NULL COMMENT '勋章ID',
  `unlock_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '解锁时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`player_medal_id`),
  UNIQUE KEY `uk_gamer_medal` (`gamer_id`, `medal_id`),
  INDEX `idx_gamer` (`gamer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家勋章表';

-- ==================== 文化科普内容表 ====================
DROP TABLE IF EXISTS `culture_tips`;
CREATE TABLE `culture_tips` (
  `tip_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '科普内容ID',
  `track_id` int(11) NOT NULL COMMENT '关联赛道ID',
  `level_id` int(11) DEFAULT 0 COMMENT '关联关卡ID（0表示赛道通用）',
  `tip_title` varchar(128) COMMENT '科普标题',
  `tip_content` text COMMENT '科普内容（一句话）',
  `tip_image` varchar(255) COMMENT '配图路径',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序顺序',
  `is_enabled` tinyint(1) DEFAULT 1 COMMENT '是否启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`tip_id`),
  INDEX `idx_track_level` (`track_id`, `level_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文化科普内容表';

-- 示例科普内容
INSERT INTO `culture_tips` VALUES 
(1, 1, 0, '结绳计数', '结绳计数是古代最早的计数方式之一，通过绳结的数量和位置来记录数字', '/static/culture/rope_tip.png', 1, 1, NOW(), NOW()),
(2, 2, 0, '筹算', '算筹是中国古代重要的计算工具，用不同长度的棍子表示不同的数字', '/static/culture/counting_rods_tip.png', 1, 1, NOW(), NOW()),
(3, 3, 0, '珠算', '算盘是古代最先进的计算工具，至今仍在一些地方使用', '/static/culture/abacus_tip.png', 1, 1, NOW(), NOW());

DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `comment_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `user_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '评论人ID',
  `reply_to_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '回复评论ID',
  `content` longtext CHARACTER SET utf8 COMMENT '内容',
  `nickname` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '头像地址',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `source_table` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '来源表',
  `source_field` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '来源字段',
  `source_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '来源ID',
  PRIMARY KEY (`comment_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='评论';

DROP TABLE IF EXISTS `praise`;
CREATE TABLE `praise` (
  `praise_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '点赞人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `source_table` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '来源表',
  `source_field` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '来源字段',
  `source_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '来源ID',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '点赞状态:1为点赞，0已取消',
  PRIMARY KEY (`praise_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='点赞';

DROP TABLE IF EXISTS `hits`;
CREATE TABLE `hits` (
  `hits_id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '点击记录ID',
  `user_id` int(11) unsigned NOT NULL DEFAULT '0' COMMENT '点击用户ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `source_table` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '来源表',
  `source_field` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '来源字段',
  `source_id` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '来源ID',
  PRIMARY KEY (`hits_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户点击';
