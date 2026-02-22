package com.project.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 玩家关卡解锁状态：(PlayerLevelUnlock)表实体类
 */
@TableName("`player_level_unlock`")
@Data
@EqualsAndHashCode(callSuper = false)
public class PlayerLevelUnlock implements Serializable {

    // 解锁记录ID
    @TableId(value = "unlock_id", type = IdType.AUTO)
    private Integer unlock_id;

    // 玩家ID
    @TableField(value = "`gamer_id`")
    private Integer gamer_id;

    // 关卡ID
    @TableField(value = "`game_levels_id`")
    private Integer game_levels_id;

    // 赛道ID
    @TableField(value = "`track_id`")
    private Integer track_id;

    // 是否已解锁
    @TableField(value = "`is_unlocked`")
    private Integer is_unlocked;

    // 是否已完成
    @TableField(value = "`is_completed`")
    private Integer is_completed;

    // 最佳分数
    @TableField(value = "`best_score`")
    private Double best_score;

    // 完成次数
    @TableField(value = "`complete_times`")
    private Integer complete_times;

    // 首次完成时间
    @TableField(value = "`first_complete_time`")
    private Timestamp first_complete_time;

    // 最后完成时间
    @TableField(value = "`last_complete_time`")
    private Timestamp last_complete_time;

    // 创建时间
    @TableField(value = "`create_time`")
    private Timestamp create_time;

    // 更新时间
    @TableField(value = "`update_time`")
    private Timestamp update_time;
}
