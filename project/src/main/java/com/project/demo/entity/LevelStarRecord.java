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
 * 关卡星级记录：(LevelStarRecord)表实体类
 */
@TableName("`level_star_record`")
@Data
@EqualsAndHashCode(callSuper = false)
public class LevelStarRecord implements Serializable {

    // 星级记录ID
    @TableId(value = "star_record_id", type = IdType.AUTO)
    private Integer star_record_id;

    // 玩家ID
    @TableField(value = "`gamer_id`")
    private Integer gamer_id;

    // 关卡ID
    @TableField(value = "`level_id`")
    private Integer level_id;

    // 赛道ID
    @TableField(value = "`track_id`")
    private Integer track_id;

    // 累计获得星级
    @TableField(value = "`total_stars`")
    private Integer total_stars;

    // 最佳完成时间（秒）
    @TableField(value = "`best_time`")
    private Double best_time;

    // 单次最佳星级
    @TableField(value = "`best_stars`")
    private Integer best_stars;

    // 完成次数
    @TableField(value = "`complete_count`")
    private Integer complete_count;

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
