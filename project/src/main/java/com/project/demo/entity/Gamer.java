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
 * 游戏玩家：(Gamer)表实体类
 *
 */
@TableName("`gamer`")
@Data
@EqualsAndHashCode(callSuper = false)
public class Gamer implements Serializable {

    // Gamer编号
    @TableId(value = "gamer_id", type = IdType.AUTO)
    private Integer gamer_id;

    // 玩家网名
    @TableField(value = "`player_screen_name`")
    private String player_screen_name;

    // 金币余额
    @TableField(value = "`gold_coin_balance`")
    private Double gold_coin_balance;

    // 审核状态
    @TableField(value = "`examine_state`")
    private String examine_state;

    // 用户编号
    @TableField(value = "user_id")
    private Integer userId;











			


	// 更新时间
    @TableField(value = "update_time")
    private Timestamp update_time;

    // 创建时间
    @TableField(value = "create_time")
    private Timestamp create_time;

}
