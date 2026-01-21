package com.project.demo.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * 游戏记录：(GameRecord)表实体类
 *
 */
@TableName("`game_record`")
@Data
@EqualsAndHashCode(callSuper = false)
public class GameRecord implements Serializable {

    // GameRecord编号
    @TableId(value = "game_record_id", type = IdType.AUTO)
    private Integer game_record_id;

    // 关卡名称
    @TableField(value = "`level_name`")
    private String level_name;
    // 游戏玩家
    @TableField(value = "`gamer`")
    private Integer gamer;
    // 玩家头像
    @TableField(value = "`player_avatar`")
    private String player_avatar;
    // 玩家网名
    @TableField(value = "`player_screen_name`")
    private String player_screen_name;
    // 金币奖励
    @TableField(value = "`gold_coin_rewards`")
    private Double gold_coin_rewards;
    // 游戏时间
    @TableField(value = "`game_time`")
    private Double game_time;
    // 使用步数
    @TableField(value = "`number_of_steps_used`")
    private Double number_of_steps_used;
    // 游戏总分
    @TableField(value = "`total_game_score`")
    private Double total_game_score;
    // 通关状态
    @TableField(value = "`clearance_status`")
    private String clearance_status;




















				// 来源表
	@TableField(value = "source_table")
	private String source_table;
	
	// 来源ID
	@TableField(value = "source_id")
	private Integer source_id;
	
	// 来源用户ID
	@TableField(value = "source_user_id")
	private Integer source_user_id;
	


	// 更新时间
    @TableField(value = "update_time")
    private Timestamp update_time;

    // 创建时间
    @TableField(value = "create_time")
    private Timestamp create_time;

}
