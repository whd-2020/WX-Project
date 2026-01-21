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
 * 玩家成就：(Achievements)表实体类
 *
 */
@TableName("`achievements`")
@Data
@EqualsAndHashCode(callSuper = false)
public class Achievements implements Serializable {

    // Achievements编号
    @TableId(value = "achievements_id", type = IdType.AUTO)
    private Integer achievements_id;

    // 游戏玩家
    @TableField(value = "`game_user`")
    private Integer game_user;
    // 玩家网名
    @TableField(value = "`player_screen_name`")
    private String player_screen_name;
    // 成就名称
    @TableField(value = "`achievement_name`")
    private String achievement_name;
    // 金币奖励
    @TableField(value = "`gold_coin_rewards`")
    private String gold_coin_rewards;




















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
