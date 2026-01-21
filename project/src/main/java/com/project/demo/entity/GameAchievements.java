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
 * 游戏成就：(GameAchievements)表实体类
 *
 */
@TableName("`game_achievements`")
@Data
@EqualsAndHashCode(callSuper = false)
public class GameAchievements implements Serializable {

    // GameAchievements编号
    @TableId(value = "game_achievements_id", type = IdType.AUTO)
    private Integer game_achievements_id;

    // 成就名称
    @TableField(value = "`achievement_name`")
    private String achievement_name;
    // 成就类型
    @TableField(value = "`type_of_achievement`")
    private String type_of_achievement;
    // 分数设置
    @TableField(value = "`score_settings`")
    private Double score_settings;
    // 金币奖励
    @TableField(value = "`gold_coin_rewards`")
    private String gold_coin_rewards;



















		// 发放限制次数
	@TableField(value = "achievements_limit_times")
	private String achievements_limit_times;
	
			


	// 更新时间
    @TableField(value = "update_time")
    private Timestamp update_time;

    // 创建时间
    @TableField(value = "create_time")
    private Timestamp create_time;

}
