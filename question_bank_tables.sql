

-- ==================== 题目库表 ====================
DROP TABLE IF EXISTS `question_bank`;
CREATE TABLE `question_bank` (
  `question_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `track_id` int(11) COMMENT '所属赛道ID（1-结绳计数，2-筹算，3-珠算）',
  `level_id` int(11) COMMENT '所属关卡ID（关联 game_levels.game_levels_id）',
  `question_type` varchar(32) COMMENT '题目类型（rope_mark-绳结标记，rope_color-绳结颜色，rope_shape-绳结形状，counting_rods_basic-筹算基础，counting_rods_position-筹算位值，counting_rods_add-筹算加法，counting_rods_sub-筹算减法，counting_rods_mix-筹算混合，abacus_basic-珠算基础，abacus_digit-珠算拨数，abacus_add-珠算加法，abacus_sub-珠算减法，abacus_mix-珠算混合）',
  `question_title` varchar(255) COMMENT '题目标题/描述',
  `question_content` text COMMENT '题目内容（JSON格式，存储题目详细配置）',
  `correct_answer` text COMMENT '正确答案（JSON格式，存储标准答案）',
  `difficulty` int(11) DEFAULT 1 COMMENT '难度等级（1-简单，2-中等，3-困难）',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序顺序',
  `is_enabled` tinyint(1) DEFAULT 1 COMMENT '是否启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`question_id`),
  INDEX `idx_track_level` (`track_id`, `level_id`),
  INDEX `idx_question_type` (`question_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目库表';

-- ==================== 玩家答题记录表 ====================
DROP TABLE IF EXISTS `player_question_record`;
CREATE TABLE `player_question_record` (
  `record_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `gamer_id` int(11) COMMENT '玩家ID',
  `question_id` int(11) COMMENT '题目ID',
  `level_id` int(11) COMMENT '关卡ID',
  `track_id` int(11) COMMENT '赛道ID',
  `user_answer` text COMMENT '玩家答案（JSON格式）',
  `is_correct` tinyint(1) DEFAULT 0 COMMENT '是否正确（0-错误，1-正确）',
  `answer_time` double(8,2) DEFAULT 0 COMMENT '答题耗时（秒）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '答题时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  INDEX `idx_gamer_question` (`gamer_id`, `question_id`),
  INDEX `idx_gamer_level` (`gamer_id`, `level_id`),
  INDEX `idx_gamer_track` (`gamer_id`, `track_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家答题记录表';

-- ==================== 关卡星级记录表 ====================
DROP TABLE IF EXISTS `level_star_record`;
CREATE TABLE `level_star_record` (
  `star_record_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '星级记录ID',
  `gamer_id` int(11) COMMENT '玩家ID',
  `level_id` int(11) COMMENT '关卡ID',
  `track_id` int(11) COMMENT '赛道ID',
  `total_stars` int(11) DEFAULT 0 COMMENT '累计获得星级（0-3星）',
  `best_time` double(8,2) DEFAULT 999999 COMMENT '最佳完成时间（秒）',
  `best_stars` int(11) DEFAULT 0 COMMENT '单次最佳星级',
  `complete_count` int(11) DEFAULT 0 COMMENT '完成次数',
  `last_complete_time` datetime COMMENT '最后完成时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`star_record_id`),
  UNIQUE KEY `uk_gamer_level` (`gamer_id`, `level_id`),
  INDEX `idx_gamer_track` (`gamer_id`, `track_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关卡星级记录表';

-- ==================== 初始化结绳计数第一关题目 ====================
-- 第一关：用 "绳结 + 特殊装饰" 做标记
-- 题目类型：rope_mark（绳结标记）

INSERT INTO `question_bank` (`track_id`, `level_id`, `question_type`, `question_title`, `question_content`, `correct_answer`, `difficulty`, `sort_order`) VALUES
-- 题目1：认识数字1的绳结表示
(1, 1, 'rope_mark', '今天学习用绳结表示数字', 
'{"options":["数字1的绳结怎么表示呀？"]}',
'{"answer":"1"}', 1, 1),

-- 题目2：认识数字2的绳结表示
(1, 1, 'rope_mark', '今天学习用绳结表示数字', 
'{"options":["数字2的绳结怎么表示呀？"]}',
'{"answer":"2"}', 1, 2),

-- 题目3：认识数字3的绳结表示
(1, 1, 'rope_mark', '今天学习用绳结表示数字', 
'{"options":["数字3的绳结怎么表示呀？"]}',
'{"answer":"3"}', 1, 3),

-- 题目4：认识数字5的绳结表示
(1, 1, 'rope_mark', '今天学习用绳结表示数字', 
'{"options":["数字4的绳结怎么表示呀？"]}',
'{"answer":"4"}', 1, 4),

-- 题目5：数字和绳结数量匹配（3）
(1, 1, 'rope_mark', '今天学习用绳结表示数字', 
'{"options":["数字5的绳结怎么表示呀？"]}',
'{"answer":"5"}', 1, 5),

-- 题目6：数字和绳结数量匹配（4）
(1, 1, 'rope_mark', '今天学习用绳结表示数字', 
'{"options":["数字6的绳结怎么表示呀？"]}',
'{"answer":"6"}', 1, 6),

-- 题目7：数字和绳结数量匹配（7）
(1, 1, 'rope_mark', '今天学习用绳结表示数字', 
'{"options":["数字7的绳结怎么表示呀？"]}',
'{"answer":"7"}', 1, 7),

-- 题目8：数字和绳结数量匹配（10）
(1, 1, 'rope_mark', '今天学习用绳结表示数字', 
'{"options":["数字8的绳结怎么表示呀？"]}',
'{"answer":"8"}', 1, 8),

-- 题目9：判断题 - 绳结数量是否表示正确的数字
(1, 1, 'rope_mark', '今天学习用绳结表示数字', 
'{"options":["数字9的绳结怎么表示呀？"]}',
'{"answer":"9"}', 1, 9),

-- 题目10：综合题 - 选择所有正确的“数字-绳结数量”对应
(1, 1, 'rope_mark', '今天学习用绳结表示数字', 
'{"options":["数字10的绳结怎么表示呀？"]}',
'{"answer":"10"}', 1, 10);
