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
 * 游戏关卡：(GameLevels)表实体类
 *
 */
@TableName("`game_levels`")
@Data
@EqualsAndHashCode(callSuper = false)
public class GameLevels implements Serializable {

    // GameLevels编号
    @TableId(value = "game_levels_id", type = IdType.AUTO)
    private Integer game_levels_id;

    // 所属赛道ID
    @TableField(value = "`track_id`")
    private Integer track_id;

    // 关卡顺序
    @TableField(value = "`level_order`")
    private Integer level_order;

    // 关卡名称
    @TableField(value = "`level_name`")
    private String level_name;
    // 关卡图片
    @TableField(value = "`level_picture`")
    private String level_picture;
    // 限制步数
    @TableField(value = "`limit_number_of_steps`")
    private Double limit_number_of_steps;
    // 消除数量
    @TableField(value = "`elimination_quantity`")
    private Double elimination_quantity;
    // 游戏时长
    @TableField(value = "`game_duration`")
    private Double game_duration;
    // 金币奖励
    @TableField(value = "`gold_coin_rewards`")
    private Double gold_coin_rewards;
    // 关卡说明
    @TableField(value = "`level_description`")
    private String level_description;




    // 评论数
    @TableField(value = "comment_len")
    private Integer comment_len;















		// 开始游戏限制次数
	@TableField(value = "game_record_limit_times")
	private String game_record_limit_times;
	
			


	// 更新时间
    @TableField(value = "update_time")
    private Timestamp update_time;

    // 创建时间
    @TableField(value = "create_time")
    private Timestamp create_time;

}
